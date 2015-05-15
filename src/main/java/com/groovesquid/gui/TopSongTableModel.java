package com.groovesquid.gui;

import com.groovesquid.model.Song;
import com.groovesquid.util.I18n;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class TopSongTableModel extends AbstractTableModel {

    private final String[] columnNames = {"", I18n.getLocaleString("SONG"), I18n.getLocaleString("ARTIST")};

    private List<Song> songs = new ArrayList<Song>();

    public TopSongTableModel() {

    }

    public TopSongTableModel(List<Song> songs) {
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
                return song.getArtists() != null ? song.getArtistNames() : "";
        }
        return null;
    }

    public List<Song> getSongs() {
        return songs;
    }

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
        if (columnIndex == 0) {
            return true;
        } else {
            return super.isCellEditable(rowIndex, columnIndex);
        }
    }

}
