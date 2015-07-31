package com.eclipsegroup.dorel.financetime;

import org.json.JSONObject;

public interface JSONPopulator {
    void populate(JSONObject data);
    JSONObject toJSON();
}
