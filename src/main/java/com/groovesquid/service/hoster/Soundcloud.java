package com.groovesquid.service.hoster;

import com.eclipsesource.json.JsonArray;
import com.groovesquid.model.Track;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

public class Soundcloud extends Hoster {

    public Soundcloud() {
        setName("SoundCloud");
    }

    public String getDownloadUrl(Track track) {
        String response = null;
        try {
            response = get("http://api.soundcloud.com/tracks/?q=" + URLEncoder.encode(track.getSong().getArtistNames().replaceAll(",", "") + " " + track.getSong().getName(), "UTF-8") + "&client_id=b45b1aa10f1ac2941910a7f0d10f8e28", Arrays.asList(browserHeaders));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        JsonArray songs = JsonArray.readFrom(response);
        if (songs != null && !songs.isEmpty()) {
            return "https://api.soundcloud.com/tracks/" + songs.get(0).asObject().get("id").asLong() + "/stream?client_id=b45b1aa10f1ac2941910a7f0d10f8e28";
        }
        return null;
    }

}
