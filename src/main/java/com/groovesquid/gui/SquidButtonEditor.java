package com.groovesquid.gui;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class SquidButtonEditor extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener {

    private JTable table;
    private Action action;
    private JButton renderButton;
    private JButton editButton;
    private Font fontAwesome;
    public String buttonText;
    public static String DOWNLOAD_ICON = "\uf019";
    public static String PLAY_ICON = "\uf04b";

    public SquidButtonEditor(JTable table, Action action, int column, String buttonText) {
        this(table, action, buttonText);

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(column).setCellRenderer(this);
        columnModel.getColumn(column).setCellEditor(this);
    }

    public SquidButtonEditor(JTable table, Action action, String buttonText) {
        this.table = table;
        this.action = action;
        this.buttonText = buttonText;

        try {
            InputStream is = getClass().getResourceAsStream("/gui/fonts/fontawesome.ttf");
            fontAwesome = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (Exception ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

        editButton = buildButton();
        editButton.addActionListener(this);
        renderButton = buildButton();
    }

    private JButton buildButton() {
        final JButton button = new JButton();
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setBorder(null);
        button.setFocusable(false);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);
        button.setSize(button.getPreferredSize());
        button.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        if (fontAwesome != null) {
            if (buttonText.equals(DOWNLOAD_ICON)) {
                button.setFont(fontAwesome.deriveFont(Font.PLAIN, 13f));
            } else if (buttonText.equals(PLAY_ICON)) {
                button.setFont(fontAwesome.deriveFont(Font.PLAIN, 10f));
            }
        }
        return button;
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (value instanceof Boolean && !((Boolean) value)) {
            editButton.setText(buttonText);
            editButton.setEnabled(true);
        } else {
            editButton.setText(null);
            editButton.setEnabled(false);
        }

        if (isSelected) {
            editButton.setBackground(table.getSelectionBackground());
            editButton.setForeground(Color.LIGHT_GRAY);
        } else {
            editButton.setForeground(Color.GRAY);
            if (row % 2 == 0) {
                editButton.setBackground(new Color(255, 255, 255));
            } else {
                editButton.setBackground(new Color(245, 245, 245));
            }
        }

        return editButton;
    }

    public Object getCellEditorValue() {
        return editButton.getText();
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof Boolean && !((Boolean) value)) {
            renderButton.setText(buttonText);
            renderButton.setEnabled(true);
        } else {
            renderButton.setText(null);
            renderButton.setEnabled(false);
        }

        if (isSelected) {
            renderButton.setBackground(table.getSelectionBackground());
            renderButton.setForeground(Color.WHITE);
        } else {
            renderButton.setForeground(Color.BLACK);
            if (row % 2 == 0) {
                renderButton.setBackground(new Color(255, 255, 255));
            } else {
                renderButton.setBackground(new Color(245, 245, 245));
            }
        }

        return renderButton;
    }

    public void actionPerformed(ActionEvent e) {
        int row = table.convertRowIndexToModel(table.getEditingRow());
        fireEditingStopped();

        //  Invoke the Action
        ActionEvent event = new ActionEvent(table, ActionEvent.ACTION_PERFORMED, "" + row);
        action.actionPerformed(event);
    }
    
}