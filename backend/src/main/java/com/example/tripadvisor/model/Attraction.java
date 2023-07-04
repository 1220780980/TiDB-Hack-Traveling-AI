package com.example.tripadvisor.model;
import java.time.Duration;

public class Attraction extends Content {

    private int num;
    private String name;
    private boolean indoor;
    private Duration recommendDuration;

    public Attraction (int num, String name, boolean indoor, Duration recommendDuration) {
        this.num = num;
        this.name = name;
        this.indoor = indoor;
        this.recommendDuration = recommendDuration;
    }

    public String getName() {
        return name;
    }

    public int getNum() {
        return num;
    }

    public boolean isIndoor() {
        return indoor;
    }

    public Duration getRecommendDuration() {
        return recommendDuration;
    }
}