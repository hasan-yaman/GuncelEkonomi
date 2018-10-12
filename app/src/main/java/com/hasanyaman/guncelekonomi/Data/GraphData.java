package com.hasanyaman.guncelekonomi.Data;

public class GraphData {
    private float selling;
    private long updateDate;

    public GraphData(float selling, long updateDate) {
        this.selling = selling;
        this.updateDate = updateDate;
    }

    public float getSelling() {
        return selling;
    }

    public void setSelling(float selling) {
        this.selling = selling;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }
}
