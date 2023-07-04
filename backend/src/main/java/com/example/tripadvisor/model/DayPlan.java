package com.example.tripadvisor.model;

public class DayPlan {

    private int num;
    private List<Content> plan;

    public DayPlan(int num, List<Content> plan) {
        this.num = num;
        this.plan = plan;
    }

    public int getNum() {
        return num;
    }

    public List<Content> getPlan() {
        return plan;
    }
}