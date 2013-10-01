package com.vmonaco.bbl.events;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.vmonaco.bbl.BioEvent;
import com.vmonaco.bbl.Utility;

public class BioMotionEvent implements BioEvent {

    public static final String event_type = "motion_event";
    
    public long time;
    public int x;
    public int y;
    public int modifier_code;
    public String modifier_string;
    public int dragged;
    
    @Override
    public String toString() {
        return Utility.csvString(event_type,time,x,y,modifier_code,modifier_string,dragged); 
    }
    
    public String logString() {
        return "Mouse motion, time: "+ time + ", position: " + x + ", " + y + ", dragged: " + dragged + ", mods: " + modifier_string; 
    }
    
    public JSONObject toJSON() {
        HashMap<String, Object> values = new HashMap<String, Object>();
        values.put("time", ""+time);
        values.put("x", ""+x);
        values.put("y", ""+y);
        values.put("dragged", ""+dragged);
        return new JSONObject(values);
    }
    
    @Override
    public String[] values() {
        return new String[] {event_type, ""+time, ""+x, ""+y, ""+modifier_code, modifier_string, ""+dragged};
    }
}
