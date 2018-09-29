package com.hasanyaman.guncelekonomi.Data;

public class Cryptocurrency {
    private String fullName;
    private String symbol;
    private String value_try;
    private double changeRate;
    private double volume;
    private int rank;

    public Cryptocurrency(String fullName, String symbol, String value_try, double changeRate, double volume, int rank) {
        this.fullName = fullName;
        this.symbol = symbol;
        this.value_try = value_try;
        this.changeRate = changeRate;
        this.volume = volume;
        this.rank = rank;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getValue_try() {
        return value_try;
    }

    public void setValue_try(String  value_try) {
        this.value_try = value_try;
    }

    public double getChangeRate() {
        return changeRate;
    }

    public void setChangeRate(double changeRate) {
        this.changeRate = changeRate;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
