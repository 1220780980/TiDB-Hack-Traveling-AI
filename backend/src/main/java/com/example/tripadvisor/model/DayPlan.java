package com.example.tripadvisor.model;
import java.util.List;

public class DayPlan {

    private int num;
    private String weather;
    private List<Content> plan;

    public DayPlan(int num, String weather, List<Content> plan) {
        this.weather = weather;
        this.num = num;
        this.plan = plan;
    }

    public int getNum() {
        return num;
    }

    public List<Content> getPlan() {
        return plan;
    }

    public String getWeather() { return weather; }
}