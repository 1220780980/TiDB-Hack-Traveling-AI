package com.example.tripadvisor.controller;

import com.example.tripadvisor.dataAccessObject.AttractionGetter;
import com.example.tripadvisor.dataAccessObject.PlanGetter;
import com.example.tripadvisor.dataAccessObject.TransportationGetter;
import com.example.tripadvisor.model.*;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
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

        int days = (int) ChronoUnit.DAYS.between(LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE),
                                            LocalDate.parse(leaveDate, DateTimeFormatter.ISO_DATE)) + 1;

        List<DayPlan> dayPlanList = new ArrayList<>();
        boolean newDay = true;
        List<Content> contents = new ArrayList<>();
        for (int i = 1; i <= days; i++) {
            List<Row> rows = new ArrayList<>();
            if (newDay) {
                contents = new ArrayList<>();
                for (Attraction a : attractions) {
                    int occurrence = findTotalOccurrence(a, plans);
                    boolean WeatherIndoorOutdoor = a.isIndoor() | weatherList.get(i - 1);
                    Row row = new Row(a.getNum(), occurrence, 0,
                                      Duration.ofMinutes(0), a.getRecommendDuration(),
                                      WeatherIndoorOutdoor);
                    rows.add(row);
                }
                int best = findBestAttractionNewDay(rows);
                Attraction bestAttraction = findAttraction(attractions, best);
                contents.add(bestAttraction);
                attractions.removeIf(a -> bestAttraction.getNum() == a.getNum());
                newDay = false;
            } else {
                String path = System.getProperty("user.dir") + "/backend/src/main/java/com/example/tripadvisor/trained";
                Model decision_tree = Model.fromFile(path + "/decision_tree_model.pmml");
                Model knn = Model.fromFile(path + "/knn_model.pmml");
                Model logistic_regression = Model.fromFile(path + "/logistic_regression_model.pmml");
                Model random_forest = Model.fromFile(path + "/random_forest_model.pmml");
                Model svm = Model.fromFile(path + "/svm_model.pmml");
                String[] inputNames = decision_tree.inputNames();
                Map<String, Object> result = decision_tree.predict(new HashMap<String, Object>() {{
                    put("total_occurrence", 0.8);
                    put("neighbour_occurrence", 0.7);
                    put("transportation_time", 10.0);
                }});
                System.out.println(result);
                result = knn.predict(new HashMap<String, Object>() {{
                    put("total_occurrence", 0.8);
                    put("neighbour_occurrence", 0.7);
                    put("transportation_time", 10.0);
                }});
                System.out.println(result);
                result = logistic_regression.predict(new HashMap<String, Object>() {{
                    put("total_occurrence", 0.8);
                    put("neighbour_occurrence", 0.7);
                    put("transportation_time", 10.0);
                }});
                System.out.println(result);
                result = random_forest.predict(new HashMap<String, Object>() {{
                    put("total_occurrence", 0.8);
                    put("neighbour_occurrence", 0.7);
                    put("transportation_time", 10.0);
                }});
                System.out.println(result);
                result = svm.predict(new HashMap<String, Object>() {{
                    put("total_occurrence", 0.8);
                    put("neighbour_occurrence", 0.7);
                    put("transportation_time", 10.0);
                }});
                System.out.println(result);
                // TODO: similar to above
                newDay = true;
            }

            DayPlan dayPlan = new DayPlan(i, contents);
            dayPlanList.add(dayPlan);
        }

        ResultPlan resultPlan = new ResultPlan(dayPlanList);

        return ResponseEntity.status(HttpStatus.OK).body(resultPlan);
    }

    private Attraction findAttraction(List<Attraction> attractions, int best) {
        for (Attraction a : attractions) {
            if (a.getNum() == best) {
                return a;
            }
        }
        return null;
    }

    private int findBestAttractionNewDay(List<Row> rows) {
        int largestOccurrence = 0;
        int bestAttraction = -1;
        for (Row r : rows) {
            if (r.getWeatherIndoorOutdoor() && (r.getTotalOccurrence() > largestOccurrence)) {
                largestOccurrence = r.getTotalOccurrence();
                bestAttraction = r.getAttractions();
            }
        }
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
