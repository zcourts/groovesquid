package com.groovesquid.model;

import com.groovesquid.util.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Track {

    private final Song song;
    private transient Store store;
    private long totalBytes;
    private long downloadedBytes;
    private Status status;
    private Date date;
    private String path;
    private Long startDownloadTime;
    private Long stopDownloadTime;
    private Double downloadRate;
    private String downloadUrl, streamKey, streamServerID;

    public static enum Status {
        QUEUED, INITIALIZING, DOWNLOADING, FINISHED, CANCELLED, ERROR;

        public boolean isSuccessful() {
            return this == FINISHED;
        }

        public boolean isFinished() {
            return this == FINISHED || this == CANCELLED || this == ERROR;
        }
        
        public boolean isDownloading() {
            return this == DOWNLOADING;
        }
    }

    public Track(Song song, Store store) {
        this.song = song;
        this.store = store;
        this.totalBytes = 0;
        this.downloadedBytes = 0;
        this.status = Status.QUEUED;
        this.date = new Date();
        this.path = store.getDescription();
        this.startDownloadTime = 0L;
        this.stopDownloadTime = 0L;
        this.downloadRate = null;
    }
    
    public Song getSong() {
        return song;
    }
    
    public Store getStore() {
        return store;
    }
    
    public void setStore(Store store) {
        this.store = store;
    }
    
    public int getProgress() {
        return (int)(totalBytes > 0 ? (downloadedBytes * 100.0) / totalBytes : 0.0);
    }
    
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        if (!this.status.isFinished()) {
            this.status = status;
        }
    }
    
    public long getTotalBytes() {
        return totalBytes;
    }
    
    public long getDownloadedBytes() {
        return downloadedBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }
    
    public void updateDownloadedBytes() {
        downloadedBytes++;
    }
    
    public void incDownloadedBytes(long increment) {
        this.downloadedBytes += increment;
    }
    
    public String getDownloadedSize() {
        return Utils.humanReadableByteCount(downloadedBytes, true);
    }
    
    public String getSize() {
        return Utils.humanReadableByteCount(totalBytes, true);
    }
    
    public String getPath() {
        return path;
    }
    
    public String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(date);
    }
    
    public long getStartDownloadTime() {
        return startDownloadTime;
    }
    
    public void setStartDownloadTime(long startDownloadTime) {
        this.startDownloadTime = startDownloadTime;
    }
    
    public long getStopDownloadTime() {
        return stopDownloadTime;
    }
    
    public void setStopDownloadTime(long stopDownloadTime) {
        this.stopDownloadTime = stopDownloadTime;
    }
    
    public Long getDownloadDuration() {
        if (startDownloadTime == null || startDownloadTime == 0)
            return null;
        else if (stopDownloadTime != null && stopDownloadTime > 0)
            return stopDownloadTime - startDownloadTime;
        else
            return System.currentTimeMillis() - startDownloadTime;
    }

    public Double getDownloadRate() {
        if(status == Status.FINISHED && downloadRate != null) {
            return downloadRate;
        }
        Long downloadDuration = getDownloadDuration();
        if (downloadDuration != null && downloadDuration > 0 && downloadedBytes > 0)
            downloadRate = (double) downloadedBytes / downloadDuration * 1000.0;
        else
            downloadRate = 0.0D;
        return downloadRate;
    }

    public String getStreamKey() {
        return streamKey;
    }

    public void setStreamKey(String streamKey) {
        this.streamKey = streamKey;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getStreamServerID() {
        return streamServerID;
    }

    public void setStreamServerID(String streamServerID) {
        this.streamServerID = streamServerID;
    }

}
