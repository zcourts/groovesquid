package com.groovesquid.model;

import java.util.List;

public class Playlist {
    private String id;
    private String name;
    private String author;
    private String numSongs;
    private List<Song> songs;
    
    public Playlist(Object id, Object name, Object author, Object numSongs) {
        this.id = id.toString();
        this.name = name.toString();
        this.author = author.toString();
        this.numSongs = numSongs.toString();
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public String getNumSongs() {
        int numSongsIndexOf = numSongs.indexOf(".");
        if(numSongsIndexOf > 0) {
            return numSongs.substring(0, numSongsIndexOf);
        }
        return numSongs;
    }

    public String toString() {
        return getName();
    }
}
