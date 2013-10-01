package com.vmonaco.bbl.events;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.apache.ws.commons.util.Base64;
import org.json.simple.JSONObject;

import com.vmonaco.bbl.BioLogger;
import com.vmonaco.bbl.BioEvent;
import com.vmonaco.bbl.Utility;

public class BioClickEvent implements BioEvent {
    
    public static final String event_type = "click_event";
    
    public long press_time;
    public long release_time;
    public int button_code;
    public int press_x;
    public int press_y;
    public int release_x;
    public int release_y;
    public int modifier_code;
    public String modifier_string;
    public BufferedImage image;
    
    @Override
    public String toString() {
        return Utility.csvString(event_type,press_time,release_time,button_code,press_x,press_y,release_x,release_y,modifier_code,modifier_string); 
    }

    public String logString() {
        return "Mouse clicked, button: "+ button_code + ", position: " + press_x + ", " + press_y; 
    }

    public JSONObject toJSON() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try {
            ImageIO.write(image, "png", out);
        } catch (IOException e) {
            e.printStackTrace();
            BioLogger.LOGGER.severe(Utility.createCrashReport(e));
        }
        
        HashMap<String, Object> values = new HashMap<String, Object>();
        values.put("timepress", ""+press_time);
        values.put("timerelease", ""+release_time);
        values.put("button", ""+button_code);
        values.put("xpress", ""+press_x);
        values.put("ypress", ""+press_y);
        values.put("xrelease", ""+release_x);
        values.put("yrelease", ""+release_y);
        values.put("image", Base64.encode(out.toByteArray()));
        return new JSONObject(values);
    }
    
    @Override
    public String[] values() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", out);
        } catch (IOException e) {
        	BioLogger.LOGGER.severe(Utility.createCrashReport(e));
        }
        return new String[] {event_type, ""+press_time, ""+release_time, 
                ""+button_code, ""+press_x, ""+press_y, ""+release_x, ""+release_y, 
                ""+modifier_code, modifier_string, Base64.encode(out.toByteArray())};
    }
}
