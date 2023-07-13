package com.example.tripadvisor.controller;

import com.example.tripadvisor.dataAccessObject.AttractionGetter;
import com.example.tripadvisor.dataAccessObject.PlanGetter;
import com.example.tripadvisor.dataAccessObject.TransportationGetter;
import com.example.tripadvisor.dataAccessObject.WeatherAPIGetter;
import com.example.tripadvisor.model.Attraction;
import com.example.tripadvisor.model.Transportation;
import com.example.tripadvisor.model.Weather;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.tripadvisor.model.WeatherAPI;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

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

    @PostMapping("/MainPage")
    public void handlePostRequest(@RequestParam("country") String country, @RequestParam("city") String city,
                                  @RequestParam("startDate") String startDate, @RequestParam("leaveDate") String leaveDate,
                                  @RequestParam("daily") String dailyHours) {
        List<Attraction> attractions = attractionGetter.getAttraction(country, city);
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
    }
}
