package com.example.tripadvisor.model;
import java.time.Duration;
import java.util.List;

public class Transport extends Content {

    private String name;
    private String start;
    private String destination;
    private Duration duration;

    public Transport(String name, String start, String destination, Duration duration) {
        this.name = name;
        this.start = start;
        this.destination = destination;
        this.duration = duration;
    }

    public String getName() {
        return name;
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