/*
 * Copyright (C) 2013 Maino
 * 
 * This work is licensed under the Creative Commons
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of
 * this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send
 * a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco,
 * California, 94105, USA.
 * 
 */

package groovesquid;

import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author Maino
 */
public class GetAdsThread extends Thread {
    private final static Logger log = Logger.getLogger(Main.class.getName());
    private static final Gson gson = new Gson();
    private static final String getAdsUrl = "http://groovesquid.com/ads/inc/api.php?getAds";
    private List<AdsResponse.Ad> ads;
    private final JEditorPane adPane;
    
    public GetAdsThread(JEditorPane adPane) {
        this.adPane = adPane;
    }
    
    @Override
    public void run() {
        AdsResponse adsResponse = gson.fromJson(getFile(getAdsUrl), AdsResponse.class);
        ads = adsResponse.getAds();
        if(ads.size() > 0) {
            Random randomGenerator = new Random();
            int index = randomGenerator.nextInt(ads.size());
            
            AdsResponse.Ad ad = ads.get(index);
            
            HTMLEditorKit kit = new HTMLEditorKit();
            adPane.setEditorKit(kit);
            
            StyleSheet styleSheet = kit.getStyleSheet();
            styleSheet.addRule("body { width: 160px; margin: 0; padding: 10px 0 0 0; }");
            //styleSheet.addRule("div { width: 160px; height:600px; position:absolute; top:50%; margin-top:-300px; }");
            String result = "<html><body><div><a href=\"" + ad.getUrl() + "\"><img src=\"" + ad.getImage() + "\" alt=\"" + ad.getTitle() + "\" border=\"0\"/></a></div></body></html>";
            Document doc = kit.createDefaultDocument();
            adPane.setDocument(doc);
            adPane.setText(result);
            
            System.out.println(result);
        }

    }
    
    public static String getFile(String url) {
        String responseContent = null;
        HttpEntity httpEntity = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
            httpGet.setHeader(HTTP.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31");
            
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpResponse httpResponse = httpClient.execute(httpGet);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            httpEntity = httpResponse.getEntity();
            
            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                httpEntity.writeTo(baos);
            } else {
                throw new RuntimeException(url);
            }

            responseContent = baos.toString("UTF-8");
        } catch (Exception ex) {
            log.log(Level.SEVERE, null, ex);
        } finally {
            try {
                EntityUtils.consume(httpEntity);
            } catch (IOException ex) {
                log.log(Level.SEVERE, null, ex);
            }
        }
        return responseContent;
    }
    
    public class AdsResponse {
        private List<Ad> ads;
        
        public class Ad {
            private String title;
            private String image;
            private String url;

            public String getTitle() {
                return title;
            }

            public String getImage() {
                return image;
            }

            private String getUrl() {
                return url;
            }
        }
        
        public List<Ad> getAds() {
            return ads;
        }
    }
}
