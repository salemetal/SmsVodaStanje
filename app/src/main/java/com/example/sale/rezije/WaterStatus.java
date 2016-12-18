package com.example.sale.rezije;

/**
 * Created by Sale on 18.5.2016..
 */
public class WaterStatus {

    private int id;
    public int wcVal;
    public int kupaonaVal;

    public WaterStatus() {
    }

    public WaterStatus(int wcVal, int kupaonaVal) {
        this.wcVal = wcVal;
        this.kupaonaVal = kupaonaVal;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


}
