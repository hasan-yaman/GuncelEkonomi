package com.hasanyaman.guncelekonomi.Data;

public class StockMarket {
    private String fullName;
    private double latest;
    private double changeRate;
    private double firstSeanceLowest;
    private double firstSeanceHighest;
    private double firstSeanceClosing;
    private double secondSeanceLowest;
    private double secondSeanceHighest;
    private double secondSeanceClosing;
    private double previousClosing;

    public StockMarket(String fullName, double latest, double changeRate, double firstSeanceLowest, double firstSeanceHighest, double firstSeanceClosing, double secondSeanceLowest, double secondSeanceHighest, double secondSeanceClosing, double previousClosing) {
        this.fullName = fullName;
        this.latest = latest;
        this.changeRate = changeRate;
        this.firstSeanceLowest = firstSeanceLowest;
        this.firstSeanceHighest = firstSeanceHighest;
        this.firstSeanceClosing = firstSeanceClosing;
        this.secondSeanceLowest = secondSeanceLowest;
        this.secondSeanceHighest = secondSeanceHighest;
        this.secondSeanceClosing = secondSeanceClosing;
        this.previousClosing = previousClosing;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public double getLatest() {
        return latest;
    }

    public void setLatest(double latest) {
        this.latest = latest;
    }

    public double getChangeRate() {
        return changeRate;
    }

    public void setChangeRate(double changeRate) {
        this.changeRate = changeRate;
    }

    public double getFirstSeanceLowest() {
        return firstSeanceLowest;
    }

    public void setFirstSeanceLowest(double firstSeanceLowest) {
        this.firstSeanceLowest = firstSeanceLowest;
    }

    public double getFirstSeanceHighest() {
        return firstSeanceHighest;
    }

    public void setFirstSeanceHighest(double firstSeanceHighest) {
        this.firstSeanceHighest = firstSeanceHighest;
    }

    public double getFirstSeanceClosing() {
        return firstSeanceClosing;
    }

    public void setFirstSeanceClosing(double firstSeanceClosing) {
        this.firstSeanceClosing = firstSeanceClosing;
    }

    public double getSecondSeanceLowest() {
        return secondSeanceLowest;
    }

    public void setSecondSeanceLowest(double secondSeanceLowest) {
        this.secondSeanceLowest = secondSeanceLowest;
    }

    public double getSecondSeanceHighest() {
        return secondSeanceHighest;
    }

    public void setSecondSeanceHighest(double secondSeanceHighest) {
        this.secondSeanceHighest = secondSeanceHighest;
    }

    public double getSecondSeanceClosing() {
        return secondSeanceClosing;
    }

    public void setSecondSeanceClosing(double secondSeanceClosing) {
        this.secondSeanceClosing = secondSeanceClosing;
    }

    public double getPreviousClosing() {
        return previousClosing;
    }

    public void setPreviousClosing(double previousClosing) {
        this.previousClosing = previousClosing;
    }
}
