package groovesquid.gui;

import groovesquid.Main;
import groovesquid.model.Song;
import groovesquid.service.Services;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author Marius Gebhardt
 */
public class SquidTableColumnModel extends DefaultTableColumnModel {

    private JTable table;
    
    public SquidTableColumnModel(JTable table) {
        this.table = table;
        this.columnMargin = 0;
    }

    @Override
    public void addColumn(TableColumn tc) {
        if(table.getModel() instanceof SongSearchTableModel) {
            if(tc.getModelIndex() == 0) {
                tc.setMaxWidth(22);
                
                Action download = new AbstractAction() {
                    public void actionPerformed(ActionEvent e) { 
                        int modelRow = Integer.valueOf( e.getActionCommand() );
                        DownloadTableModel downloadTableModel = (DownloadTableModel) Main.getGui().getDownloadTable().getModel();
                        SongSearchTableModel songSearchTableModel = (SongSearchTableModel) Main.getGui().getSearchTable().getModel();
                        Song song = songSearchTableModel.getSongs().get(modelRow);
                        downloadTableModel.addRow(0, Services.getDownloadService().download(song, Main.getGui().getDownloadListener(downloadTableModel)));
                    } 
                };
                SquidButtonEditor buttonColumn = new SquidButtonEditor(table, download);
                
                tc.setCellRenderer(buttonColumn);
                tc.setCellEditor(buttonColumn);
            }
        } else if(table.getModel() instanceof DownloadTableModel) {
            if(tc.getModelIndex() == 5) {
                tc.setCellRenderer(new ProgressCellRenderer());
                tc.setMinWidth(200);
            }
        }
        super.addColumn(tc);
    }

}
