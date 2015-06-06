package com.groovesquid.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class GuiUtils {

    public enum OperatingSystem {
        WINDOWS, MAC, LINUX
    }

    public static ImageIcon stretchImage(ImageIcon image, int width, int height, ImageObserver imageObserver) {
        return stretchImage(image.getImage(), width, height, imageObserver);
    }

    public static ImageIcon stretchImage(Image image, int width, int height, ImageObserver imageObserver) {
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) newImage.getGraphics();
        g.drawImage(image, 0, 0, width, height, imageObserver);
        g.dispose();
        return new ImageIcon(newImage);
    }

    public static OperatingSystem getSystem() {
        String OS = java.lang.System.getProperty("os.name").toLowerCase();
        if (OS.indexOf("mac") >= 0) {
            return OperatingSystem.MAC;
        } else if (OS.indexOf("win") >= 0) {
            return OperatingSystem.WINDOWS;
        } else if (OS.indexOf("nux") >= 0) {
            return OperatingSystem.LINUX;
        }
        return OperatingSystem.LINUX;
    }
}
