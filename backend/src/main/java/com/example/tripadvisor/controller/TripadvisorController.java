package com.example.tripadvisor.controller;

import com.example.tripadvisor.dataAccessObject.Fortune500Dao;
import com.example.tripadvisor.model.Company;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Controller
public class TripadvisorController {

//    private final Fortune500Dao fortune500Dao;
//
//    @Autowired
//    public TripadvisorController(Fortune500Dao fortune500Dao) {
//        this.fortune500Dao = fortune500Dao;
//    }

    @GetMapping(value="/MainPage")
    public String MainPage() {
        return "MainPage";
    }

    @PostMapping("/MainPage")
    public void handlePostRequest(@RequestParam("country") String country, @RequestParam("city") String city,
                                  @RequestParam("startDate") String startDate, @RequestParam("leaveDate") String leaveDate) {
        System.out.println(country);
        System.out.println(city);
        System.out.println(startDate);
        System.out.println(leaveDate);
    }
}
