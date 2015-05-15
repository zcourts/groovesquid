package com.groovesquid.gui;

import com.groovesquid.Groovesquid;
import com.groovesquid.model.Song;
import com.groovesquid.model.Track;
import com.groovesquid.util.I18n;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class DownloadTableModel extends AbstractTableModel {

    private String[] columnNames = {I18n.getLocaleString("SONG"), I18n.getLocaleString("ARTIST"), I18n.getLocaleString("ALBUM"), I18n.getLocaleString("PATH"), I18n.getLocaleString("DATE"), I18n.getLocaleString("PROGRESS"), I18n.getLocaleString("HOSTER")};

    private List<Track> songDownloads = new ArrayList<Track>();

    public DownloadTableModel() {
        
    }
    
    public DownloadTableModel(List<Song> songs) {
        for (Song song : songs) {
            Track track = Groovesquid.getDownloadService().download(song);
            songDownloads.add(track);
            fireTableDataChanged();
        }
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return songDownloads == null ? 0 : songDownloads.size();
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Track track = songDownloads.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return track.getSong() != null && track.getSong().getName() != null ? track.getSong().getName() : "";
            case 1:
                return track.getSong() != null && track.getSong().getArtists() != null ? track.getSong().getArtistNames() : "";
            case 2:
                return track.getSong() != null && track.getSong().getAlbum() != null && track.getSong().getAlbum().getName() != null ? track.getSong().getAlbum().getName() : "";
            case 3:
                return track.getPath() != null ? track.getPath() : "";
            case 4:
                return track.getDate() != null ? track.getDate() : "";
            case 5: return track.getProgress();
            case 6:
                return track.getHoster() != null ? track.getHoster() : "";
        }
        return null;
    }
    
    public void setValueAt(Object obj, Track songDownload, int col) {
        int index = songDownloads.indexOf(songDownload);
        setValueAt(obj, col, index);
        fireTableCellUpdated(index, col);
        updateSongDownloads();
    }
    
    /*@Override
    public Class<?> getColumnClass(int column) {
        if(songDownloads.size() > 0 && getRowCount() > 0) {
            return getValueAt(0, column).getClass();
        } else {
            return Object.class;
        }
    }*/
    
    public List<Track> getSongDownloads() {
        return songDownloads;
    }
    
    public void setSongDownloads(List<Track> songDownloads) {
        this.songDownloads = songDownloads;
        fireTableDataChanged();
        updateSongDownloads();
    }
    
    public Track getSongDownload(Song song) {
        for(Track songDownload : songDownloads) {
            if(songDownload.getSong().equals(song)) {
                return songDownload;
            }
        }
        return null;
    }
    
    public int getSongDownloadIndex(Song song) {
        for(Track songDownload : songDownloads) {
            if(songDownload.getSong().equals(song)) {
                return songDownloads.indexOf(songDownload);
            }
        }
        return -1;
    }
        
    public void removeRow(int row) {
        songDownloads.remove(row);
        //fireTableDataChanged();
        fireTableRowsDeleted(row, row);
        updateSongDownloads();
    }
    
    public void removeRow(Track songDownload) {
        songDownloads.remove(songDownload);
        //fireTableDataChanged();
        updateSongDownloads();
    }
    
    public void addRow(Track songDownload) {
        songDownloads.add(songDownload);
        fireTableDataChanged();
        updateSongDownloads();
    }

    public void addRow(int row, Track songDownload) {
        songDownloads.add(row, songDownload);
        fireTableDataChanged();
        updateSongDownloads();
    }
    
    public void updateSongDownloads() {
        Groovesquid.getConfig().setDownloads(songDownloads);
    }

    public void fireTableCellUpdated(Song song, int col) {
        fireTableCellUpdated(getSongDownloadIndex(song), col);
    }

}
