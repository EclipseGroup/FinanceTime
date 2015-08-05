package com.eclipsegroup.dorel.financetime.models;

public class Index {

    public String firstName;
    public String secondName;
    public String dailyOpen;
    public String value;
    public String min;
    public String max;
    public String growth;
    public String percent_growth;
    public Integer color;
    public String centralName;

    public Index(String firstName, String secondName, String dailyOpen,
                 String currentValue, String min, String max){
        this.firstName = firstName;
        this.secondName = secondName;
        this.dailyOpen = dailyOpen;
        this.value = currentValue;
        this.min = min;
        this.max = max;
        this.growth = getGrowth(dailyOpen, currentValue);
        this.percent_growth = getPercentGrowth(this.growth, dailyOpen);

    }

    public Index(String close, String symbol, String date){
        this.firstName = date;
        this.secondName = close;
        this.centralName = symbol;
        this.dailyOpen = "";
        this.value = "";
        this.min = "";
        this.max = "";
        this.growth = "";
        this.percent_growth = "";
        this.color = 0;
    }

    public static Index setNewIndex(String firstName, String secondName, String dailyOpen,
                         String currentValue, String min, String max){

        Index index = new Index(firstName, secondName, dailyOpen,
                currentValue, min, max);

        return index;
    }

    private String getGrowth(String open, String price)
    {
        double op;
        double pr;
        double var;
        String variation;
        if (open.equals("null")){
            variation = "No Data";
            color = 1;
        }

        else {
            op = Double.parseDouble(open);
            pr = Double.parseDouble(price);
            var = pr - op;
            var = (double)((int)(var*100))/100;
            if(var < 0){
                variation = Double.toString(var);
                color = 0;
            }else{
                variation = "+" + Double.toString(var);
                color = 1;
            }

        }
        return variation;
    }

    private String getPercentGrowth(String variation, String open)
    {
        double op;
        double var;
        double varPerc;
        String perc ;
        if (variation.equals("null") || open.equals("null"))
            perc = "( No Data )";
        else {
            op = Double.parseDouble(open);
            var = Double.parseDouble(variation);
            varPerc = (double)((int)(((var*100)/op)*100))/100;
            if(var < 0){
                perc = Double.toString(varPerc) + "%";
            }else{
                perc = "+" + Double.toString(varPerc) + "%";
            }
        }
        return perc;
    }


}


