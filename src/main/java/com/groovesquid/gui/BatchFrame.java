/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.groovesquid.gui;

import com.groovesquid.Main;
import com.groovesquid.model.Song;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Marius
 */

@SuppressWarnings("serial")
public class BatchFrame extends JFrame {

    /**
     * Creates new form About
     */
    public BatchFrame() {
        initComponents();
        
        // center screen
        setLocationRelativeTo(null);
        
        // icon
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/gui/icon.png")));
    }

    private void initComponents() {

        downloadButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        searchTextArea = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();

        setTitle("Batch");
        setResizable(false);

        downloadButton.setText(Main.getLocaleString("DOWNLOAD"));
        downloadButton.setFocusable(false);
        downloadButton.setRequestFocusEnabled(false);
        downloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadButtonActionPerformed(evt);
            }
        });

        searchTextArea.setColumns(20);
        searchTextArea.setRows(5);
        jScrollPane1.setViewportView(searchTextArea);

        jLabel1.setText(Main.getLocaleString("ONE_SEARCH_TERM_PER_LINE"));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 388, Short.MAX_VALUE)
                        .addComponent(downloadButton, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(downloadButton)
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void downloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadButtonActionPerformed
        String terms[] = searchTextArea.getText().split("\\r?\\n");
        final DownloadTableModel downloadTableModel = (DownloadTableModel) Main.getMainFrame().getDownloadTable().getModel();
        List<Song> songs = new ArrayList<Song>();
        
        for (String term : terms) {
            List<Song> results = Main.getSearchService().searchSongsByQuery(term);
            if(results.size() > 0) {
                songs.add(results.get(0));
            }
        }
        for (Song song : songs) {
            downloadTableModel.addRow(0, Main.getDownloadService().download(song, Main.getMainFrame().getDownloadListener(downloadTableModel)));
        }
    }//GEN-LAST:event_downloadButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton downloadButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea searchTextArea;
    // End of variables declaration//GEN-END:variables
}
