package com.vmonaco.bbl.events;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.vmonaco.bbl.BioEvent;
import com.vmonaco.bbl.Utility;

public class BioKeystrokeEvent implements BioEvent {

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
        return "Keystroke, key: " + key_string + ", " + key_code;
    }

    public JSONObject toJSON() {
        HashMap<String, Object> values = new HashMap<String, Object>();
        
        values.put("timepress", ""+press_time);
        values.put("timerelease", ""+release_time);
        values.put("keycode", ""+key_code);
        values.put("keylocation", ""+key_location);
        return new JSONObject(values);
    }
    
    @Override
    public String[] values() {
        return new String[] {event_type, ""+press_time, ""+release_time, 
                ""+key_code, key_string, ""+modifier_code, modifier_string, ""+key_location};
    }
}
