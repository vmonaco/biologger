package com.vmonaco.bbl;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

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
        	BioLogger.LOGGER.severe(Utility.createCrashReport(e));
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
    
    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);  
    }
    
    public static String createCrashReport(Throwable e) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        printWriter.close();
        
        String header = "";
        header += "os.name    : " + System.getProperty("os.name") + "\n";
        header += "os.arch    : " + System.getProperty("os.arch") + "\n";
        header += "os.version : " + System.getProperty("os.version") + "\n";
        
        System.getProperties().keySet();
        for (Object k : System.getProperties().keySet()) {
            header += padRight(k.toString(), 40) + ": " + System.getProperty((String) k) + "\n";
        }
        
        String stacktrace = result.toString();
        return header + "\nStacktrace:\n" + stacktrace;
    }
}
