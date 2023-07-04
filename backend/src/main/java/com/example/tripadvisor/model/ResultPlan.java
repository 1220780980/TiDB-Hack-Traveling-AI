package com.example.tripadvisor.model;

public class ResultPlan {

    private List<DayPlan> plan;

    public ResultPlan(List<DayPlan> plan) {
        this.plan = plan;
    }

    public List<DayPlan> getPlan() {
        return plan;
    }
}