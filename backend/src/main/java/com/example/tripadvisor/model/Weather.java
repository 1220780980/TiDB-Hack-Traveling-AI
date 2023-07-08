package com.example.tripadvisor.model;

import com.example.tripadvisor.dataAccessObject.WeatherAPIGetter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.List;

public class Weather {

    private final String country;
    private final String city;
    private final LocalDate startDate;
    private final LocalDate leaveDate;

    private final WeatherAPI ListWeather;


    public Weather(String country, String city, String startDate, String leaveDate) {
        this.country = country;
        this.city = city;
        this.startDate = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        this.leaveDate = LocalDate.parse(leaveDate, DateTimeFormatter.ISO_DATE);
        WeatherAPIGetter weatherAPIGetter = new WeatherAPIGetter();
        this.ListWeather = weatherAPIGetter.getWeatherAPI(city, country, this.startDate, this.leaveDate);
    }


    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getLeaveDate() {
        return leaveDate;
    }

    public WeatherAPI getListWeather() {
        return ListWeather;
    }

}