package com.example.tripadvisor.model;

public class Company {

    private String companyName;
    private int rank;
    private String country;
    private int employeesNum;

    public Company(String companyName, int rank, String country, int employeesNum) {
        this.companyName = companyName;
        this.rank = rank;
        this.country = country;
        this.employeesNum = employeesNum;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getEmployeesNum() {
        return employeesNum;
    }

    public void setEmployeesNum(int employeesNum) {
        this.employeesNum = employeesNum;
    }
}

