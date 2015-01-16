package com.groovesquid.gui.style;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.*;
import java.awt.*;

public class Style {

    protected boolean undecorated, buttonBackgrounds;
    protected Color mainFrameBackground, playerPanelForeground, playerPanelBackground, searchButtonsForeground, downloadButtonsForeground, downloadTableSelectionBackground, downloadTableSelectionForeground, searchTableSelectionBackground, searchTableSelectionForeground;
    protected ImageIcon playIcon, playIconActive, pauseIcon, pauseIconActive, nextIcon, nextIconActive, previousIcon, previousIconActive, minimizeButtonIcon, minimizeButtonHoverIcon, maximizeButtonIcon, maximizeButtonHoverIcon, closeButtonIcon, closeButtonHoverIcon, volumeOnIcon, volumeOffIcon, titleBarIcon;
    protected Image searchButtonsBackground, searchButtonsHoverBackground, searchButtonsPressedBackground, downloadButtonsBackground, downloadButtonsHoverBackground, downloadButtonsPressedBackground;
    protected Font font = new Font("Lucida Grande", 0, 11);
    protected Border searchTypeComboBoxBorder, selectComboBoxBorder;

    public Style() {


    }

    public boolean isUndecorated() {
        return undecorated;
    }

    public boolean usesButtonBackgrounds() {
        return buttonBackgrounds;
    }

    public ImageIcon getTitleBarIcon() {
        return titleBarIcon;
    }

    public Font getFont() {
        return font;
    }

    public Font getFont(int size) {
        return new Font(font.getName(), font.getStyle(), size);
    }

    public Color getPlayerPanelForeground() {
        return playerPanelForeground;
    }

    public Color getPlayerPanelBackground() {
        return playerPanelBackground;
    }

    public Color getSearchButtonsForeground() {
        return searchButtonsForeground;
    }

    public Color getDownloadButtonsForeground() {
        return downloadButtonsForeground;
    }

    public ImageIcon getPlayIcon() {
        return playIcon;
    }

    public ImageIcon getPlayIconActive() {
        return playIconActive;
    }

    public ImageIcon getPauseIcon() {
        return pauseIcon;
    }

    public ImageIcon getPauseIconActive() {
        return pauseIconActive;
    }

    public ImageIcon getNextIcon() {
        return nextIcon;
    }

    public ImageIcon getNextIconActive() {
        return nextIconActive;
    }

    public ImageIcon getPreviousIcon() {
        return previousIcon;
    }

    public ImageIcon getVolumeOffIcon() {
        return volumeOffIcon;
    }

    public ImageIcon getVolumeOnIcon() {
        return volumeOnIcon;
    }

    public ImageIcon getPreviousIconActive() {

        return previousIconActive;
    }

    public ImageIcon getMinimizeButtonIcon() {
        return minimizeButtonIcon;
    }

    public ImageIcon getMinimizeButtonHoverIcon() {
        return minimizeButtonHoverIcon;
    }

    public ImageIcon getMaximizeButtonIcon() {
        return maximizeButtonIcon;
    }

    public ImageIcon getMaximizeButtonHoverIcon() {
        return maximizeButtonHoverIcon;
    }

    public ImageIcon getCloseButtonIcon() {
        return closeButtonIcon;
    }

    public ImageIcon getCloseButtonHoverIcon() {
        return closeButtonHoverIcon;
    }

    public SliderUI getSliderUI(JSlider slider, int thumbHeight) {
        return slider.getUI();
    }

    public ScrollBarUI getSearchScrollBarUI(JScrollBar scrollBar) {
        return scrollBar.getUI();
    }

    public ScrollBarUI getDownloadScrollBarUI(JScrollBar scrollBar) {
        return scrollBar.getUI();
    }

    public ComboBoxUI getSearchTypeComboBoxUI(JComboBox comboBox) {
        return comboBox.getUI();
    }

    public ComboBoxUI getSelectComboBoxUI(JComboBox comboBox) {
        return comboBox.getUI();
    }

    public SplitPaneUI getSplitPaneUI(JSplitPane splitPane) {
        return splitPane.getUI();
    }

    public Color getMainFrameBackground() {
        return mainFrameBackground;
    }

    public Image getSearchButtonsBackground() {
        return searchButtonsBackground;
    }

    public Image getSearchButtonsHoverBackground() {
        return searchButtonsHoverBackground;
    }

    public Image getSearchButtonsPressedBackground() {
        return searchButtonsPressedBackground;
    }

    public Image getDownloadButtonsBackground() {
        return downloadButtonsBackground;
    }

    public Image getDownloadButtonsHoverBackground() {
        return downloadButtonsHoverBackground;
    }

    public Image getDownloadButtonsPressedBackground() {
        return downloadButtonsPressedBackground;
    }

    public Color getDownloadTableSelectionBackground() {
        return downloadTableSelectionBackground;
    }

    public Color getDownloadTableSelectionForeground() {
        return downloadTableSelectionForeground;
    }

    public Color getSearchTableSelectionBackground() {
        return searchTableSelectionBackground;
    }

    public Color getSearchTableSelectionForeground() {
        return searchTableSelectionForeground;
    }

    public Border getSearchTypeComboBoxBorder() {
        return searchTypeComboBoxBorder;
    }

    public Border getSelectComboBoxBorder() {
        return selectComboBoxBorder;
    }

    public ProgressBarUI getProgressBarUI(JProgressBar progressBar) {
        return progressBar.getUI();
    }
}
