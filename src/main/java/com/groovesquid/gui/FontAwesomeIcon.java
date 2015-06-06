package com.groovesquid.gui;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FontAwesomeIcon implements Icon, PropertyChangeListener {
    private JComponent component;
    private String text;
    private Font font;
    private Color foreground;
    private int padding;

    //  Used for the implementation of Icon interface
    private int iconWidth;
    private int iconHeight;

    public static String DOWNLOAD_ICON = "\uf019";
    public static String PLAY_ICON = "\uf04b";
    public static String PAUSE_ICON = "\uf04c";
    public static String VOLUME_OFF_ICON = "\uf026";
    public static String VOLUME_DOWN_ICON = "\uf027";
    public static String VOLUME_UP_ICON = "\uf028";
    public static String BACKWARD_ICON = "\uf04a";
    public static String FORWARD_ICON = "\uf04e";

    public FontAwesomeIcon(JComponent component, String text) {
        this(component, text, text.equals(DOWNLOAD_ICON) ? 13f : (text.equals(PLAY_ICON) ? 11f : 12f), null);
    }

    public FontAwesomeIcon(JComponent component, String text, Color foreground) {
        this(component, text, text.equals(DOWNLOAD_ICON) ? 13f : (text.equals(PLAY_ICON) ? 11f : 12f), foreground);
    }

    public FontAwesomeIcon(String text, float fontSize) {
        this(new JLabel(), text, fontSize, null);
    }

    public FontAwesomeIcon(String text, float fontSize, Color foreground) {
        this(new JLabel(), text, fontSize, foreground);
    }

    public FontAwesomeIcon(JComponent component, String text, float fontSize, Color foreground) {
        this.component = component;
        this.foreground = foreground;

        try {
            InputStream is = getClass().getResourceAsStream("/gui/fonts/fontawesome.ttf");
            font = Font.createFont(Font.TRUETYPE_FONT, is);
            font = font.deriveFont(Font.PLAIN, fontSize);
        } catch (Exception ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

        setText(text);

        component.addPropertyChangeListener("font", this);
    }

    /**
     * Get the text String that will be rendered on the Icon
     *
     * @return the text of the Icon
     */
    public String getText() {
        return text;
    }

    /**
     * Set the text to be rendered on the Icon
     *
     * @param text the text to be rendered on the Icon
     */
    public void setText(String text) {
        this.text = text;

        calculateIconDimensions();
    }

    /**
     * Get the foreground Color used to render the text. This will default to
     * the foreground Color of the component unless the foreground Color has
     * been overridden by using the setForeground() method.
     *
     * @return the Color used to render the text
     */
    public Color getForeground() {
        if (foreground == null)
            return component.getForeground();
        else
            return foreground;
    }

    /**
     * Set the foreground Color to be used for rendering the text
     *
     * @param foreground the foreground Color to be used for rendering the text
     */
    public void setForeground(Color foreground) {
        this.foreground = foreground;
        component.repaint();
    }

    /**
     * Get the padding used when rendering the text
     *
     * @return the padding specified in pixels
     */
    public int getPadding() {
        return padding;
    }

    /**
     * By default the size of the Icon is based on the size of the rendered
     * text. You can specify some padding to be added to the start and end
     * of the text when it is rendered.
     *
     * @param padding the padding amount in pixels
     */
    public void setPadding(int padding) {
        this.padding = padding;

        calculateIconDimensions();
    }

    /**
     * Calculate the size of the Icon using the FontMetrics of the Font.
     */
    private void calculateIconDimensions() {
        FontMetrics fm = component.getFontMetrics(font);

        iconWidth = fm.stringWidth(text) + (padding * 2);
        iconHeight = fm.getHeight();

        component.revalidate();
    }
    //
    //  Implement the Icon Interface
    //

    /**
     * Gets the width of this icon.
     *
     * @return the width of the icon in pixels.
     */
    public int getIconWidth() {
        return iconWidth;
    }

    /**
     * Gets the height of this icon.
     *
     * @return the height of the icon in pixels.
     */
    public int getIconHeight() {
        return iconHeight;
    }

    /**
     * Paint the icons of this compound icon at the specified location
     *
     * @param c The component to which the icon is added
     * @param g the graphics context
     * @param x the X coordinate of the icon's top-left corner
     * @param y the Y coordinate of the icon's top-left corner
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();

        //  The "desktophints" is supported in JDK6

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Map map = (Map) (toolkit.getDesktopProperty("awt.font.desktophints"));

        if (map != null) {
            g2.addRenderingHints(map);
        } else
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setFont(font);
        g2.setColor(getForeground());
        FontMetrics fm = g2.getFontMetrics();

        g2.translate(x, y + fm.getAscent());
        g2.drawString(text, padding, 0);

        g2.dispose();
    }

    //
    //  Implement the PropertyChangeListener interface
    //
    public void propertyChange(PropertyChangeEvent e) {
        //  Handle font change when using the default font

        if (font == null)
            calculateIconDimensions();
    }
}
