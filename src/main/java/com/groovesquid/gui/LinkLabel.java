package com.groovesquid.gui;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LinkLabel extends JLabel {

    public LinkLabel() {
        setForeground(Color.blue);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                try {
                    Desktop.getDesktop().browse(java.net.URI.create(((JLabel) evt.getSource()).getText()));
                } catch (IOException ex) {
                    Logger.getLogger(AboutFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

}
