package com.example.tripadvisor.model;

import com.example.tripadvisor.model.Attraction;
import com.example.tripadvisor.model.Company;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.json.JSONObject;

public class WeatherGetter {

    private final String country;
    private final String city;
    private final LocalDate startDate;
    private final LocalDate leaveDate;


    public WeatherGetter(String country, String city, String startDate, String leaveDate) {
        this.country = country;
        this.city = city;
        this.startDate = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        this.leaveDate = LocalDate.parse(leaveDate, DateTimeFormatter.ISO_DATE);
    }

    public void getForecastWeather() {
        // TODO
    }
}