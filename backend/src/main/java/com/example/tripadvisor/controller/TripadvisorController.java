package com.example.tripadvisor.controller;

import com.example.tripadvisor.dataAccessObject.AttractionGetter;
import com.example.tripadvisor.dataAccessObject.PlanGetter;
import com.example.tripadvisor.dataAccessObject.TransportationGetter;
import com.example.tripadvisor.model.*;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.json.JSONObject;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.pmml4s.model.Model;

@Controller
public class TripadvisorController {

    private final AttractionGetter attractionGetter;
    private final PlanGetter planGetter;
    private final TransportationGetter transportationGetter;

    @Autowired
    public TripadvisorController(AttractionGetter attractionGetter, PlanGetter planGetter, TransportationGetter transportationGetter) {
        this.attractionGetter = attractionGetter;
        this.planGetter = planGetter;
        this.transportationGetter = transportationGetter;
    }

    @GetMapping(value="/MainPage")
    public String MainPage() {

        return "MainPage";
    }

    @PostMapping(value="/MainPage", produces = "application/json")
    public ResponseEntity<ResultPlan> handlePostRequest(@RequestParam("country") String country, @RequestParam("city") String city,
                                  @RequestParam("startDate") String startDate, @RequestParam("leaveDate") String leaveDate,
                                  @RequestParam("daily") String dailyHours) {
        List<Attraction> attractions = attractionGetter.getAttraction(country, city);
        Duration leftDuration = Duration.ofMinutes(Integer.parseInt(dailyHours) * 60);
        attractions.removeIf(a -> a.getRecommendDuration().toMillis() > leftDuration.toMillis());

        List<String> plansString = planGetter.getPlan(country, city);
        List<Transportation> transportations = transportationGetter.getTransportation(country, city);
        List<JSONObject> plans = new ArrayList<>();
        for (String plan : plansString) {
            JSONObject jsonObject = new JSONObject(plan);
            plans.add(jsonObject);
        }

        Weather weather = new Weather(country, city, startDate, leaveDate);
        WeatherAPI getWeatherResult = weather.getListWeather();
        List<Boolean> weatherList = getWeatherResult.getTripWeatherGoodOrNot();
        List<String> weathers = getWeatherResult.getTripWeather();

        int days = (int) ChronoUnit.DAYS.between(LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE),
                                            LocalDate.parse(leaveDate, DateTimeFormatter.ISO_DATE)) + 1;

        List<DayPlan> dayPlanList = new ArrayList<>();
        boolean newDay = true;
        Attraction bestAttraction = null;
        List<Content> contents;
        int timeLeft = Integer.parseInt(dailyHours) * 60;
        String path = System.getProperty("user.dir") + "/backend/src/main/java/com/example/tripadvisor/trained";
        Model decision_tree = Model.fromFile(path + "/decision_tree_model.pmml");
        Model logistic_regression = Model.fromFile(path + "/logistic_regression_model.pmml");
        Model random_forest = Model.fromFile(path + "/random_forest_model.pmml");
        Model svm = Model.fromFile(path + "/svm_model.pmml");

