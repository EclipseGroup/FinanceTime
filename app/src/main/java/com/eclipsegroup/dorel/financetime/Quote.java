package com.eclipsegroup.dorel.financetime;

import org.json.JSONException;
import org.json.JSONObject;


public class Quote implements JSONPopulator {
    private String symbol;
    private String name;
    private String daysLow;
    private String daysHigh;
    private String open;
    private String lastTrade;


    public String getLastTrade() { return lastTrade;  }

    public String getOpen() { return open; }

    public String getDaysHigh() {
        return daysHigh;
    }

    public String getDaysLow() {
        return daysLow;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public void populate(JSONObject data) {
        symbol = data.optString("Symbol");
        name = data.optString("Name");
        daysLow = data.optString("DaysLow");
        daysHigh = data.optString("DaysHigh");
        open = data.optString("Open");
        lastTrade = data.optString("LastTradePriceOnly");
    }

    @Override
    public JSONObject toJSON() {
        JSONObject data = new JSONObject();

        try {
            data.put("symbol", symbol);
            data.put("Name", name);
            data.put("DaysLow", daysLow);
            data.put("DaysHigh", daysHigh);
            data.put("LastTradePriceOnly", lastTrade);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;
    }


}

