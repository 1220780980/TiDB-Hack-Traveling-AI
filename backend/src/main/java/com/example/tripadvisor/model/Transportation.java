package com.example.tripadvisor.model;

import java.time.Duration;

public class Transportation {

    private int Attraction1;
    private int Attraction2;
    private Duration duration;
    private String detail;

    public Transportation(int Attraction1, int Attraction2, Duration duration, String detail) {
        this.Attraction1 = Attraction1;
        this.Attraction2 = Attraction2;
        this.duration = duration;
        this.detail = detail;
    }

    public Duration getDuration() {
        return duration;
    }

    public int getAttraction1() {
        return Attraction1;
    }

    public int getAttraction2() {
        return Attraction2;
    }

    public String getDetail() {
        return detail;
    }
}
