package com.vmonaco.bbl.events;

import java.util.HashMap;
import java.util.Map;

import com.vmonaco.bbl.Mappable;
import com.vmonaco.bbl.Utility;

public class BioMotionEvent implements Mappable {

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
    
    public Map toMap() {
        HashMap<String, Object> values = new HashMap<String, Object>();
        values.put("event_type", event_type);
        values.put("time", ""+time);
        values.put("x", ""+x);
        values.put("y", ""+y);
        values.put("modifier_code", ""+modifier_code);
        values.put("modifier_string", ""+modifier_string);
        values.put("dragged", ""+dragged);
        return values;
    }
    
    @Override
    public String[] values() {
        return new String[] {event_type, ""+time, ""+x, ""+y, ""+modifier_code, modifier_string, ""+dragged};
    }
}
