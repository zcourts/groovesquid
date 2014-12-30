package com.groovesquid.gui;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author Marius
 */

@SuppressWarnings("serial")
public class SquidTable extends JTable {
    
    public SquidTable() {
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        this.addMouseListener(editingListener);
        this.addMouseMotionListener(editingListener);   
    }
    
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int vColIndex) {
        Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
        
        if (isCellSelected(rowIndex, vColIndex)) {
            c.setBackground(getSelectionBackground());
        } else {
            if (rowIndex % 2 == 0) {
                c.setBackground(new Color(242,242,242));
            } else {
                c.setBackground(new Color(230,230,230));
            }
        }
        if(getModel() instanceof SongSearchTableModel && vColIndex == 0) {
            ((JComponent) c).setBorder(null);
        } else {
            ((JComponent) c).setBorder(BorderFactory.createEmptyBorder(0,5,0,0));
        }
        
        return c;
    }
    
    @Override
    public void setModel(TableModel model) {
        setColumnModel(new SquidTableColumnModel(this));
        super.setModel(model);
    }
    
    private final MouseAdapter editingListener = new MouseAdapter() {
        @Override
        public void mouseMoved(MouseEvent e) {
            possiblySwitchEditors(e);
        }
        @Override
        public void mouseEntered(MouseEvent e) {
            possiblySwitchEditors(e);
        }
        @Override
        public void mouseExited(MouseEvent e) {
            possiblySwitchEditors(e);
        }
        @Override
        public void mouseClicked(MouseEvent e) {
            possiblySwitchEditors(e);
        }
    };
    
    private void possiblySwitchEditors(MouseEvent e) {
        Point p = e.getPoint();
        if (p != null) {
            int row = rowAtPoint(p);
            int col = columnAtPoint(p);
            if (row != getEditingRow() || col != getEditingColumn()) {
                if (isEditing()) {
                    TableCellEditor editor = getCellEditor();
                    if (!editor.stopCellEditing()) {
                        editor.cancelCellEditing();
                    }
                }
                if (!isEditing()) {
                    if (row != -1 && isCellEditable(row, col)) {
                        editCellAt(row, col);
                    }
                }
            }
        }
    }
}
