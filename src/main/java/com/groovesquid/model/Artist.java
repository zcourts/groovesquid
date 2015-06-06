package com.groovesquid.model;

public class Artist {
    private String id;
    private String name;

    public Artist(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Artist(String name) {
        this.name = name.trim();
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

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Artist other = (Artist) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return !((this.name == null) ? (other.name != null) : !this.name.equals(other.name));
    }
}
