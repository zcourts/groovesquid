package com.groovesquid.gui;

import com.groovesquid.model.Song;
import com.groovesquid.util.I18n;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class SongSearchTableModel extends AbstractTableModel {

    private final String[] columnNames = {"", I18n.getLocaleString("SONG"), I18n.getLocaleString("ARTIST"), I18n.getLocaleString("ALBUM"), I18n.getLocaleString("DURATION"), I18n.getLocaleString("YEAR")/*, I18n.getLocaleString("TRACK_NUMBER")*/};
    
    private List<Song> songs = new ArrayList<Song>();

    public SongSearchTableModel() {
        
    }
    
    public SongSearchTableModel(List<Song> songs) {
        this.songs = songs;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return songs.size();
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        Song song = songs.get(row);

        switch (col) {
            case 0:
                return song.isDownloaded();
            case 1:
                return song.getName();
            case 2:
                return song.getArtistNames();
            case 3:
                return song.getAlbum();
            case 4:
                return song.getReadableDuration();
            case 5:
                return song.getAlbum().getReleaseYear();
            /*case 6:
                return song.getTrackNum();*/
        }
        return null;
    }
    
    public List<Song> getSongs() {
        return songs;
    }
    
    /*@Override
    public Class<?> getColumnClass(int column) {
        if(songs.size() > 0 && getRowCount() > 0) {
            return getValueAt(0, column).getClass();
        } else {
            return Object.class;
        }
    }*/
    
    public void removeRow(int row) {
        songs.remove(row);
        fireTableRowsDeleted(row, row);
    }
    
    public void removeRow(Song song) {
        int row = this.songs.indexOf(song);
        songs.remove(song);
        fireTableRowsDeleted(row, row);
    }
    
    public void addRow(Song song) {
        songs.add(song);
        int row = this.songs.indexOf(song);
        fireTableRowsInserted(row, row);
    }
    
    public void addRows(List<Song> songs) {
        int from = this.songs.size() - 1;
        this.songs.addAll(songs);
        fireTableRowsInserted(from, this.songs.size() - 1);
    }
    
    public void setRows(List<Song> songs) {
        this.songs = songs;
        fireTableRowsUpdated(0, this.songs.indexOf(this.songs.get(this.songs.size() - 1)));
        fireTableStructureChanged();
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if(columnIndex == 0) {
            return true;
        } else {
            return super.isCellEditable(rowIndex, columnIndex);
        }
    }
    
}
