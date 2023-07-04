package com.example.tripadvisor.model;
import java.time.Duration;
import java.util.List;

public class Transport extends Content {

    private String name;
    private float cost;
    private String start;
    private String destination;
    private Duration duration;

    public Transport(String name, float cost, String start, String destination, Duration duration) {
        this.name = name;
        this.cost = cost;
        this.start = start;
        this.destination = destination;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public float getCost() {
        return cost;
    }

    public String getStart() {
        return start;
    }

    public String getDestination() {
        return destination;
    }

    public Duration getDuration() {
        return duration;
    }
}