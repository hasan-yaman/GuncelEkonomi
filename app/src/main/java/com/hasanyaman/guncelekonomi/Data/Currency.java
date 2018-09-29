package com.hasanyaman.guncelekonomi.Data;

public class Currency {
    private String name;
    private double value;
    private double changeRate;

    public Currency(String name, double value, double changeRate) {
        this.name = name;
        this.value = value;
        this.changeRate = changeRate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getChangeRate() {
        return changeRate;
    }

    public void setChangeRate(double changeRate) {
        this.changeRate = changeRate;
    }
}
