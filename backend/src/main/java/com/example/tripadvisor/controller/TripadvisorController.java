package com.example.tripadvisor.controller;

import com.example.tripadvisor.dataAccessObject.AttractionGetter;
import com.example.tripadvisor.dataAccessObject.PlanGetter;
import com.example.tripadvisor.model.Attraction;
import com.example.tripadvisor.model.WeatherGetter;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

@Controller
public class TripadvisorController {

    private final AttractionGetter attractionGetter;
    private final PlanGetter planGetter;

    @Autowired
    public TripadvisorController(AttractionGetter attractionGetter, PlanGetter planGetter) {
        this.attractionGetter = attractionGetter;
        this.planGetter = planGetter;
    }

    @GetMapping(value="/MainPage")
    public String MainPage() {
        return "MainPage";
    }

    @PostMapping("/MainPage")
    public void handlePostRequest(@RequestParam("country") String country, @RequestParam("city") String city,
                                  @RequestParam("startDate") String startDate, @RequestParam("leaveDate") String leaveDate) {
        List<Attraction> attractions = attractionGetter.getAttraction(country, city);
        List<String> plansString = planGetter.getPlan(country, city);
        List<JSONObject> plans = new ArrayList<>();
        for (String plan : plansString) {
            JSONObject jsonObject = new JSONObject(plan);
            plans.add(jsonObject);
        }
        // TODO
        WeatherGetter weatherGetter = new WeatherGetter(country, city, startDate, leaveDate);
    }
}