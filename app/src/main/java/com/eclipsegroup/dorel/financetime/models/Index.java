package com.eclipsegroup.dorel.financetime.models;

public class Index {

    public String firstName;
    public String secondName;
    public String centralName;
    public Double value;
    public Double min;
    public Double max;
    public Double growth;
    public Double percent_growth;

    public static Index setNewIndex(String firstName, String secondName, String centralName,
                         Double currentValue, Double min, Double max, Double growth,
                         Double percent_growth){

        Index index = new Index();

        index.firstName = firstName;
        index.secondName = secondName;
        index.centralName = centralName;
        index.value = currentValue;
        index.min = min;
        index.max = max;
        index.growth = growth;
        index.percent_growth = percent_growth;

        return index;
    }
}


