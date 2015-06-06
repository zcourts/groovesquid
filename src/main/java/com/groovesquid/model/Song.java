package com.groovesquid.model;

import com.groovesquid.Groovesquid;

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
    private Boolean downloaded;
    private boolean playing;

    public Song(String id, String name, List<Artist> artists) {
        this(id, name, artists, null, 0);
    }

    public Song(String id, Object name, List<Artist> artists, Album album, long duration) {
        this.id = id;
        this.name = name.toString().trim();
        this.artists = artists;
        this.album = album;

        this.duration = duration;

        /*if (year != null) {
            this.year = year;
        } else {
            this.year = "";
        }*/
        this.year = "";

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
            return String.format("%d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
            );
        } else {
            return "0:00";
        }
    }

    public String getArtistNames() {
        if (artists == null) {
            return "";
        }
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
        return "Song" + "{id=" + id + ", name='" + name + '\'' + ", artists='" + artists + '}';
    }

    public boolean isDownloaded() {
        if (downloaded == null) {
            downloaded = false;
            for (Track track : Groovesquid.getConfig().getDownloads()) {
                if (this.equals(track.getSong())) {
                    downloaded = true;
                    break;
                }
            }
        }
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    public boolean isPlaying() {
        Track currentTrack = Groovesquid.getPlayService().getCurrentTrack();
        return currentTrack != null && currentTrack.getSong().equals(this) && !Groovesquid.getPlayService().isPaused();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Song other = (Song) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.artists == null) ? (other.artists != null) : !this.artists.containsAll(other.artists) || !other.artists.containsAll(this.artists)) {
            return false;
        }
        if ((this.album == null) ? (other.album != null) : !this.album.equals(other.album)) {
            return false;
        }
        if ((this.year == null) ? (other.year != null) : !this.year.equals(other.year)) {
            return false;
        }
        return this.trackNum == other.trackNum;
    }
}
