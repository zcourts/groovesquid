package com.groovesquid.gui;

import com.groovesquid.Main;
import com.groovesquid.model.Album;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class AlbumSearchTableModel extends AbstractTableModel {

    private String[] columnNames = { Main.getLocaleString("NAME"), Main.getLocaleString("ARTIST") };
    
    private List<Album> albums = new ArrayList<Album>();

    public AlbumSearchTableModel() {
        
    }
    
    public AlbumSearchTableModel(List<Album> albums) {
        this.albums = albums;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return albums.size();
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        Album album = albums.get(row);

        switch (col) {
            case 0: return album.getName();
            case 1: return album.getArtist().getName();
        }
        return null;
    }
    
    public List<Album> getAlbums() {
        return albums;
    }
        
    public void removeRow(int row) {
        albums.remove(row);
        fireTableStructureChanged();
    }
    
    public void removeRow(Album song) {
        albums.remove(song);
        fireTableStructureChanged();
    }
    
    public void addRow(Album song) {
        albums.add(song);
        fireTableStructureChanged();
    }
}
