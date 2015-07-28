package com.eclipsegroup.dorel.financetime.models;

import java.util.ArrayList;
import java.util.Date;

public class Graph {

    String symbol;
    Date endPeriod;
    Date startPeriod;
    String resolution;
    Integer graphType;
    ArrayList<Double> values;

    public Graph(String symbol){
        this.symbol = symbol;
    }

    public String getName(){
        return symbol;
    }
}
