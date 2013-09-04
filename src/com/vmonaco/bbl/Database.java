package com.vmonaco.bbl;

import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.crypto.dsig.spec.ExcC14NParameterSpec;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import au.com.bytecode.opencsv.CSVWriter;

public class Database {

    private static String api_url = "";
    private static final String api_password = "";
    private static String backup_url = "";
    private static final String backup_password = "";
  
    XmlRpcClientConfigImpl config;
    XmlRpcClient client;

    private static boolean FAILED = false;
    private SessionData mSessionData;
    
    Map<String, String[]> mEventColumns;
    
    Map<String, Object> result;
    Object[] params;
    
    public Database(SessionData session) {
        mSessionData = session;
        config = new XmlRpcClientConfigImpl();
        
        try {
            config.setServerURL(new URL(api_url));
        } catch (MalformedURLException e) {
            FAILED = true;
            e.printStackTrace();
            CustomExceptionHandler.submitCrashReport(e);
            BioLogger.LOGGER.severe("Error establishing a connection with the server.");
        }
        
        client = new XmlRpcClient();
        client.setConfig(config);
        
        mEventColumns = new HashMap<String, String[]>();
        mEventColumns.put("click_event", new String[] {
"event_type", "press_time", "release_time", "button_code", "press_x", "press_y", "release_x", "release_y", "modifier_code", "modifier_string", "image"});
        mEventColumns.put("keystroke_event", new String[] {
"event_type", "press_time", "release_time", "key_code", "key_string", "modifier_code", "modifier_string", "key_location"});
        mEventColumns.put("motion_event", new String[] {
"event_type", "time", "x", "y", "modifier_code", "modifier_string", "dragged"});
        mEventColumns.put("scroll_event", new String[] {
"event_type", "time", "rotation", "amount", "type", "x", "y", "modifier_code", "modifier_string"});
        mEventColumns.put("stylometry_event", new String[] {
"event_type", "start_time", "end_time", "text"});

        
    
    }

    public boolean hasFailed() {
        return FAILED;
    }
    
    public boolean openSession() {

        if (FAILED) {
            return false;
        }

        params = new Object[] { api_password, mSessionData.identity, mSessionData.key,
                mSessionData.os_name, mSessionData.os_arch, mSessionData.os_version,
                mSessionData.locale, mSessionData.tags };

        try {

            result = (HashMap<String, Object>) client.execute("openSession",
                    params);

            if ((Integer) result.get("error") != 0) {
                FAILED = true;
                BioLogger.LOGGER.severe("API error:");
                BioLogger.LOGGER.severe((String) result.get("error_message"));
            } else {
                mSessionData.session_id = (Integer) result.get("session_id");
                BioLogger.LOGGER.info("Opened session " + mSessionData.session_id);
            }
            
        } catch (Exception e) {
            FAILED = true;
            e.printStackTrace();
            CustomExceptionHandler.submitCrashReport(e);
            BioLogger.LOGGER.severe("Error opening session.");
        }
        
        return !FAILED;
    }
    
    public void closeSession() {

        if (FAILED) {
            return;
        }

        params = new Object[] { api_password, mSessionData.identity, 
                mSessionData.key, mSessionData.session_id };

        try {

            result = (HashMap<String, Object>) client.execute("closeSession",
                    params);

            if ((Integer) result.get("error") != 0) {
                FAILED = true;
                BioLogger.LOGGER.severe("API error:");
                BioLogger.LOGGER.severe((String) result.get("error_message"));
            } else {
                BioLogger.LOGGER.info("Closed session " + mSessionData.session_id);
            }
            
        } catch (Exception e) {
            FAILED = true;
            e.printStackTrace();
            CustomExceptionHandler.submitCrashReport(e);
            BioLogger.LOGGER.severe("Error closing session");
        } finally {

        }

    }

    public void insertEntities(Map[] data) {

        if (data.length == 0) {
            return;
        }
        
        if (FAILED) {
            BioLogger.LOGGER.info("Something failed, sending backup data.");
            backupData(data);
            return;
        }
        
        BioLogger.LOGGER.info("Sending events");
        
        params = new Object[] { api_password, mSessionData.identity, 
                mSessionData.key, mSessionData.session_id, data };

        try {

            result = (HashMap<String, Object>) client.execute("enroll", params);
            
            if ((Integer) result.get("error") != 0) {
                FAILED = true;
                BioLogger.LOGGER.severe("API error:");
                BioLogger.LOGGER.severe((String) result.get("error_message"));
            } else {
                if (data.length == (Integer) result.get("enrolled")) {
                    BioLogger.LOGGER.info("Enrolled " + data.length + " events.");
                } else {
                    FAILED = true;
                    String message = "Error!! buffer contained: " + 
                            data.length + ", but only enrolled: " + (Integer) result.get("enrolled");
                    BioLogger.LOGGER.severe(message);
                    CustomExceptionHandler.submitCrashReport(new Exception(message));
                    backupData(data);
                }
            }
            
        } catch (Exception e) {
            FAILED = true;
            e.printStackTrace();
            CustomExceptionHandler.submitCrashReport(e);
            BioLogger.LOGGER.severe("Error sending events.");
        }
    }
    
    public void backupData(Map[] data) {
        String filename = mSessionData.session_id + ".csv";
        StringWriter stringWriter = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(stringWriter);
        
        for (int i = 0; i < data.length; i++) {
            Map event = data[i];
            String[] values = new String[event.size()];
            for (int j = 0; j < event.size(); j++) {
                values[j] = "" + event.get(mEventColumns.get((String)event.get("event_type"))[j]);
            }
            csvWriter.writeNext(values);
        }
        
        try {
            csvWriter.flush();
            csvWriter.close();
            sendBackupData(filename, stringWriter.toString());
        } catch (IOException e) {
            e.printStackTrace();
            CustomExceptionHandler.submitCrashReport(e);
            BioLogger.LOGGER.severe("Error backing up data, probably lost information.");
        }
    }
    
    private void sendBackupData(String filename, String contents)
            throws ClientProtocolException, IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(backup_url);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("filename", filename));
        nvps.add(new BasicNameValuePair("contents", contents));
        nvps.add(new BasicNameValuePair("password", backup_password));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        httpClient.execute(httpPost);
//        HttpResponse httpResponse = httpClient.execute(httpPost);
//        HttpEntity responseEntity = httpResponse.getEntity();
//        if(responseEntity!=null) {
//            System.out.println(EntityUtils.toString(responseEntity));
//        }
    }
}
