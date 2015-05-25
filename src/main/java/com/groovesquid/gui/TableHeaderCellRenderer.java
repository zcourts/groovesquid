package com.groovesquid.gui;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

@SuppressWarnings("serial")
public class TableHeaderCellRenderer implements TableCellRenderer {

    private final TableCellRenderer wrappedRenderer;
    private final JLabel label;
    private Color color;
    
    public TableHeaderCellRenderer(TableCellRenderer wrappedRenderer, Color color) {
        if (!(wrappedRenderer instanceof JLabel)) {
            throw new IllegalArgumentException("The supplied renderer must inherit from JLabel");
        }
        this.wrappedRenderer = wrappedRenderer;
        this.label = (JLabel) wrappedRenderer;
        this.color = color;
    }  

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (table.getModel() instanceof TopSongTableModel && column == 0) {
            label.setHorizontalAlignment(SwingConstants.RIGHT);
        }

        wrappedRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        label.setBackground(Color.WHITE);
        label.setPreferredSize(new Dimension(label.getPreferredSize().width, 30));
        if(column > 0) {
            label.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 10, 0, new Color(204, 204, 204)), BorderFactory.createMatteBorder(0, 1, 0, 0, color)), BorderFactory.createEmptyBorder(0, 5, 0, 0)));
        } else {
            label.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 10, 0, new Color(204, 204, 204)), BorderFactory.createEmptyBorder(0, 5, 0, 0)));
        }
        label.setHorizontalTextPosition(10);

        return label;
    }
}