        for (int i = 1; i <= days; i++) {
            contents = new ArrayList<>();

            while (timeLeft > 0) {
                List<Row> rows = new ArrayList<>();

                if (newDay) {
                    for (Attraction a : attractions) {
                        int occurrence = findTotalOccurrence(a, plans);
                        boolean WeatherIndoorOutdoor = a.isIndoor() | weatherList.get(i - 1);
                        Row row = new Row(a.getNum(), occurrence, 0,
                                Duration.ofMinutes(0), a.getRecommendDuration(),
                                WeatherIndoorOutdoor);
                        rows.add(row);
                    }
                    int best = findBestAttractionNewDay(rows, timeLeft, attractions);
                    bestAttraction = findAttraction(attractions, best);
                    if (bestAttraction == null) {
                        break;
                    }
                    contents.add(bestAttraction);
                    Attraction finalBestAttraction = bestAttraction;
                    attractions.removeIf(a -> finalBestAttraction.getNum() == a.getNum());
                    timeLeft = timeLeft - (int) bestAttraction.getRecommendDuration().toMinutes();
                    newDay = false;
                } else {
                    for (Attraction a : attractions) {
                        int occurrence = findTotalOccurrence(a, plans);
                        int neighbourTimes = findNeighbourTimes(a, bestAttraction, plans);
                        Duration transportationTime = findTransportationTime(a, bestAttraction, transportations);
                        boolean WeatherIndoorOutdoor = a.isIndoor() | weatherList.get(i - 1);
                        Row row = new Row(a.getNum(), occurrence, neighbourTimes,
                                transportationTime, a.getRecommendDuration(),
                                WeatherIndoorOutdoor);
                        rows.add(row);
                    }
                    int best = findBestAttraction(rows, plans.size(), decision_tree, logistic_regression, random_forest, svm,
                            attractions, bestAttraction, transportations, timeLeft);
                    Attraction nextBestAttraction = findAttraction(attractions, best);
                    if (nextBestAttraction == null) {
                        break;
                    }
                    Transportation transportation = findTransportation(bestAttraction, nextBestAttraction, transportations);
                    Transport transport = new Transport(transportation.getDetail(), bestAttraction.getName(), nextBestAttraction.getName(), transportation.getDuration());
                    contents.add(transport);
                    contents.add(nextBestAttraction);
                    attractions.removeIf(a -> nextBestAttraction.getNum() == a.getNum());
                    timeLeft = timeLeft - (int) nextBestAttraction.getRecommendDuration().toMinutes();
                    timeLeft = timeLeft - (int) transportation.getDuration().toMinutes();
                    bestAttraction = nextBestAttraction;
                }
            }

            newDay = true;
            timeLeft = Integer.parseInt(dailyHours) * 60;
            DayPlan dayPlan = new DayPlan(i, weathers.get(i - 1), contents);
            dayPlanList.add(dayPlan);
        }

        ResultPlan resultPlan = new ResultPlan(dayPlanList);

