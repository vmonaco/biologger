package com.vmonaco.bbl.events;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.vmonaco.bbl.BioEvent;
import com.vmonaco.bbl.Utility;

public class BioStylometryEvent implements BioEvent {

    public static final String event_type = "stylometry_event";
    
    public long start_time;
    public long end_time;
    public String text = "";
    
    @Override
    public String toString() {
        return Utility.csvString(event_type,start_time,end_time,text); 
    }

    public String logString() {
        return "Stylometry: " + text;
    }
    
    public JSONObject toJSON() {
        HashMap<String, Object> values = new HashMap<String, Object>();
        values.put("timestart", ""+start_time);
        values.put("timeend", ""+end_time);
        values.put("text", text);
        return new JSONObject(values);
    }
    
    @Override
    public String[] values() {
        return new String[] {event_type, ""+start_time, ""+end_time, text}; 
    }
}
