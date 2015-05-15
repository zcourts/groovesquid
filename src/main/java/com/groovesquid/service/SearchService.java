package com.groovesquid.service;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.groovesquid.model.Album;
import com.groovesquid.model.Artist;
import com.groovesquid.model.Song;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SearchService extends HttpService {

    public List<Song> getSongsByQuery(String query) {
        String response = null;
        try {
            response = get("http://search.musicbrainz.org/ws/2/recording/?query=" + URLEncoder.encode("\"" + query + "\"", "UTF-8") + "&fmt=json&dismax=true&limit=100");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        JsonValue recordings = JsonObject.readFrom(response).get("recording-list").asObject().get("recording");

        List<Song> songs = new ArrayList<Song>();

        if (recordings == null || recordings.asArray().isEmpty()) {
            return songs;
        }

        recordingsLoop:
        for (JsonValue recording : recordings.asArray()) {
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

    public List<Song> getSongsByAlbum(Album album) {
        String response = null;
        try {
            System.out.println(album.getId());
            response = get("http://search.musicbrainz.org/ws/2/recording/?query=" + URLEncoder.encode("reid:" + album.getId(), "UTF-8") + "&fmt=json&limit=100");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        JsonValue recordings = JsonObject.readFrom(response).get("recording-list").asObject().get("recording");

        List<Song> songs = new ArrayList<Song>();

        if (recordings == null || recordings.asArray().isEmpty()) {
            return songs;
        }

        for (JsonValue recording : recordings.asArray()) {
            List<Artist> artists = new ArrayList<Artist>();
            for (JsonValue artistsJson : recording.asObject().get("artist-credit").asObject().get("name-credit").asArray()) {
                artists.add(new Artist(artistsJson.asObject().get("artist").asObject().get("id").asString(), artistsJson.asObject().get("artist").asObject().get("name").asString()));
            }

            Song song = new Song(recording.asObject().get("id").asString(), recording.asObject().get("title").asString(), artists, album, recording.asObject().get("length") != null ? recording.asObject().get("length").asLong() : 0);
            songs.add(song);
        }

        return songs;
    }

    public List<Album> getAlbumsByQuery(String query) {
        String response = null;
        try {
            response = get("http://search.musicbrainz.org/ws/2/release/?query=" + URLEncoder.encode("\"" + query + "\"", "UTF-8") + "&fmt=json&dismax=true&limit=100");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        JsonValue releases = JsonObject.readFrom(response).get("release-list").asObject().get("release");

        List<Album> albums = new ArrayList<Album>();

        if (releases == null || releases.asArray().isEmpty()) {
            return albums;
        }

        for (JsonValue release : releases.asArray()) {
            List<Artist> artists = new ArrayList<Artist>();
            for (JsonValue artistsJson : release.asObject().get("artist-credit").asObject().get("name-credit").asArray()) {
                artists.add(new Artist(artistsJson.asObject().get("artist").asObject().get("id").asString(), artistsJson.asObject().get("artist").asObject().get("name").asString()));
            }
            Calendar date = Calendar.getInstance();
            if (release.asObject().get("date") != null) {
                try {
                    if (release.asObject().get("date").asString().length() == 4) {
                        date.setTime(new SimpleDateFormat("Y").parse(release.asObject().get("date").asString()));
                    } else if (release.asObject().get("date").asString().length() == 7) {
                        date.setTime(new SimpleDateFormat("Y-m").parse(release.asObject().get("date").asString()));
                    } else if (release.asObject().get("date").asString().length() == 10) {
                        date.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(release.asObject().get("date").asString()));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            albums.add(new Album(release.asObject().get("id").asString(), release.asObject().get("title").asString(), artists, date));
        }

        return albums;
    }

    public List<Artist> getArtistsByQuery(String query) {
        List<Artist> artists = new ArrayList<Artist>();

        String response = null;
        try {
            response = get("http://search.musicbrainz.org/ws/2/artist/?query=" + URLEncoder.encode("\"" + query + "\"", "UTF-8") + "&fmt=json&dismax=true&limit=100");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return artists;
    }

    public List<Song> getTopItunesSongs() {
        List<Song> songs = new ArrayList<Song>();
        String response = get("https://itunes.apple.com/us/rss/topsongs/limit=100/explicit=true/json", Arrays.asList(browserHeaders));
        JsonValue entries = JsonObject.readFrom(response).get("feed").asObject().get("entry");
        if (entries != null && entries.isArray()) {
            for (JsonValue entry : entries.asArray()) {
                List<Artist> artists = new ArrayList<Artist>();
                artists.add(new Artist(null, entry.asObject().get("im:artist").asObject().get("label").asString()));
                Album album = new Album(null, entry.asObject().get("im:collection").asObject().get("im:name").asObject().get("label").asString(), artists, null);
                songs.add(new Song(null, entry.asObject().get("im:name").asObject().get("label").asString(), artists, album, 0));
            }
        }
        return songs;
    }

    public List<Song> getTopBeatportSongs() {
        List<Song> songs = new ArrayList<Song>();
        String response = get("https://pro.beatport.com/top-100", Arrays.asList(browserHeaders));
        String playables = StringUtils.substringBetween(response, "window.Playables = ", ";");
        JsonValue tracks = JsonObject.readFrom(playables).get("tracks");
        if (tracks != null && tracks.isArray()) {
            for (JsonValue track : tracks.asArray()) {
                List<Artist> artists = new ArrayList<Artist>();
                for (JsonValue artist : track.asObject().get("artists").asArray()) {
                    artists.add(new Artist(null, artist.asObject().get("name").asString()));
                }
                Album album = new Album(null, track.asObject().get("label").asObject().get("name").asString(), artists, null);
                songs.add(new Song(null, track.asObject().get("title").asString(), artists, album, track.asObject().get("duration").asObject().get("milliseconds").asLong()));
            }
        }
        return songs;
    }
}
