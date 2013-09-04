package com.vmonaco.bbl.events;

import java.util.HashMap;
import java.util.Map;

import com.vmonaco.bbl.Mappable;
import com.vmonaco.bbl.Utility;

public class BioStylometryEvent implements Mappable {

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
    
    public Map toMap() {
        HashMap<String, Object> values = new HashMap<String, Object>();
        values.put("event_type", event_type);
        values.put("start_time", ""+start_time);
        values.put("end_time", ""+end_time);
        values.put("text", text);
        return values;
    }
    
    @Override
    public String[] values() {
        return new String[] {event_type, ""+start_time, ""+end_time, text}; 
    }
}
