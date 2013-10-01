package com.vmonaco.bbl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.vmonaco.bbl.events.BioClickEvent;
import com.vmonaco.bbl.events.BioKeystrokeEvent;
import com.vmonaco.bbl.events.BioMotionEvent;
import com.vmonaco.bbl.events.BioScrollEvent;
import com.vmonaco.bbl.events.BioStylometryEvent;

/**
 * A buffer for storing biometric events and sending them to the server. 
 * 
 * @author vinnie
 *
 */
public class Buffer {

    SessionData mSession;
    
    private JSONArray mKeystroke;
    private JSONArray mStylometry;
    private JSONArray mMouseClick;
    private JSONArray mMouseMotion;
    private JSONArray mMouseScroll;
    
    public Buffer(SessionData session) {
    	mSession = session;
        mKeystroke = new JSONArray();
        mStylometry = new JSONArray();
        mMouseClick = new JSONArray();
        mMouseMotion = new JSONArray();
        mMouseScroll = new JSONArray();
    }

    public void addKeystrokeEvent(BioKeystrokeEvent e) {
    	mKeystroke.add(e.toJSON());
    }
    
    public void addStylometryEvent(BioStylometryEvent e) {
    	mStylometry.add(e.toJSON());
    }
    
    public void addClickEvent(BioClickEvent e) {
    	mMouseClick.add(e.toJSON());
    }
    
    public void addMotionEvent(BioMotionEvent e) {
    	mMouseMotion.add(e.toJSON());
    }
    
    public void addScrollEvent(BioScrollEvent e) {
    	mMouseScroll.add(e.toJSON());
    }
    
    public void flush() {
    	try {
    		postData();
    	} catch (IOException e) {
            BioLogger.LOGGER.severe("Error sending data.");
            BioLogger.LOGGER.severe(Utility.createCrashReport(e));
        }
    }

    private void postData() throws ClientProtocolException, IOException {
    	
    	HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
        HttpResponse response;
        HttpPost post = new HttpPost(mSession.enrollURL);
        
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("username", mSession.username));
        nameValuePairs.add(new BasicNameValuePair("useragent", "native"));
        nameValuePairs.add(new BasicNameValuePair("platform", mSession.os_name + " " + mSession.os_version + " " + mSession.os_arch));
        nameValuePairs.add(new BasicNameValuePair("task", mSession.task));
        nameValuePairs.add(new BasicNameValuePair("source", "native_bbl"));
        nameValuePairs.add(new BasicNameValuePair("tags", mSession.tags));
        
        nameValuePairs.add(new BasicNameValuePair("keystroke", mKeystroke.toJSONString()));
        nameValuePairs.add(new BasicNameValuePair("numkeystroke", ""+mKeystroke.size()));
        
        nameValuePairs.add(new BasicNameValuePair("stylometry", mStylometry.toJSONString()));
        nameValuePairs.add(new BasicNameValuePair("numstylometry", ""+mStylometry.size()));
        
        nameValuePairs.add(new BasicNameValuePair("mouseclick", mMouseClick.toJSONString()));
        nameValuePairs.add(new BasicNameValuePair("nummouseclick", ""+mMouseClick.size()));
        
        nameValuePairs.add(new BasicNameValuePair("mousemotion", mMouseMotion.toJSONString()));
        nameValuePairs.add(new BasicNameValuePair("nummousemotion", ""+mMouseMotion.size()));
        
        nameValuePairs.add(new BasicNameValuePair("mousescroll", mMouseScroll.toJSONString()));
        nameValuePairs.add(new BasicNameValuePair("nummousescroll", ""+mMouseScroll.size()));
        
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        response = client.execute(post);
        
        if(response!=null){
	    	BioLogger.LOGGER.info("Response from server: " + EntityUtils.toString(response.getEntity()));
        }
    }
}
