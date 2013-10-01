package com.vmonaco.bbl.events;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.vmonaco.bbl.BioEvent;
import com.vmonaco.bbl.Utility;

public class BioScrollEvent implements BioEvent {

    public static final String event_type = "scroll_event";

    public long time;
    public int rotation;
    public int amount;
    public int type; // 0 for unit, 1 for block
    public int x;
    public int y;
    public int modifier_code;
    public String modifier_string;
    
    @Override
    public String toString() {
        return Utility.csvString(event_type,time,rotation,amount,type,x,y,modifier_code,modifier_string); 
    }
    
    public String logString() {
        return "Mouse wheel moved, direction: " + rotation;
    }
    
    public JSONObject toJSON() {
        HashMap<String, Object> values = new HashMap<String, Object>();
        values.put("time", ""+time);
        values.put("rotation", ""+rotation);
        values.put("amount", ""+amount);
        values.put("type", ""+type);
        values.put("x", ""+x);
        values.put("y", ""+y);
        return new JSONObject(values);
    }
    
    @Override
    public String[] values() {
        return new String[] {event_type, ""+time, ""+rotation, ""+amount, ""+type, ""+x, ""+y, ""+modifier_code, modifier_string};
    }
}
