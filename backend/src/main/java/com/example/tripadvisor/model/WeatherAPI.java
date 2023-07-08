package com.example.tripadvisor.model;

import java.util.List;

public class WeatherAPI {
    private final List<String> tripWeather;

    private final List<Boolean> tripWeatherGoodOrNot;

    public WeatherAPI(List<String> tripWeather,List<Boolean> tripWeatherGoodOrNot) {
        this.tripWeather = tripWeather;
        this.tripWeatherGoodOrNot = tripWeatherGoodOrNot;
    }

    public List<String> getTripWeather() {
        return tripWeather;
    }

    public List<Boolean> getTripWeatherGoodOrNot() {
        return tripWeatherGoodOrNot;
    }

}
