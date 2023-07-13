package com.example.tripadvisor.model;

import java.time.Duration;

public class Row {
    private int Attractions;
    private int TotalOccurrence;
    private int OccurrenceNextToLast;
    private Duration TransportationTime;
    private Duration AttractionVisitTime;
    private Boolean WeatherIndoorOutdoor;

    public Row(int Attractions, int TotalOccurrence, int OccurrenceNextToLast, Duration TransportationTime, Duration AttractionVisitTime, Boolean WeatherIndoorOutdoor) {
        this.Attractions = Attractions;
        this.TotalOccurrence = TotalOccurrence;
        this.OccurrenceNextToLast = OccurrenceNextToLast;
        this.TransportationTime = TransportationTime;
        this.AttractionVisitTime = AttractionVisitTime;
        this.WeatherIndoorOutdoor = WeatherIndoorOutdoor;
    }

    public int getAttractions() {
        return Attractions;
    }

    public Boolean getWeatherIndoorOutdoor() {
        return WeatherIndoorOutdoor;
    }

    public Duration getAttractionVisitTime() {
        return AttractionVisitTime;
    }

    public Duration getTransportationTime() {
        return TransportationTime;
    }

    public int getOccurrenceNextToLast() {
        return OccurrenceNextToLast;
    }

    public int getTotalOccurrence() {
        return TotalOccurrence;
    }
}
