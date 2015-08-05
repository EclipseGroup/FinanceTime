package com.eclipsegroup.dorel.financetime.models;

public class GraphElement {

    public String date;
    public String open;
    public String close;
    public String min;
    public String max;
    public String volume;

    public GraphElement(String date, String open, String close, String min, String max, String volume){
        this.date = date;
        this.open = open;
        this.close = close;
        this.max = max;
        this.min = min;
        this.volume = volume;
    }
}

