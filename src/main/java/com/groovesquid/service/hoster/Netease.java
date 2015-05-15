package com.groovesquid.service.hoster;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.groovesquid.model.Track;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Netease extends Hoster {

    public Netease() {
        setName("Netease");
    }

    public String getDownloadUrl(Track track) {
        List<NameValuePair> data = new ArrayList<NameValuePair>();
        data.add(new BasicNameValuePair("s", track.getSong().getArtistNames().replaceAll(",", "") + " " + track.getSong().getName()));
        data.add(new BasicNameValuePair("type", "1"));
        data.add(new BasicNameValuePair("offset", "0"));
        data.add(new BasicNameValuePair("limit", "10"));
        data.add(new BasicNameValuePair("total", "true"));
        List<Header> headers = new ArrayList<Header>(Arrays.asList(browserHeaders));
        headers.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));
        headers.add(new BasicHeader("Referer", "http://music.163.com/search/"));
        String response = post("http://music.163.com/api/search/get", data, headers);

        if (response == null) {
            return null;
        }

        JsonObject result = JsonObject.readFrom(response).get("result").asObject();
        if (result != null && result.get("songCount").asInt() > 0) {
            long songId = 0;
            for (JsonValue song : result.get("songs").asArray()) {
                if (song.asObject().get("name").asString().equalsIgnoreCase(track.getSong().getName())) {
                    songId = song.asObject().get("id").asLong();
                    break;
                }
            }
            if (songId == 0) {
                songId = result.get("songs").asArray().get(0).asObject().get("id").asLong();
            }

            response = get("http://music.163.com/api/song/detail/?id=" + songId + "&ids=[" + songId + "]");

            if (JsonObject.readFrom(response).get("songs") != null && !JsonObject.readFrom(response).get("songs").asArray().isEmpty()) {
                JsonObject song = JsonObject.readFrom(response).get("songs").asArray().get(0).asObject();

                if (song.get("hMusic") != null) {
                    return makeNeteaseUrl(song.get("hMusic").asObject().get("dfsId").asLong());
                } else if (song.get("mp3Url") != null) {
                    return song.get("mp3Url").asString();
                } else if (song.get("bMusic") != null) {
                    return makeNeteaseUrl(song.get("bMusic").asObject().get("dfsId").asLong());
                }
            }
        }

        return null;
    }

    private String makeNeteaseUrl(Long dfsId) {
        byte[] byte1 = "3go8&$8*3*3h0k(2)2".getBytes();
        byte[] byte2 = dfsId.toString().getBytes();
        for (int i = 0; i < byte2.length; i++) {
            byte2[i] = (byte) (byte2[i] ^ byte1[i % byte1.length]);
        }
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byte2);

            String encId = Base64.encodeBase64String(md5.digest());
            encId = encId.replace('/', '_');
            encId = encId.replace('+', '-');

            return String.format("http://m1.music.126.net/%s/%s.mp3", encId, dfsId);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }


}
