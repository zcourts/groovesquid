package com.groovesquid.gui;

import com.groovesquid.Groovesquid;
import com.groovesquid.util.I18n;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

@SuppressWarnings({"serial", "rawtypes"})
public class AboutFrame extends JFrame {

    private JLabel websiteTextLabel;
    private LinkLabel websiteLabel;
    private JLabel facebookTextLabel;
    private LinkLabel facebookLabel;
    private JLabel twitterTextLabel;
    private LinkLabel twitterLabel;
    private JLabel copyrightLabel;
    private JLabel iconLabel;
    private JLabel versionLabel;
    private JButton closeButton;

    public AboutFrame() {
        initComponents();
        
        // center screen
        setLocationRelativeTo(null);
        
        // icon
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/gui/icon.png")));

        versionLabel.setText("Version " + Groovesquid.getVersion());
    }

    private void initComponents() {
        websiteTextLabel = new JLabel();
        websiteLabel = new LinkLabel();
        facebookTextLabel = new JLabel();
        facebookLabel = new LinkLabel();
        twitterTextLabel = new JLabel();
        twitterLabel = new LinkLabel();
        copyrightLabel = new JLabel();
        closeButton = new JButton();
        iconLabel = new JLabel();
        versionLabel = new JLabel();

        setTitle(I18n.getLocaleString("ABOUT"));
        setResizable(false);

        websiteTextLabel.setFont(new Font(websiteTextLabel.getFont().getName(), Font.PLAIN, 11));
        websiteTextLabel.setText("Website:");

        websiteLabel.setFont(new Font(websiteLabel.getFont().getName(), Font.PLAIN, 11));
        websiteLabel.setText("http://groovesquid.com");

        facebookTextLabel.setFont(new Font(facebookTextLabel.getFont().getName(), Font.PLAIN, 11));
        facebookTextLabel.setText("Facebook:");

        facebookLabel.setFont(new Font(facebookLabel.getFont().getName(), Font.PLAIN, 11));
        facebookLabel.setText("http://facebook.com/groovesquid");

        twitterTextLabel.setFont(new Font(twitterTextLabel.getFont().getName(), Font.PLAIN, 11));
        twitterTextLabel.setText("Twitter:");

        twitterLabel.setFont(new Font(twitterLabel.getFont().getName(), Font.PLAIN, 11));
        twitterLabel.setText("http://twitter.com/groovesquid");

        copyrightLabel.setFont(new Font(copyrightLabel.getFont().getName(), Font.PLAIN, 10));
        copyrightLabel.setForeground(new Color(102, 102, 102));
        copyrightLabel.setText("Copyright (c) 2015 by GBHRDT Development. All rights reserved.");

        closeButton.setText(I18n.getLocaleString("CLOSE"));
        closeButton.setFocusable(false);
        closeButton.setRequestFocusEnabled(false);
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        iconLabel.setIcon(new ImageIcon(getClass().getResource("/gui/logo.png")));

        versionLabel.setFont(new Font(versionLabel.getFont().getName(), Font.PLAIN, 11));
        versionLabel.setText("Version");

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(iconLabel)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(websiteTextLabel)
                                                    .addComponent(facebookTextLabel)
                                                    .addComponent(twitterTextLabel))
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(websiteLabel)
                                                    .addComponent(facebookLabel)
                                                    .addComponent(twitterLabel))))
                                .addGap(74, 74, 74))
                                .addComponent(copyrightLabel, GroupLayout.Alignment.LEADING))
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
                                .addComponent(websiteTextLabel)
                                .addComponent(websiteLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(facebookTextLabel)
                                .addComponent(facebookLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(twitterTextLabel)
                                .addComponent(twitterLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(versionLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(copyrightLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(closeButton))
                    .addComponent(iconLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }

    private void closeButtonActionPerformed(ActionEvent evt) {
        dispose();
    }

}
