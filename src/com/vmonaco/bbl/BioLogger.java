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
    private Database mDatabase;
    private Buffer mBuffer;
    private Thread mBufferThread;
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
            CustomExceptionHandler.submitCrashReport(e);
            return;
        }
        
        if (! mDatabase.openSession()) {
            FAILED = true;
            return;
        }
        
        mBufferThread = new Thread(mBuffer);
        mBufferThread.start();
        
        GlobalScreen.getInstance().addNativeKeyListener(mListener);
        GlobalScreen.getInstance().addNativeMouseListener(mListener);
        GlobalScreen.getInstance().addNativeMouseMotionListener(mListener);
        GlobalScreen.getInstance().addNativeMouseWheelListener(mListener);
        mGui.setStatus("Recording session as :  " + mSessionData.identity);
    }

    public void stopLogging() {
        if (FAILED) {
            return;
        }
        GlobalScreen.unregisterNativeHook();
        mDatabase.closeSession();
        mBuffer.stop();
        mBuffer.finish();
    }

    public void close() {
        stopLogging();
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
                System.out.println("vk_map['" + keyString + "'] = " + i);
            }
        }
    }
    
    public static SessionData createSession(String[] args) {
        SessionData session = new SessionData();
        session.identity = args[0];
        session.key = args[1];
        session.os_name = System.getProperty("os.name").trim();
        session.os_arch = System.getProperty("os.arch").trim();
        session.os_version = System.getProperty("os.version").trim();
        session.locale = InputContext.getInstance().getLocale().toString().trim();
        session.tags.add("bbl");
        
        if (args.length > 2) {
            for (int i = 2; i < args.length; i++) {
                session.tags.add(args[i]);
            }
        }
        
        return session;
    }
    
    public void initialize(String[] args) {
        mSessionData = createSession(args);
        mDatabase = new Database(mSessionData);
        mBuffer = new Buffer(mDatabase);
        mListener = new Listener(mBuffer);
        mGui = new GUI(this);
    }
    
    public static void main(String[] args) {
//        printKeyMap();

        if (args.length < 2) {
            System.out.println("Usage: bbl identity key [tags]");
            System.exit(1);
        }
        
        Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler());
        BioLogger app = new BioLogger();
        
        try {
            ServerSocket ss = new ServerSocket(PORT, 0, InetAddress.getByAddress(new byte[] {127,0,0,1}));
            app.initialize(args);
        } catch (BindException e) {
            CustomExceptionHandler.submitCrashReport(e);
            System.out.println("Logger already running");
            app.alert("Logger already running.");
            System.exit(1);
        } catch (IOException e) {
            CustomExceptionHandler.submitCrashReport(e);
            System.out.println("Unexpected error.");
            app.alert("Unexpected error.");
            System.exit(2);
        }
        
        app.startLogging();
    }
}
