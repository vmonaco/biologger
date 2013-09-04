package com.vmonaco.bbl.events;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.apache.ws.commons.util.Base64;

import com.vmonaco.bbl.CustomExceptionHandler;
import com.vmonaco.bbl.Mappable;
import com.vmonaco.bbl.Utility;

public class BioClickEvent implements Mappable {
    
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

    public Map toMap() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try {
            ImageIO.write(image, "png", out);
        } catch (IOException e) {
            e.printStackTrace();
            CustomExceptionHandler.submitCrashReport(e);
        }
        
        HashMap<String, Object> values = new HashMap<String, Object>();
        values.put("event_type", event_type);
        values.put("press_time", ""+press_time);
        values.put("release_time", ""+release_time);
        values.put("button_code", ""+button_code);
        values.put("press_x", ""+press_x);
        values.put("press_y", ""+press_y);
        values.put("release_x", ""+release_x);
        values.put("release_y", ""+release_y);
        values.put("modifier_code", ""+modifier_code);
        values.put("modifier_string", ""+modifier_string);
        values.put("image", Base64.encode(out.toByteArray()));
        return values;
    }
    
    @Override
    public String[] values() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", out);
        } catch (IOException e) {
            e.printStackTrace();
            CustomExceptionHandler.submitCrashReport(e);
        }
        return new String[] {event_type, ""+press_time, ""+release_time, 
                ""+button_code, ""+press_x, ""+press_y, ""+release_x, ""+release_y, 
                ""+modifier_code, modifier_string, Base64.encode(out.toByteArray())};
    }
}
