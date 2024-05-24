package com.example.economicsanalysis;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Indicator {
    private final StringProperty name;
    private final DoubleProperty previousYear;
    private final DoubleProperty currentYear;
    private final DoubleProperty absoluteDeviation;
    private final DoubleProperty growthRate;

    public Indicator(String name, double previousYear, double currentYear) {
        this.name = new SimpleStringProperty(name);
        this.previousYear = new SimpleDoubleProperty(previousYear);
        this.currentYear = new SimpleDoubleProperty(currentYear);
        this.absoluteDeviation = new SimpleDoubleProperty(currentYear - previousYear);
        this.growthRate = new SimpleDoubleProperty((currentYear  / previousYear) * 100);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public DoubleProperty previousYearProperty() {
        return previousYear;
    }

    public DoubleProperty currentYearProperty() {
        return currentYear;
    }

    public DoubleProperty absoluteDeviationProperty() {
        return absoluteDeviation;
    }

    public DoubleProperty growthRateProperty() {
        return growthRate;
    }
}
