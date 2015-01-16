package com.groovesquid;

import com.google.gson.Gson;
import com.groovesquid.model.Clients;
import com.groovesquid.model.Clients.Client;
import com.groovesquid.model.Country;
import com.groovesquid.model.JsonRequest;
import com.groovesquid.util.I18n;
import com.groovesquid.util.Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GroovesharkClient {

    private final static Logger log = Logger.getLogger(GroovesharkClient.class.getName());

    private String[] jsQueueMethods = {"getCountry", "getStreamKeyFromSongIDEx", "markSongDownloadedEx"};
    // http://www.scilor.com/grooveshark/xml/GrooveFix.xml
    private Clients clients = Main.getConfig().getClients();
    private HashMap<String, Object> header = new HashMap<String, Object>();
    private String commToken = "";
    private String session = "";
    private String uuid = UUID.randomUUID().toString();
    private Country country = new Country();
    private long tokenExpires = 0;
    private String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31";
    private Gson gson = new Gson();

    public GroovesharkClient() {
        init();
    }

    public String sendRequest(String method, HashMap<String, Object> parameters) {
        if(tokenExpires <= new Date().getTime() && tokenExpires != 0 && !"initiateSession".equals(method) && !"getCommunicationToken".equals(method) && !"getCountry".equals(method)) {
            init();
        }

        String responseContent = null;
        HttpEntity httpEntity = null;
        try {
            Client client = clients.getHtmlshark();

            String protocol = "http://";

            if(method.equals("getCommunicationToken")) {
                protocol = "https://";
            }

            String url = protocol + "grooveshark.com/more.php?" + method;

            for (String jsqueueMethod : jsQueueMethods) {
                if(jsqueueMethod.equals(method)) {
                    client = clients.getJsqueue();
                    break;
                }
            }

            header.put("client", client.getName());
            header.put("clientRevision", client.getRevision());
            header.put("privacy", "0");
            header.put("uuid", uuid);
            header.put("country", country);
            if(!method.equals("initiateSession")) {
                header.put("session", session);
                header.put("token", generateToken(method, client.getSecret()));
            }

            String jsonString = gson.toJson(new JsonRequest(header, parameters, method));
            log.info(">>> " + jsonString);

            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json");
            httpPost.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
            httpPost.setHeader(HTTP.USER_AGENT, userAgent);
            httpPost.setHeader("Referer", "http://grooveshark.com/JSQueue.swf?" + client.getRevision());
            httpPost.setHeader("Content-Language", "en-US");
            httpPost.setHeader("Cache-Control", "max-age=0");
            httpPost.setHeader("Accept", "*/*");
            httpPost.setHeader("Accept-Charset", "utf-8,ISO-8859-1;q=0.7,*;q=0.3");
            httpPost.setHeader("Accept-Language", "de-DE,de;q=0.8,en-US;q=0.6,en;q=0.4");
            httpPost.setHeader("Accept-Encoding", "gzip,deflate,sdch");
            httpPost.setHeader("Origin", "http://grooveshark.com");
            if(!method.equals("initiateSession")) {
                httpPost.setHeader("Cookie", "PHPSESSID=" + session);
            }
            httpPost.setEntity(new StringEntity(jsonString, "UTF-8"));
       
            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
            if(Main.getConfig().getProxyHost() != null && Main.getConfig().getProxyPort() != null) {
                httpClientBuilder.setProxy(new HttpHost(Main.getConfig().getProxyHost(), Main.getConfig().getProxyPort()));
            }
            HttpClient httpClient = httpClientBuilder.build();
            HttpResponse httpResponse = httpClient.execute(httpPost);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            httpEntity = httpResponse.getEntity();
            
            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                httpEntity.writeTo(baos);
            } else {
                throw new RuntimeException("method " + method + ": " + statusLine);
            }

            responseContent = baos.toString("UTF-8");

        } catch (Exception ex) {
            Logger.getLogger(GroovesharkClient.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                EntityUtils.consume(httpEntity);
            } catch (IOException ex) {
                Logger.getLogger(GroovesharkClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        log.info("<<< " + responseContent);
        return responseContent;
    }

    public void init() {
        try {
            InitTask initTask = new InitTask();
            Thread initThread = new Thread(initTask);
            initThread.start();
            initTask.getLatch().await();
        } catch (InterruptedException ex) {
            log.log(Level.SEVERE, null, ex);
        }
        log.info("session: " + session);
        log.info("commToken: " + commToken);
        log.info("country: " + country.toString());
    }

    public String generateToken(String method, String secret) throws NoSuchAlgorithmException {
        String randomHex = Utils.createRandomHexNumber(6);
        return randomHex + Utils.sha1(method + ":" + commToken + ":" + secret + ":" + randomHex);
    }

    public synchronized void setClients(Clients clients) {
        this.clients = clients;
        log.info("clients: " + clients.toString());
    }

    public Country getCountry() {
        return country;
    }

    public class InitTask implements Runnable {

        private CountDownLatch latch = new CountDownLatch(1);

        public CountDownLatch getLatch() {
            return latch;
        }

        public InitTask() {
            log.info("initializing...");
        }

        public void run() {
            // first of all, try to get session, commtoken & country via preload.php
            HttpEntity httpEntity = null;
            try {
                String preloadUrl = "http://grooveshark.com/preload.php?getCommunicationToken=1&hash=&" + System.currentTimeMillis();
                HttpGet httpGet = new HttpGet(preloadUrl);
                httpGet.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
                httpGet.setHeader(HTTP.USER_AGENT, userAgent);
                httpGet.setHeader("Accept", "*/*");
                httpGet.setHeader("Accept-Language", "en-US,en;q=0.8");
                httpGet.setHeader("Accept-Encoding", "gzip,deflate,sdch");

                HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
                if (Main.getConfig().getProxyHost() != null && Main.getConfig().getProxyPort() != null) {
                    httpClientBuilder.setProxy(new HttpHost(Main.getConfig().getProxyHost(), Main.getConfig().getProxyPort()));
                }
                HttpClient httpClient = httpClientBuilder.build();
                HttpResponse httpResponse = httpClient.execute(httpGet);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                httpEntity = httpResponse.getEntity();

                // get session cookie
                Header[] headers = httpResponse.getHeaders("Set-Cookie");
                for (Header header : headers) {
                    if (header.getValue().contains("PHPSESSID=")) {
                        session = StringUtils.substringBetween(header.getValue(), "PHPSESSID=", ";");
                        break;
                    }
                }

                StatusLine statusLine = httpResponse.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == HttpStatus.SC_OK) {
                    httpEntity.writeTo(baos);
                } else {
                    throw new RuntimeException(preloadUrl);
                }

                String preloadContent = baos.toString("UTF-8");

                // get communicationToken
                commToken = StringUtils.substringBetween(preloadContent, "\"getCommunicationToken\":\"", "\"");

                // get country
                country = gson.fromJson("{" + StringUtils.substringBetween(preloadContent, "\"country\":{", "}") + "}", Country.class);

            } catch (Exception ex) {
                Logger.getLogger(GroovesharkClient.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    EntityUtils.consume(httpEntity);
                } catch (IOException ex) {
                    Logger.getLogger(GroovesharkClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (session.isEmpty() || session == null) {
                try {
                    session = gson.fromJson(sendRequest("initiateSession", null), Response.class).getResult();
                } catch (Exception ex) {
                    Main.getMainFrame().showError(I18n.getLocaleString("ERROR_INITIATE_SESSION"));
                    return;
                }
            }

            if (commToken.isEmpty() || commToken == null) {
                commToken = gson.fromJson(sendRequest("getCommunicationToken", new HashMap<String, Object>() {{
                    put("secretKey", Utils.md5(session));
                }}), Response.class).getResult();
            }

            if (country == null) {
                country = new Country();
            }

            // commToken expires after 25 minutes
            tokenExpires = new Date().getTime() + ((1000 * 60) * 25);

            // activate main frame components
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Main.getMainFrame().initDone();
                }
            });

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
    }

}
