package com.groovesquid.model;

public class Artist {
    private String id;
    private String name;

    public Artist(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Artist(String name) {
        this.name = name.toString().trim();
    }

    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public String toString() {
        return getName();
    }
}
