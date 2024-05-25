package com.example.economicsanalysis;

public class Indicator {
    private String name;
    private double previousYear;
    private double currentYear;
    private double absoluteDeviation;
    private double growthRate;

    public Indicator(String name, double previousYear, double currentYear) {
        this.name = name;
        this.previousYear = previousYear;
        this.currentYear = currentYear;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPreviousYear() {
        return previousYear;
    }

    public void setPreviousYear(double previousYear) {
        this.previousYear = previousYear;
    }

    public double getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(double currentYear) {
        this.currentYear = currentYear;
    }

    public double getAbsoluteDeviation() {
        return absoluteDeviation;
    }

    public void setAbsoluteDeviation(double absoluteDeviation) {
        this.absoluteDeviation = absoluteDeviation;
    }

    public double getGrowthRate() {
        return growthRate;
    }

    public void setGrowthRate(double growthRate) {
        this.growthRate = growthRate;
    }
}
