package com.groovesquid.gui;

import com.groovesquid.Main;
import com.groovesquid.model.Artist;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ArtistSearchTableModel extends AbstractTableModel {

    private String[] columnNames = { Main.getLocaleString("NAME") };
    
    private List<Artist> artists = new ArrayList<Artist>();

    public ArtistSearchTableModel() {
        
    }
    
    public ArtistSearchTableModel(List<Artist> artists) {
        this.artists = artists;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return artists.size();
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        Artist artist = artists.get(row);

        switch (col) {
            case 0: return artist.getName();
        }
        return null;
    }
    
    public List<Artist> getArtists() {
        return artists;
    }
        
    public void removeRow(int row) {
        artists.remove(row);
        fireTableStructureChanged();
    }
    
    public void removeRow(Artist song) {
        artists.remove(song);
        fireTableStructureChanged();
    }
    
    public void addRow(Artist song) {
        artists.add(song);
        fireTableStructureChanged();
    }
}
