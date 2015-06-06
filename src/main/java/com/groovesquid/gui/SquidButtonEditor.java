package com.groovesquid.gui;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class SquidButtonEditor extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener {

    private JTable table;
    private Action action;
    private JButton renderButton;
    private JButton editButton;
    private FontAwesomeIcon renderIcon;
    private FontAwesomeIcon editIcon;

    public SquidButtonEditor(JTable table, Action action, int column, String iconText) {
        this(table, action, iconText);

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(column).setCellRenderer(this);
        columnModel.getColumn(column).setCellEditor(this);
    }

    public SquidButtonEditor(JTable table, Action action, String iconText) {
        this.table = table;
        this.action = action;

        editButton = buildButton();
        editButton.addActionListener(this);
        renderButton = buildButton();

        renderIcon = new FontAwesomeIcon(renderButton, iconText);
        editIcon = new FontAwesomeIcon(renderButton, iconText);
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

        return button;
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (isSelected) {
            editButton.setBackground(table.getSelectionBackground());
            editIcon.setForeground(Color.LIGHT_GRAY);
        } else {
            editIcon.setForeground(Color.GRAY);
            if (row % 2 == 0) {
                editButton.setBackground(new Color(255, 255, 255));
            } else {
                editButton.setBackground(new Color(245, 245, 245));
            }
        }

        if (value instanceof Boolean && !((Boolean) value)) {
            editButton.setIcon(editIcon);
            editButton.setEnabled(true);
        } else {
            editButton.setIcon(null);
            editButton.setEnabled(false);
        }

        return editButton;
    }

    public Object getCellEditorValue() {
        return editButton.getText();
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            renderButton.setBackground(table.getSelectionBackground());
            renderIcon.setForeground(Color.WHITE);
        } else {
            renderIcon.setForeground(Color.DARK_GRAY);
            if (row % 2 == 0) {
                renderButton.setBackground(new Color(255, 255, 255));
            } else {
                renderButton.setBackground(new Color(245, 245, 245));
            }
        }

        if (value instanceof Boolean && !((Boolean) value)) {
            renderButton.setIcon(renderIcon);
            renderButton.setEnabled(true);
        } else {
            renderButton.setIcon(null);
            renderButton.setEnabled(false);
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