        return ResponseEntity.status(HttpStatus.OK).body(resultPlan);
    }

    private Transportation findTransportation(Attraction bestAttraction, Attraction nextBestAttraction, List<Transportation> transportations) {
        for (Transportation t : transportations) {
            if ((t.getAttraction1() == nextBestAttraction.getNum()) && (t.getAttraction2() == bestAttraction.getNum())) {
                return t;
            }
            if ((t.getAttraction2() == nextBestAttraction.getNum()) && (t.getAttraction1() == bestAttraction.getNum())) {
                return t;
            }
        }
        return null;
    }

    private int findBestAttraction(List<Row> rows, int size, Model decisionTree, Model logisticRegression, Model randomForest, Model svm,
                                   List<Attraction> attractions, Attraction bestAttraction, List<Transportation> transportations, int timeLeft) {
        int largestScore = -1;
        int best = -1;
        Row bestRow = null;
        for (Row r : rows) {
            int temp = timeLeft;
            Attraction nextBestAttraction = findAttraction(attractions, r.getAttractions());
            Transportation transportation = findTransportation(bestAttraction, nextBestAttraction, transportations);
            temp = temp - (int) nextBestAttraction.getRecommendDuration().toMinutes();
            temp = temp - (int) transportation.getDuration().toMinutes();
            if (temp < 0) {
                continue;
            }
            HashMap<String, Object> input = new HashMap<String, Object>() {{
                put("total_occurrence", (float) r.getTotalOccurrence() / (float) size);
                put("neighbour_occurrence", (float) r.getOccurrenceNextToLast() / (float) r.getTotalOccurrence());
                put("transportation_time", r.getTransportationTime().toMinutes());
            }};
            int decisionTree_result = getPredictedValue(decisionTree.predict(input));
            int logisticRegression_result = getPredictedValue(logisticRegression.predict(input));
            int randomForest_result = getPredictedValue(randomForest.predict(input));
            long svm_result = (long) svm.predict(input).get("predicted_score");

            int score = vote(decisionTree_result, logisticRegression_result, randomForest_result, (int) svm_result);
            if (!r.getWeatherIndoorOutdoor()) {
                score = score - 1;
            }
            if (score > largestScore) {
                largestScore = score;
                best = r.getAttractions();
                bestRow = r;
            } else if (score == largestScore) {
                Row attraction = findAttractionInRows(rows, best);
                if (r.getTransportationTime().toMinutes() < attraction.getTransportationTime().toMinutes()) {
                    best = r.getAttractions();
                    bestRow = r;
                } else if (r.getTransportationTime().toMinutes() == attraction.getTransportationTime().toMinutes()) {
                    if (r.getTotalOccurrence() > attraction.getTotalOccurrence()) {
                        best = r.getAttractions();
                        bestRow = r;
                    }
                }
            }
        }
        rows.remove(bestRow);
        return best;
    }

    private int vote(int num1, int num2, int num3, int num4) {
        if (num1 != num2 && num1 != num3 && num1 != num4 && num2 != num3 && num2 != num4 && num3 != num4) {
            return (num1 + num2 + num3 + num4) / 4;
        } else {
            int[] nums = {num1, num2, num3, num4};
            int maxCount = 0;
            int mode = 0;

            for (int num : nums) {
                int count = 0;
                for (int n : nums) {
                    if (num == n) {
                        count++;
                    }
                }

                if (count > maxCount) {
                    maxCount = count;
                    mode = num;
                }
            }
            return mode;
        }
    }

    private Row findAttractionInRows(List<Row> rows, int bestAttraction) {
        for (Row r : rows) {
            if (r.getAttractions() == bestAttraction) {
                return r;
            }
        }
        return null;
    }

    private int getPredictedValue(Map<String, Object> predict) {
        double largestProbability = -1;
        int result = -1;
        for (String key : predict.keySet()) {
            double value = (double) predict.get(key);
            if (value > largestProbability) {
                largestProbability = value;
                result = Integer.parseInt(key);
            }
        }
        return result;
    }

    private Duration findTransportationTime(Attraction attraction, Attraction bestAttraction, List<Transportation> transportations) {
        for (Transportation t : transportations) {
            if ((t.getAttraction1() == attraction.getNum()) && (t.getAttraction2() == bestAttraction.getNum())) {
                return t.getDuration();
            }
            if ((t.getAttraction2() == attraction.getNum()) && (t.getAttraction1() == bestAttraction.getNum())) {
                return t.getDuration();
            }
        }
        return null;
    }

    private int findNeighbourTimes(Attraction attraction, Attraction bestAttraction, List<JSONObject> plans) {
        int count = 0;
        for (JSONObject plan : plans) {
            for (String key : plan.keySet()) {
                JSONArray jsonArray = plan.getJSONArray(key);
                int index1 = -10;
                int index2 = -10;
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (attraction.getNum() == jsonArray.getInt(i)) {
                        index1 = i;
                    }
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (bestAttraction.getNum() == jsonArray.getInt(i)) {
                        index2 = i;
                    }
                }
                if ((index1 == index2 + 1) || (index1 == index2 - 1)) {
                    count++;
                }
            }
        }
        return count;
    }

    private Attraction findAttraction(List<Attraction> attractions, int best) {
        for (Attraction a : attractions) {
            if (a.getNum() == best) {
                return a;
            }
        }
        return null;
    }

    private int findBestAttractionNewDay(List<Row> rows, int timeLeft, List<Attraction> attractions) {
        int largestOccurrence = 0;
        int bestAttraction = -1;
        Row bestRow = null;
        for (Row r : rows) {
            Attraction a = findAttraction(attractions, r.getAttractions());
            if (r.getWeatherIndoorOutdoor() && (r.getTotalOccurrence() > largestOccurrence) && (a.getRecommendDuration().toMinutes() <= timeLeft)) {
                largestOccurrence = r.getTotalOccurrence();
                bestAttraction = r.getAttractions();
                bestRow = r;
            }
        }
        rows.remove(bestRow);
        return bestAttraction;
    }

    private int findTotalOccurrence(Attraction attraction, List<JSONObject> plans) {
        int count = 0;
        for (JSONObject plan : plans) {
            for (String key : plan.keySet()) {
                JSONArray jsonArray = plan.getJSONArray(key);
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (attraction.getNum() == jsonArray.getInt(i)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
