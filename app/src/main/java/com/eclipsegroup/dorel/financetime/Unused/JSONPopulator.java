package com.eclipsegroup.dorel.financetime.Unused;

import org.json.JSONObject;

public interface JSONPopulator {
    void populate(JSONObject data);
    JSONObject toJSON();
}
