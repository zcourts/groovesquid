package com.groovesquid.gui;

import com.groovesquid.model.Track;

import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

@SuppressWarnings("serial")
public class ProgressCellRenderer extends DefaultTableCellRenderer {

    private final JPanel panel;
    private final JProgressBar b;
    
    public ProgressCellRenderer() {
        super();
        setOpaque(true);
        b = new JProgressBar();

        b.setStringPainted(true);
        b.setMinimum(0);
        b.setMaximum(100);

        b.setBorderPainted(true);
        b.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        //b.setPreferredSize(new Dimension(1,1));
        b.setOpaque(true);

        b.setUI(new BasicProgressBarUI());

        panel = new JPanel(new BorderLayout());
        panel.add(b, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
    }

    @Override
    public boolean isDisplayable() { 
        return true; 
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        String text = "";
        Track track = ((DownloadTableModel)table.getModel()).getSongDownloads().get(row);
        
        if(track != null) {
            Double downloadRate = track.getDownloadRate();
            if(track.getStatus() != null) {
                switch (track.getStatus()) {
                    case INITIALIZING:
                        text = "initializing...";
                        break;
                    case QUEUED:
                        text = "waiting...";
                        break;
                    case DOWNLOADING:
                        if (downloadRate != null) {
                            text = String.format("%1.0f%%, %d of %d kB, %1.0f kB/s",
                                    track.getProgress() * 1.0,
                                    track.getDownloadedBytes() / 1024,
                                    track.getTotalBytes() / 1024,
                                    downloadRate / 1024);
                        } else {
                            text = String.format("%1.0f%%, %d kB",
                                    track.getProgress() * 1.0,
                                    track.getTotalBytes() / 1024);
                        }
                        break;
                    case FINISHED:
                        downloadRate = track.getDownloadRate();
                        if (downloadRate != null) {
                            text = String.format("%d kB, %1.0f kB/s",
                                    track.getDownloadedBytes() / 1024,
                                    downloadRate / 1024);
                        } else {
                            text = String.format("%d kB",
                                    track.getDownloadedBytes() / 1024);
                        }
                        break;
                    case CANCELLED:
                        text = "cancelled";
                        break;
                    case ERROR:
                        text = "Error";
                        value = 100;
                        break;
                }
            }
        }

        b.setValue((Integer) value);
        b.setString(text);
        b.setFont(table.getFont());
        
        if (isSelected) {
            b.setForeground(new Color(255, 255, 255));
            b.setBackground(table.getSelectionBackground());
        } else {
            b.setForeground(new Color(243, 156, 18));
            if (row % 2 == 0) {
                b.setBackground(new Color(242, 242, 242));
            } else {
                b.setBackground(new Color(230, 230, 230));
            }
        }

        return panel;
    }

}