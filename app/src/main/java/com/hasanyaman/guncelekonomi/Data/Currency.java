package com.hasanyaman.guncelekonomi.Data;

public class Currency {
    private String name;
    private double selling;
    private double buying;
    private double changeRate;

    public Currency(String name, double buying, double selling, double changeRate) {
        this.name = name;
        this.buying = buying;
        this.selling = selling;
        this.changeRate = changeRate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSelling() {
        return selling;
    }

    public void setSelling(double selling) {
        this.selling = selling;
    }

    public double getBuying() {
        return buying;
    }

    public void setBuying(double buying) {
        this.buying = buying;
    }

    public double getChangeRate() {
        return changeRate;
    }

    public void setChangeRate(double changeRate) {
        this.changeRate = changeRate;
    }
}
