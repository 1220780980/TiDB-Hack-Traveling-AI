package com.example.tripadvisor.model;
import java.util.List;

public class ResultPlan {

    private List<DayPlan> plan;

    public ResultPlan(List<DayPlan> plan) {
        this.plan = plan;
    }

    public List<DayPlan> getPlan() {
        return plan;
    }
}