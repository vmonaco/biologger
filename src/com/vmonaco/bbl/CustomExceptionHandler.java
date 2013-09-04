package com.vmonaco.bbl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class CustomExceptionHandler implements UncaughtExceptionHandler {

    private static String url = "";
    private static final String password = "";
    private static String localPath = null;
    private static final String TAG = "CustomExceptionHandler";
    private static UncaughtExceptionHandler defaultUEH;
    
    /* 
     * if any of the parameters is null, the respective functionality 
     * will not be used 
     */
    public CustomExceptionHandler() {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
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
    
    public static void submitCrashReport(Throwable e) {
        String crashReport = createCrashReport(e);
        String timestamp = "" + Calendar.getInstance().getTimeInMillis();
        String filename = timestamp + ".stacktrace";

        if (localPath != null) {
            writeToFile(crashReport, filename);
        }

        if (url != null) {
            sendToServer(crashReport, filename);
        }
    }
    
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        BioLogger.LOGGER.severe("Uncaught exception, creating crash report.");
        submitCrashReport(e);
        defaultUEH.uncaughtException(t, e);
    }

    private static void writeToFile(String stacktrace, String filename) {
        try {
            BufferedWriter bos = new BufferedWriter(new FileWriter(localPath + "/" + filename));
            bos.write(stacktrace);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendToServer(String stacktrace, String filename) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("filename", filename));
        nvps.add(new BasicNameValuePair("contents", stacktrace));
        nvps.add(new BasicNameValuePair("password", password));
        
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            httpClient.execute(httpPost);
        } catch (IOException e) {
            BioLogger.LOGGER.severe("Unable to submit crash report.");
        }
    }
}
