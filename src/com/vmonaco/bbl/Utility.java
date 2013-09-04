package com.vmonaco.bbl;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

public class Utility {

    private static Dimension mScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
    
    /**
     * Convert an array of objects to a CSV string
     */
    public static String csvString(Object...objects) {
        String str = objects[0].toString();
        for (int i = 1; i < objects.length; i++) {
            str += "," + objects[i].toString();
        }
        return str;
    }
    
    /**
     * Capture a section of the screen and zero out any part of the capture 
     * rectangle that is out of bounds (could contain noise)
     * 
     * @param capture
     * @return
     */
    public static BufferedImage screenCapture(Rectangle capture) {
        
        BufferedImage image = null;
        
        try {
            image = new Robot().createScreenCapture(capture);
        } catch (AWTException e) {
            CustomExceptionHandler.submitCrashReport(e);
        }
        
        Rectangle fillX = new Rectangle();
        Rectangle fillY = new Rectangle();
        
        if (capture.x < 0) {
            fillX.x = 0;
            fillX.width = 0 - capture.x;
            fillX.height = capture.height;
        } else if (capture.x + capture.width > mScreenSize.width) {
            fillX.x = capture.width - ((capture.x + capture.width) - mScreenSize.width);
            fillX.width = (capture.x + capture.width) - mScreenSize.width;
            fillX.height = capture.height;
        }
        
        if (capture.y < 0) {
            fillY.y = 0;
            fillY.height = 0 - capture.y;
            fillY.width = capture.width;
        } else if (capture.y + capture.height > mScreenSize.height) {
            fillY.y = capture.height - ((capture.y + capture.height) - mScreenSize.height);
            fillY.height = (capture.y + capture.height) - mScreenSize.height;
            fillY.width = capture.width;
        }
        
        Graphics2D graph = image.createGraphics();
        graph.setColor(Color.BLACK);
        graph.fill(fillX);
        graph.fill(fillY);
        graph.dispose();
        
        return image;
    }
}
