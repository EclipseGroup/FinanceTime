package com.eclipsegroup.dorel.financetime.models;


import java.io.Serializable;
import java.util.ArrayList;

public class IndicesData implements Serializable{

    public ArrayList<Index> indices;
    public ArrayList<Index> stocks;
    public ArrayList<Index> forex;
    public ArrayList<Index> commodities;
    public Integer updateState;

    public IndicesData(){
        updateState = 0;
    }

}
