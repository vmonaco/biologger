package com.vmonaco.bbl;

import java.util.Map;

/**
 * Just a simple interface to help with event serialization
 * 
 * @author vinnie
 *
 */
public interface Mappable {
    public Map toMap();
    public String[] values();
}
