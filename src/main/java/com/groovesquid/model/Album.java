package com.groovesquid.model;

import java.util.Calendar;
import java.util.List;

public class Album {
    private String id;
    private String name;
    private List<Artist> artists;
    private String imageUrl;
    private Calendar releaseDate;

    public Album(String id, String name, List<Artist> artists, Calendar releaseDate) {
        this.id = id;
        this.name = name;
        this.artists = artists;
        this.releaseDate = releaseDate;
    }

    public String getArtistNames() {
        StringBuilder builder = new StringBuilder();
        for(Artist artist : artists) {
            builder.append(artist.getName());
            builder.append(", ");
        }
        String artistNames = builder.toString();
        artistNames = artistNames.substring(0, artistNames.length() - 2);
        return artistNames;
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
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }

    public String toString() {
        return getName();
    }

    public Calendar getReleaseDate() {
        return releaseDate;
    }

    public String getReleaseYear() {
        return releaseDate != null ? String.valueOf(releaseDate.get(Calendar.YEAR)) : "";
    }
}
