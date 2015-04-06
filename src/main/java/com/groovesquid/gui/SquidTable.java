package com.groovesquid.gui;

import com.groovesquid.model.Album;
import com.groovesquid.model.Artist;
import com.groovesquid.model.Playlist;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.renderer.AbstractRenderer;
import org.jdesktop.swingx.renderer.ComponentProvider;
import org.jdesktop.swingx.rollover.RolloverProducer;
import org.jdesktop.swingx.rollover.RolloverRenderer;
import org.jdesktop.swingx.rollover.TableRolloverController;
import org.jdesktop.swingx.rollover.TableRolloverProducer;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@SuppressWarnings("serial")
public class SquidTable extends JXTable {
    
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
                c.setBackground(new Color(255, 255, 255));
            } else {
                c.setBackground(new Color(245, 245, 245));
            }
        }
        if(getModel() instanceof SongSearchTableModel && vColIndex == 0) {
            ((JComponent) c).setBorder(null);
        } else {
            ((JComponent) c).setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        }
        
        return c;
    }
    
    @Override
    public void setModel(TableModel model) {
        setColumnModel(new SquidTableColumnModel(this));
        super.setModel(model);

        // scroll to top
        scrollRectToVisible(getCellRect(0, 0, true));
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

    static public boolean isRolloverText(JXTable tbl, int row, int col) {
        int mCol = tbl.convertColumnIndexToModel(col);
        int mRow = tbl.convertRowIndexToModel(row);
        if (tbl.getColumnClass(col) != Album.class && tbl.getColumnClass(col) != Artist.class && tbl.getColumnClass(col) != Playlist.class) {
            return false;
        }
        Point pos = tbl.getMousePosition();
        if (pos == null) {
            return false;
        }
        AbstractRenderer ren = (AbstractRenderer) tbl.getCellRenderer(row, col);
        Object value = tbl.getModel().getValueAt(mRow, mCol);
        ComponentProvider<? extends JComponent> prov = ren.getComponentProvider();
        String text = prov.getString(value);
        if (text.length() == 0) {
            return false;
        }
        JComponent com = prov.getRendererComponent(null);
        int textWidth = com.getFontMetrics(com.getFont()).stringWidth(text);
        Rectangle cellBounds = tbl.getCellRect(row, col, true);
        Rectangle textBounds = new Rectangle(cellBounds.x, cellBounds.y, textWidth, cellBounds.height);
        return textBounds.contains(pos);
    }

    @Override
    protected RolloverProducer createRolloverProducer() {
        return new TableRolloverProducer() {
            @Override
            public void mouseMoved(MouseEvent ev) {
                updateRollover(ev, ROLLOVER_KEY, true);
            }
        };
    }

    @Override
    protected TableRolloverController<JXTable>
    createLinkController() {
        return new TableRolloverController<JXTable>() {
            @Override
            protected RolloverRenderer getRolloverRenderer(
                    Point loc, boolean prep) {
                if ((getColumnClass(loc.x) == Album.class || getColumnClass(loc.x) == Artist.class || getColumnClass(loc.x) == Playlist.class) && !isRolloverText(component, loc.y, loc.x)) {
                    return null;
                }
                return super.getRolloverRenderer(loc, prep);
            }
        };
    }
}
