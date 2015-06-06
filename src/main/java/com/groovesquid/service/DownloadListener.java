package com.groovesquid.service;

import com.groovesquid.model.Track;

public interface DownloadListener {
    void statusChanged(Track track);

    void downloadedBytesChanged(Track track);
}
