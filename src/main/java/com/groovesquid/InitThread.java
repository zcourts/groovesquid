package com.groovesquid;

import com.google.gson.Gson;
import com.groovesquid.model.Country;
import com.groovesquid.util.Utils;

import javax.swing.*;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class InitThread extends Thread {
    
    private final static Logger log = Logger.getLogger(Main.class.getName());
    private CountDownLatch latch = new CountDownLatch(1);
    
    public CountDownLatch getLatch() {
        return latch;
    }

    public InitThread() {
        log.info("Initializing...");
    }
    
    @Override
    public void run() {
        Gson gson = new Gson();
        
        String session;
        try {
            session = gson.fromJson(GroovesharkClient.sendRequest("initiateSession", null), Response.class).getResult();
        } catch (Exception ex) {
            Main.getMainFrame().showError(Main.getLocaleString("ERROR_INITIATE_SESSION"));
            return;
        }
        GroovesharkClient.setSession(session);

        String commtoken = gson.fromJson(GroovesharkClient.sendRequest("getCommunicationToken", new HashMap<String, Object>() {{
            put("secretKey", Utils.md5(GroovesharkClient.getSession()));
        }}), Response.class).getResult();
        GroovesharkClient.setCommtoken(commtoken);
        // commtoken expires after 25 minutes
        GroovesharkClient.setTokenExpires(new Date().getTime() + ((1000 * 60) * 25));

        Country country = gson.fromJson(GroovesharkClient.sendRequest("getCountry", null), CountryResponse.class).getResult();
        if(country != null)
            GroovesharkClient.setCountry(country);
        
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            Main.getMainFrame().initDone();
        }});
        
        latch.countDown();
    }

    class Response {
        private HashMap<String, Object> header;
        private String result;
        private HashMap<String, Object> fault;

        public Response(HashMap<String, Object> header, String result) {
            this.header = header;
            this.result = result;
        }

        public HashMap<String, Object> getHeader() {
            return this.header;
        }

        public String getResult() {
            return this.result;
        }

        public HashMap<String, Object> getFault() {
            return this.fault;
        }
    }
    
    class CountryResponse {
        private HashMap<String, Object> header;
        private Country result;
        private HashMap<String, Object> fault;

        public CountryResponse(HashMap<String, Object> header, Country result) {
            this.header = header;
            this.result = result;
        }

        public HashMap<String, Object> getHeader() {
            return this.header;
        }

        public Country getResult() {
            return this.result;
        }

        public HashMap<String, Object> getFault() {
            return this.fault;
        }
    }
}

