package com.vmonaco.bbl;

import java.util.Map;

import org.json.simple.JSONObject;

/**
 * Just a simple interface to help with event serialization
 * 
 * @author vinnie
 *
 */
public interface BioEvent {
    public JSONObject toJSON();
    public String[] values();
}
