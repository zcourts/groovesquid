package com.groovesquid.model;

public enum Hoster {
    NETEASE("Netease"), DEEZER("Deezer"), SOUNDCLOUD("Soundcloud");

    private final String text;

    Hoster(final String text) {
        this.text = text;
    }

    public String toString() {
        return text;
    }
}
