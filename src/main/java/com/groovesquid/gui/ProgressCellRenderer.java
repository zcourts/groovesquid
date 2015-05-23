package com.groovesquid.gui;

import com.groovesquid.Groovesquid;
import com.groovesquid.model.Track;
import com.groovesquid.util.I18n;
import com.groovesquid.util.Utils;

import javax.swing.*;
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
        b.setOpaque(false);

        b.setUI(Groovesquid.getStyle().getProgressBarUI(b));

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
        row = table.convertRowIndexToModel(row);
        Track track = ((DownloadTableModel)table.getModel()).getSongDownloads().get(row);
        
        if(track != null) {
            Double downloadRate = track.getDownloadRate();
            if(track.getStatus() != null) {
                switch (track.getStatus()) {
                    case INITIALIZING:
                        text = I18n.getLocaleString("INITIALIZING") + "...";
                        break;
                    case QUEUED:
                        text = I18n.getLocaleString("WAITING") + "...";
                        break;
                    case DOWNLOADING:
                        if (downloadRate != null) {
                            text = String.format("%1.0f%%, %s " + I18n.getLocaleString("OF") + " %s, %s/s",
                                    track.getProgress() * 1.0,
                                    Utils.humanReadableByteCount(track.getDownloadedBytes(), true),
                                    Utils.humanReadableByteCount(track.getTotalBytes(), true),
                                    Utils.humanReadableByteCount(Math.round(downloadRate), true));
                        } else {
                            text = String.format("%1.0f%%, %s",
                                    track.getProgress() * 1.0,
                                    Utils.humanReadableByteCount(track.getTotalBytes(), true));
                        }
                        break;
                    case FINISHED:
                        downloadRate = track.getDownloadRate();
                        if (downloadRate != null) {
                            text = String.format("%s, %s/s",
                                    Utils.humanReadableByteCount(track.getDownloadedBytes(), true),
                                    Utils.humanReadableByteCount(Math.round(downloadRate), true));
                        } else {
                            text = String.format("%s",
                                    Utils.humanReadableByteCount(track.getDownloadedBytes(), true));
                        }
                        break;
                    case CANCELLED:
                        text = I18n.getLocaleString("CANCELLED");
                        break;
                    case ERROR:
                        text = I18n.getLocaleString("ERROR");
                        value = 100;
                        break;
                }
            }
        }

        b.setValue((Integer) value);
        b.setString(text);
        b.setFont(table.getFont().deriveFont(11f));
        
        if (isSelected) {
            //b.setForeground(table.getSelectionForeground());
            b.setBackground(table.getSelectionBackground());
        } else {
            //b.setForeground(table.getSelectionForeground());
            if (row % 2 == 0) {
                b.setBackground(new Color(242, 242, 242));
            } else {
                b.setBackground(new Color(230, 230, 230));
            }
        }

        return panel;
    }

}