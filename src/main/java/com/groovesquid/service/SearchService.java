package com.groovesquid.service;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.groovesquid.model.Album;
import com.groovesquid.model.Artist;
import com.groovesquid.model.Song;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class SearchService extends HttpService {

    public List<Song> searchSongsByQuery(String query) {
        String response = null;
        try {
            response = get("http://search.musicbrainz.org/ws/2/recording/?query=" + URLEncoder.encode("\"" + query + "\"", "UTF-8") + "&fmt=json&dismax=true&limit=100");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        JsonArray recordings = JsonObject.readFrom(response).get("recording-list").asObject().get("recording").asArray();

        List<Song> songs = new ArrayList<Song>();

        recordingsLoop:
        for (JsonValue recording : recordings) {
            List<Artist> artists = new ArrayList<Artist>();
            for (JsonValue artistsJson : recording.asObject().get("artist-credit").asObject().get("name-credit").asArray()) {
                artists.add(new Artist(artistsJson.asObject().get("artist").asObject().get("id").asString(), artistsJson.asObject().get("artist").asObject().get("name").asString()));
            }

            Album album;
            Calendar date = null;

            if (recording.asObject().get("release-list") != null) {
                JsonObject release = recording.asObject().get("release-list").asObject().get("release").asArray().get(0).asObject();
                date = Calendar.getInstance();
                if (release.get("date") != null) {
                    try {
                        if (release.get("date").asString().length() == 4) {
                            date.setTime(new SimpleDateFormat("Y").parse(release.get("date").asString()));
                        } else if (release.get("date").asString().length() == 7) {
                            date.setTime(new SimpleDateFormat("Y-m").parse(release.get("date").asString()));
                        } else if (release.get("date").asString().length() == 10) {
                            date.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(release.get("date").asString()));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                album = new Album(release.get("id").asString(), release.get("title").asString(), artists, date);
            } else {
                album = new Album("", "", artists, null);
            }

            Iterator<Song> i = songs.iterator();
            while (i.hasNext()) {
                Song song = i.next();
                if (song.getName().equalsIgnoreCase(recording.asObject().get("title").asString()) && song.getArtists().get(0).getId().equals(artists.get(0).getId())) {
                    if (date != null && date.getTime().before(song.getAlbum().getReleaseDate().getTime())) {
                        i.remove();
                    } else {
                        continue recordingsLoop;
                    }
                }
            }

            Song song = new Song(recording.asObject().get("id").asString(), recording.asObject().get("title").asString(), artists, album, recording.asObject().get("length") != null ? recording.asObject().get("length").asLong() : 0);
            songs.add(song);
        }

        return songs;
    }


    public List<Album> searchAlbumsByQuery(String query) {
        return null;
    }
}
