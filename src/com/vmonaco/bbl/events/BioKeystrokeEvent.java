package com.vmonaco.bbl.events;

import java.util.HashMap;
import java.util.Map;

import com.vmonaco.bbl.Mappable;
import com.vmonaco.bbl.Utility;

public class BioKeystrokeEvent implements Mappable {

    public static final String event_type = "keystroke_event";

    public long press_time;
    public long release_time;
    public int key_code;
    public String key_string;
    public int modifier_code;
    public String modifier_string;
    public int key_location;

    @Override
    public String toString() {
        return Utility.csvString(event_type,press_time,release_time,key_code,key_string,modifier_code,modifier_string,key_location); 
    }
    
    public String logString() {
        return "Keystroke, key: " + key_string;
    }

    public Map toMap() {
        HashMap<String, Object> values = new HashMap<String, Object>();
        values.put("event_type", event_type);
        values.put("press_time", ""+press_time);
        values.put("release_time", ""+release_time);
        values.put("key_code", ""+key_code);
        values.put("key_string", key_string);
        values.put("modifier_code", ""+modifier_code);
        values.put("modifier_string", ""+modifier_string);
        values.put("key_location", ""+key_location);
        return values;
    }
    
    @Override
    public String[] values() {
        return new String[] {event_type, ""+press_time, ""+release_time, 
                ""+key_code, key_string, ""+modifier_code, modifier_string, ""+key_location};
    }
}
