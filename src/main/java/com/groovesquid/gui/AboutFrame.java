package com.groovesquid.gui;

import com.groovesquid.Main;
import com.groovesquid.util.I18n;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings({"serial", "rawtypes"})
public class AboutFrame extends JFrame {

    public AboutFrame() {
        initComponents();
        
        // center screen
        setLocationRelativeTo(null);
        
        // icon
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/gui/icon.png")));
        
        versionLabel.setText("Version " + Main.getVersion());
    }

    private void initComponents() {
        jLabel3 = new JLabel();
        websiteLabel = new JLabel();
        facebookLabel = new JLabel();
        jLabel4 = new JLabel();
        jLabel5 = new JLabel();
        twitterLabel = new JLabel();
        jLabel2 = new JLabel();
        closeButton = new JButton();
        jLabel1 = new JLabel();
        lekoLabel = new JLabel();
        jLabel6 = new JLabel();
        versionLabel = new JLabel();

        setTitle(I18n.getLocaleString("ABOUT"));
        setResizable(false);

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 0, 11));
        jLabel3.setText("Website:");

        websiteLabel.setFont(new java.awt.Font("Lucida Grande", 0, 11));
        websiteLabel.setForeground(java.awt.Color.blue);
        websiteLabel.setText("http://groovesquid.com");
        websiteLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        websiteLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                websiteLabelMousePressed(evt);
            }
        });

        facebookLabel.setFont(new java.awt.Font("Lucida Grande", 0, 11));
        facebookLabel.setForeground(java.awt.Color.blue);
        facebookLabel.setText("http://facebook.com/groovesquid");
        facebookLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        facebookLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                websiteLabelMousePressed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Lucida Grande", 0, 11));
        jLabel4.setText("Facebook:");

        jLabel5.setFont(new java.awt.Font("Lucida Grande", 0, 11));
        jLabel5.setText("Twitter:");

        twitterLabel.setFont(new java.awt.Font("Lucida Grande", 0, 11));
        twitterLabel.setForeground(java.awt.Color.blue);
        twitterLabel.setText("http://twitter.com/groovesquid");
        twitterLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        twitterLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                websiteLabelMousePressed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 0, 11));
        jLabel2.setForeground(new java.awt.Color(102, 102, 102));
        jLabel2.setText("Copyright (c) 2014 by Maino Development. All rights reserved.");

        closeButton.setText(I18n.getLocaleString("CLOSE"));
        closeButton.setFocusable(false);
        closeButton.setRequestFocusEnabled(false);
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 0, 11));
        jLabel1.setText("Logo & UI:");

        lekoLabel.setFont(new java.awt.Font("Lucida Grande", 0, 11));
        lekoLabel.setForeground(java.awt.Color.blue);
        lekoLabel.setText("http://facebook.com/LeKoArtsDE");
        lekoLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lekoLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                websiteLabelMousePressed(evt);
            }
        });

        jLabel6.setIcon(new ImageIcon(getClass().getResource("/gui/logo.png")));

        versionLabel.setFont(new java.awt.Font("Lucida Grande", 0, 11));
        versionLabel.setText("Version");

        javax.swing.GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(lekoLabel))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel5)
                                            .addComponent(jLabel3))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(websiteLabel)
                                            .addComponent(facebookLabel)
                                            .addComponent(twitterLabel))))
                                .addGap(74, 74, 74))
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(versionLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(websiteLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(facebookLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(twitterLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(lekoLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(versionLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(closeButton))
                    .addComponent(jLabel6))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }

    private void websiteLabelMousePressed(MouseEvent evt) {
        try {
            Desktop.getDesktop().browse(java.net.URI.create(((JLabel) evt.getSource()).getText()));
        } catch (IOException ex) {
            Logger.getLogger(AboutFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void closeButtonActionPerformed(ActionEvent evt) {
        dispose();
    }

    private JButton closeButton;
    private JLabel facebookLabel;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel lekoLabel;
    private JLabel twitterLabel;
    private JLabel versionLabel;
    private JLabel websiteLabel;
}
