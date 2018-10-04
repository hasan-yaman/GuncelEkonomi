package com.hasanyaman.guncelekonomi.Data;

public class Bank {
    private String fullName;
    private double buying;
    private double selling;
    private long updateDate;

    public Bank(String fullName, double buying, double selling, long updateDate) {
        this.fullName = fullName;
        this.buying = buying;
        this.selling = selling;
        this.updateDate = updateDate;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public double getBuying() {
        return buying;
    }

    public void setBuying(double buying) {
        this.buying = buying;
    }

    public double getSelling() {
        return selling;
    }

    public void setSelling(double selling) {
        this.selling = selling;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }
}
