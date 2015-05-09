package com.groovesquid.service.extractor;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.groovesquid.model.Hoster;
import com.groovesquid.model.Track;
import com.groovesquid.service.HttpService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class DeezerExtractor extends HttpService implements Extractor {

    public String getDownloadUrl(Track track) {
        track.setHoster(Hoster.DEEZER);

        String searchResponse = null;
        try {
            searchResponse = get("http://api.deezer.com/search?q=" + URLEncoder.encode(track.getSong().getArtistNames().replaceAll(",", "") + " " + track.getSong().getName(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (searchResponse != null) {
            JsonArray data = JsonObject.readFrom(searchResponse).get("data").asArray();
            if (data.isEmpty()) {
                return null;
            }
            Long trackId = data.get(0).asObject().get("id").asLong();
            String trackResponse = get("https://api.deezer.com/track/" + trackId + "?output=json");
            //String rezeedResponse = get("https://cdn-proxy-a.rezeed.cc/api/1/" +  + ".mp3");
        }

        return null;
    }

}
