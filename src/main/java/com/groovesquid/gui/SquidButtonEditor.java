package com.groovesquid.gui;

import com.groovesquid.Main;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *  The ButtonColumn class provides a renderer and an editor that looks like a
 *  JButton. The renderer and editor will then be used for a specified column
 *  in the table. The TableModel will contain the String to be displayed on
 *  the button.
 *
 *  The button can be invoked by a mouse click or by pressing the space bar
 *  when the cell has focus. Optionally a mnemonic can be set to invoke the
 *  button. When the button is invoked the provided Action is invoked. The
 *  source of the Action will be the table. The action command will contain
 *  the model row number of the button that was clicked.
 *
 */

@SuppressWarnings("serial")
public class SquidButtonEditor extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener {
    private JTable table;
    private Action action;

    private JButton renderButton;
    private JButton editButton;
    private Object editorValue;

    /**
     *  Create the ButtonColumn to be used as a renderer and editor. The
     *  renderer and editor will automatically be installed on the TableColumn
     *  of the specified column.
     *
     *  @param table the table containing the button renderer/editor
     *  @param action the Action to be invoked when the button is invoked
     *  @param column the column to which the button renderer/editor is added
     */
    public SquidButtonEditor(JTable table, Action action, int column) {
        this(table, action);

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(column).setCellRenderer(this);
        columnModel.getColumn(column).setCellEditor(this);
    }

    public SquidButtonEditor(JTable table, Action action) {
        this.table = table;
        this.action = action;

        renderButton = buildButton();
        editButton = buildButton();
        editButton.addActionListener(this);
        editButton.setBackground(Color.RED);

        //table.addMouseListener(this);
    }

    private JButton buildButton() {
        JButton button = new JButton();
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setBorder(null);
        button.setFocusable(false);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setHorizontalTextPosition(SwingConstants.LEFT);
        return button;
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if(!((Boolean) value)) {
            editButton.setIcon(Main.getMainFrame().plusIcon);
            editButton.setRolloverIcon(Main.getMainFrame().plusIconHover);
        } else {
            editButton.setIcon(null);
            editButton.setRolloverIcon(null);
        }

        if (isSelected) {
            editButton.setBackground(table.getSelectionBackground());
        } else {
            if (row % 2 == 0) {
                editButton.setBackground(new Color(242,242,242));
            } else {
                editButton.setBackground(new Color(230,230,230));
            }
        }

        this.editorValue = value;
        return editButton;
    }

    public Object getCellEditorValue() {
        return editorValue;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if(!((Boolean) value)) {
            renderButton.setIcon(Main.getMainFrame().plusIcon);
            renderButton.setRolloverIcon(Main.getMainFrame().plusIconHover);
        } else {
            renderButton.setIcon(null);
            renderButton.setRolloverIcon(null);
        }

        if (isSelected) {
            renderButton.setBackground(table.getSelectionBackground());
        } else {
            if (row % 2 == 0) {
                renderButton.setBackground(new Color(242,242,242));
            } else {
                renderButton.setBackground(new Color(230,230,230));
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