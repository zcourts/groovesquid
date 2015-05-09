package com.groovesquid.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Song {
    private String id;
    private String name;
    private List<Artist> artists = new ArrayList<Artist>();
    private Album album;
    private long duration;
    private String year;
    private long trackNum, orderNum;
    private boolean downloaded;

    public Song(String id, String name, List<Artist> artists) {
        this(id, name, artists, null, 0);
    }

    public Song(String id, Object name, List<Artist> artists, Album album, long duration) {
        this.id = id;
        this.name = name.toString().trim();
        this.artists = artists;
        this.album = album;

        this.duration = duration;

        if (year != null) {
            this.year = year;
        } else {
            this.year = "";
        }

        this.trackNum = trackNum;
        this.orderNum = trackNum;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public List<Artist> getArtists() {
        return artists;
    }
    
    public Album getAlbum() {
        return album;
    }
    
    public String getReadableDuration() {
        if (duration > 0) {
            return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
            );
        } else {
            return "";
        }
    }

    public String getArtistNames() {
        StringBuilder builder = new StringBuilder();
        for (Artist artist : artists) {
            builder.append(artist.getName());
            builder.append(", ");
        }
        String artistNames = builder.toString();
        artistNames = artistNames.substring(0, artistNames.length() - 2);
        return artistNames;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getYear() {
        if(!year.isEmpty()) {
            return year;
        } else {
            return "";
        }
    }
    
    public Long getTrackNum() {
        return trackNum;
    }
    
    public Long getOrderNum() {
    	return orderNum;
    }
    
    @Override
    public String toString() {
        return "Song" + "{songID=" + id + ", songName='" + name + '\'' + '}';
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }
}
