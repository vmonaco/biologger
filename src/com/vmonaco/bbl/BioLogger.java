package com.vmonaco.bbl;

import java.awt.im.InputContext;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;

public class BioLogger {

    public static final Logger LOGGER =  Logger.getLogger("com.vmonaco.bbl");
    public static final int PORT = 9346;
    
    private static boolean FAILED = false;
    private SessionData mSessionData;
    private Buffer mBuffer;
    private Listener mListener;
    private GUI mGui;

    public void startLogging() {
        
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            FAILED = true;
            BioLogger.LOGGER.severe("There was a problem registering the native hook.");
            if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                BioLogger.LOGGER.severe("Make sure that Assistive Devices is enabled.");
                BioLogger.LOGGER.info("Go to Preferences->Universal Access and make sure the checkbox 'Enabled Access for Assistive Devices' is marked.");
            }
            return;
        }
        
        GlobalScreen.getInstance().addNativeKeyListener(mListener);
        GlobalScreen.getInstance().addNativeMouseListener(mListener);
        GlobalScreen.getInstance().addNativeMouseMotionListener(mListener);
        GlobalScreen.getInstance().addNativeMouseWheelListener(mListener);
        mGui.setStatus("Recording session as :  " + mSessionData.username);
    }

    public void flush() {
    	mBuffer.flush();
    }
    
    public void stopLogging() {
        GlobalScreen.unregisterNativeHook();
    }

    public void close() {
        System.runFinalization();
        System.exit(0);
    }
    
    public void alert(String text) {
        mGui.alert(text);
    }

    public static void printKeyMap() {
        
        Set<String> keySet = new HashSet<String>();
        for (int i = 0; i < 65536; i++) {
            String keyString = NativeKeyEvent.getKeyText(i).toLowerCase();
            if (! keySet.contains(keyString)  && ! keyString.contains("unknown")) {
                keySet.add(keyString);
                BioLogger.LOGGER.info(keyString + " = " + i);
            }
        }
    }
    
    public static SessionData createSession(String[] args) {
        SessionData session = new SessionData();
        session.enrollURL = args[0];
        session.username = args[1];
        session.os_name = System.getProperty("os.name").trim();
        session.os_arch = System.getProperty("os.arch").trim();
        session.os_version = System.getProperty("os.version").trim();
        session.locale = InputContext.getInstance().getLocale().toString().trim();
        session.task = args[2];
        session.tags = "";
        
        if (args.length > 3) {
            for (int i = 3; i < args.length; i++) {
                session.tags += args[i];
            }
        }
        
        return session;
    }
    
    public void initialize(String[] args) {
        mSessionData = createSession(args);
        mBuffer = new Buffer(mSessionData);
        mListener = new Listener(mBuffer);
        mGui = new GUI(this);
    }
    
    public static void main(String[] args) {
//        printKeyMap();

        if (args.length < 2) {
            System.out.println("Usage: bbl url identity [tags]");
            System.exit(1);
        }
        
//        Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler());
        BioLogger app = new BioLogger();
        
        try {
            ServerSocket ss = new ServerSocket(PORT, 0, InetAddress.getByAddress(new byte[] {127,0,0,1}));
            app.initialize(args);
        } catch (BindException e) {
        	BioLogger.LOGGER.severe("Logger already running");
            app.alert("Logger already running.");
            System.exit(1);
        } catch (IOException e) {
        	BioLogger.LOGGER.severe("Unexpected error.");
            app.alert("Unexpected error.");
            System.exit(2);
        }
        
        app.startLogging();
    }
}
