package com.example.tripadvisor.dataAccessObject;

import com.example.tripadvisor.model.Weather;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import com.jayway.jsonpath.JsonPath;
import com.example.tripadvisor.model.WeatherAPI;

@Repository
public class WeatherAPIGetter {
    private final RestTemplate restTemplate;

    public WeatherAPIGetter() {
        this.restTemplate = new RestTemplate();
    }

    public WeatherAPI getWeatherAPI(String city, String country, LocalDate startDate, LocalDate leaveDate) {
        String url = "https://api.weatherbit.io/v2.0/forecast/daily?&city=" + city + "&country=" + country + "&key=53e98c76f295400283d0adc75be14068";
        String jsonResponse = restTemplate.getForObject(url, String.class);
        List<String> dateTimes = JsonPath.parse(jsonResponse).read("$.data[*].datetime");
        List<Integer> codes = JsonPath.parse(jsonResponse).read("$.data[*].weather.code");
        List<String> WeatherDescription = JsonPath.parse(jsonResponse).read("$.data[*].weather.description");
        HashMap<LocalDate, String> APIDateAndWeatherMap = new HashMap<>();
        HashMap<LocalDate, Boolean> APIGoodWeatherOrNotMap = new HashMap<>();
        Set<Integer> validCodes = new HashSet<>(Arrays.asList(700, 711, 721, 731, 741, 751, 800, 801, 802, 803, 804));
        LocalDate APIStartDate = LocalDate.parse(dateTimes.get(0), DateTimeFormatter.ISO_DATE);
        LocalDate APIEndDate = LocalDate.parse(dateTimes.get(dateTimes.size()-1), DateTimeFormatter.ISO_DATE);

        //create a full API Date list
        List<LocalDate> APIDateTimes = new ArrayList<>();
        while (!APIStartDate.isEqual(APIEndDate)) {
            APIDateTimes.add(APIStartDate);
            APIStartDate = APIStartDate.plusDays(1);
        }
        APIDateTimes.add(APIEndDate);

        //create a GoodWeatherOrNot hashmap with key: APIDate and value: bool (good weather is 1 and o.w. is 0)
        for (int i = 0; i < codes.size(); i++) {
            if (validCodes.contains(codes.get(i))) {
                APIGoodWeatherOrNotMap.put(APIDateTimes.get(i),true);
            } else {
                APIGoodWeatherOrNotMap.put(APIDateTimes.get(i),false);
            }
        }


        //create a hashmap with key: APIDate and value: APIWeather
        for (int i = 0; i < APIDateTimes.size(); i++) {
            APIDateAndWeatherMap.put(APIDateTimes.get(i),WeatherDescription.get(i));
        }

        //create a full Trip Date list
        List<LocalDate> tripDateTimes = new ArrayList<>();
        LocalDate TripStartDate = startDate;
        while (!TripStartDate.isEqual(leaveDate)) {
            tripDateTimes.add(TripStartDate);
            TripStartDate = TripStartDate.plusDays(1);
        }
        tripDateTimes.add(leaveDate);

        //Compare and generate Trip weather list
        List<String> tripWeather = new ArrayList<>();
        for (int i = 0; i < tripDateTimes.size(); i++) {
            LocalDate tripDay = tripDateTimes.get(i);
            if (APIDateAndWeatherMap.containsKey(tripDay)) {
                tripWeather.add(APIDateAndWeatherMap.get(tripDay));
            } else {
                tripWeather.add("Sunny");
            }
        }

        //Compare and generate Trip WeatherGoodOrNot list
        List<Boolean> tripWeatherGoodOrNot = new ArrayList<>();
        for (int i = 0; i < tripDateTimes.size(); i++) {
            LocalDate tripDay = tripDateTimes.get(i);
            if (APIGoodWeatherOrNotMap.containsKey(tripDay)) {
                tripWeatherGoodOrNot.add(APIGoodWeatherOrNotMap.get(tripDay));
            } else {
                tripWeatherGoodOrNot.add(true);
            }
        }

        WeatherAPI result = new WeatherAPI(tripWeather,tripWeatherGoodOrNot);
//         TEST           //
//        System.out.println("tripDateTimes: " + tripDateTimes);
//        System.out.println("tripWeather: " + tripWeather);
//        System.out.println("tripWeatherGoodOrNot: " + tripWeatherGoodOrNot);
//        System.out.println("WeatherDescription: " + WeatherDescription);
//        System.out.println("APIDateTimes: " + APIDateTimes);

        return result;
    }
}
