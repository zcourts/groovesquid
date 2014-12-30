package com.groovesquid.gui;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

@SuppressWarnings({"rawtypes", "serial"})
public class ComboBoxLocaleRenderer extends DefaultListCellRenderer {

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        Locale locale = (Locale) value;
        setText(StringUtils.capitalize(locale.getDisplayName(locale)));

        setHorizontalAlignment(SwingConstants.LEFT);
        setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        return label;
    }

}