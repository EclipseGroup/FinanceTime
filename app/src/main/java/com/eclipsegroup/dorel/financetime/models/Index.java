package com.eclipsegroup.dorel.financetime.models;

public class Index {

    public String firstName;
    public String secondName;
    public String centralName;
    public String value;
    public String min;
    public String max;
    public String growth;
    public String percent_growth;

    public static Index setNewIndex(String firstName, String secondName, String centralName,
                         String currentValue, String min, String max){

        Index index = new Index();

        index.firstName = firstName;
        index.secondName = secondName;
        index.centralName = centralName;
        index.value = currentValue;
        index.min = min;
        index.max = max;
        index.growth = "2.02";
        index.percent_growth = "3.12";

        return index;
    }
}


