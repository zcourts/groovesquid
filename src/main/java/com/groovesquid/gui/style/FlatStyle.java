package com.groovesquid.gui.style;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings({"rawtypes", "serial", "unchecked"})
public class FlatStyle extends Style {

    private Image blueButton, blueButtonHover, blueButtonPressed, orangeButton, orangeButtonHover, orangeButtonPressed, dividerImage;
    private ImageIcon blueArrowSouth, blueArrowNorth, orangeArrowSouth, orangeArrowNorth;
    private Color pink, blue, orange, gray;

    public FlatStyle() {
        super();
        loadResources();
        initVariables();
    }

    public void initVariables() {
        undecorated = true;
        buttonBackgrounds = true;

        // colors
        pink = new Color(145, 2, 146);
        blue = new Color(52, 152, 219);
        orange = new Color(243, 156, 18);
        gray = new Color(204, 204, 204);

        mainFrameBackground = gray;
        playerPanelForeground = Color.WHITE;
        playerPanelBackground = pink;
        searchButtonsForeground = Color.WHITE;
        downloadButtonsForeground = Color.WHITE;
        searchTableSelectionBackground = blue;
        searchTableSelectionForeground = Color.WHITE;
        downloadTableSelectionBackground = orange;
        downloadTableSelectionForeground = Color.WHITE;

        // images
        searchButtonsBackground = blueButton;
        searchButtonsHoverBackground = blueButtonHover;
        searchButtonsPressedBackground = blueButtonPressed;
        downloadButtonsBackground = orangeButton;
        downloadButtonsHoverBackground = orangeButtonHover;
        downloadButtonsPressedBackground = orangeButtonPressed;

        // borders
        searchTypeComboBoxBorder = BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, blue), BorderFactory.createMatteBorder(0, 5, 0, 0, Color.WHITE));
        selectComboBoxBorder = BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, orange), BorderFactory.createMatteBorder(0, 5, 0, 0, Color.WHITE));
        searchTextFieldBorder = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(52, 152, 219)), BorderFactory.createEmptyBorder(0, 4, 0, 4));

        searchTableHeaderCellRendererColor = blue;
        downloadTableHeaderCellRendererColor = orange;
    }

    public SliderUI getSliderUI(JSlider slider, int thumbHeight) {
        return new FlatSliderUI(slider, thumbHeight);
    }

    public ScrollBarUI getSearchScrollBarUI(JScrollBar scrollBar) {
        return new FlatScrollBarUI(scrollBar, blueArrowSouth, blueArrowNorth, blue);
    }

    public ScrollBarUI getDownloadScrollBarUI(JScrollBar scrollBar) {
        return new FlatScrollBarUI(scrollBar, orangeArrowSouth, orangeArrowNorth, orange);
    }

    public ComboBoxUI getSearchTypeComboBoxUI(JComboBox comboBox) {
        return new FlatComboBoxUI(comboBox, blueArrowSouth);
    }

    public ComboBoxUI getSelectComboBoxUI(JComboBox comboBox) {
        return new FlatComboBoxUI(comboBox, orangeArrowSouth);
    }

    public SplitPaneUI getSplitPaneUI(JSplitPane splitPane) {
        return new FlatSplitPaneUI();
    }

    public ProgressBarUI getProgressBarUI(JProgressBar progressBar) {
        return new BasicProgressBarUI();
    }

    private void loadResources() {
        try {
            blueButton = ImageIO.read(getClass().getResource("/gui/style/flat/blueButton.png"));
            blueButtonHover = ImageIO.read(getClass().getResource("/gui/style/flat/blueButtonHover.png"));
            blueButtonPressed = ImageIO.read(getClass().getResource("/gui/style/flat/blueButtonPressed.png"));

            orangeButton = ImageIO.read(getClass().getResource("/gui/style/flat/orangeButton.png"));
            orangeButtonHover = ImageIO.read(getClass().getResource("/gui/style/flat/orangeButtonHover.png"));
            orangeButtonPressed = ImageIO.read(getClass().getResource("/gui/style/flat/orangeButtonPressed.png"));

            minimizeButtonIcon = new ImageIcon(getClass().getResource("/gui/style/flat/minimizeButton.png"));
            minimizeButtonHoverIcon = new ImageIcon(getClass().getResource("/gui/style/flat/minimizeButtonHover.png"));

            maximizeButtonIcon = new ImageIcon(getClass().getResource("/gui/style/flat/maximizeButton.png"));
            maximizeButtonHoverIcon = new ImageIcon(getClass().getResource("/gui/style/flat/maximizeButtonHover.png"));

            closeButtonIcon = new ImageIcon(getClass().getResource("/gui/style/flat/closeButton.png"));
            closeButtonHoverIcon = new ImageIcon(getClass().getResource("/gui/style/flat/closeButtonHover.png"));

            playIcon = new ImageIcon(getClass().getResource("/gui/style/flat/play.png"));
            playIconActive = new ImageIcon(getClass().getResource("/gui/style/flat/playH.png"));

            pauseIcon = new ImageIcon(getClass().getResource("/gui/style/flat/pause.png"));
            pauseIconActive = new ImageIcon(getClass().getResource("/gui/style/flat/pauseH.png"));

            nextIcon = new ImageIcon(getClass().getResource("/gui/style/flat/next.png"));
            nextIconActive = new ImageIcon(getClass().getResource("/gui/style/flat/nextH.png"));

            previousIcon = new ImageIcon(getClass().getResource("/gui/style/flat/previous.png"));
            previousIconActive = new ImageIcon(getClass().getResource("/gui/style/flat/previousH.png"));

            blueArrowSouth = new ImageIcon(getClass().getResource("/gui/style/flat/blueArrowSouth.png"));
            blueArrowNorth = new ImageIcon(getClass().getResource("/gui/style/flat/blueArrowNorth.png"));
            orangeArrowSouth = new ImageIcon(getClass().getResource("/gui/style/flat/orangeArrowSouth.png"));
            orangeArrowNorth = new ImageIcon(getClass().getResource("/gui/style/flat/orangeArrowNorth.png"));

            dividerImage = ImageIO.read(getClass().getResource("/gui/style/flat/divider.png"));

            volumeOnIcon = new ImageIcon(getClass().getResource("/gui/style/flat/volumeOn.png"));
            volumeOffIcon = new ImageIcon(getClass().getResource("/gui/style/flat/volumeOff.png"));

            titleBarIcon = new ImageIcon(getClass().getResource("/gui/titlebar.png"));

        } catch (IOException ex) {
            Logger.getLogger(FlatStyle.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    class FlatComboBoxUI extends BasicComboBoxUI {

        private ImageIcon arrowSouth;

        public FlatComboBoxUI(JComponent c, ImageIcon arrowSouth) {
            this.arrowSouth = arrowSouth;
            c.setBackground(Color.WHITE);
        }

        @Override
        protected JButton createArrowButton() {
            JButton button = new JButton();
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setIcon(arrowSouth);
            button.setOpaque(true);
            button.setBackground(Color.WHITE);
            return button;
        }

    }

    class FlatScrollBarUI extends BasicScrollBarUI {

        private ImageIcon arrowSouth, arrowNorth;
        private Color thumbClr;
        private int scrollBarWdth;

        public FlatScrollBarUI(JComponent c, ImageIcon arrowSouth, ImageIcon arrowNorth, Color thumbClr) {
            this.arrowSouth = arrowSouth;
            this.arrowNorth = arrowNorth;
            this.thumbClr = thumbClr;
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            JButton button = new JButton();
            button.setBorderPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
            button.setContentAreaFilled(false);
            button.setIcon(arrowSouth);
            button.setBackground(Color.WHITE);
            button.setOpaque(true);
            button.setFocusPainted(false);
            return button;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            JButton button = new JButton();
            button.setBorderPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
            button.setContentAreaFilled(false);
            button.setIcon(arrowNorth);
            button.setBackground(Color.WHITE);
            button.setOpaque(true);
            button.setFocusPainted(false);
            return button;
        }

        @Override
        protected void configureScrollBarColors() {
            trackColor = new ColorUIResource(Color.WHITE);
            trackHighlightColor = null;
            thumbColor = thumbClr;
            thumbHighlightColor = null;
            thumbDarkShadowColor = null;
            thumbLightShadowColor = null;
            scrollbar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0), BorderFactory.createEmptyBorder(0, 5, 0, 0)));
            scrollbar.setOpaque(false);
            scrollBarWdth = 30;
        }

        @Override
        public Dimension getPreferredSize(JComponent c) {
            return (scrollbar.getOrientation() == JScrollBar.VERTICAL) ? new Dimension(scrollBarWdth, 48) : new Dimension(48, scrollBarWdth);
        }

        @Override
        protected Rectangle getThumbBounds() {
            return thumbRect;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }

            int w = 11;
            int h = thumbBounds.height;

            g.translate(thumbBounds.x, thumbBounds.y);

            g.setColor(thumbColor);
            g.fillRect(8, 0, 9, h);

            g.translate(-thumbBounds.x, -thumbBounds.y);
        }

    }

    class FlatSliderUI extends BasicSliderUI {

        private transient boolean isDragging;
        private final int thumbHeight;

        public FlatSliderUI(JSlider slider, int thumbHeight) {
            super(slider);
            this.thumbHeight = thumbHeight;
        }

        @Override
        protected void calculateGeometry() {
            calculateFocusRect();
            calculateContentRect();
            calculateThumbSize();
            calculateTrackBuffer();
            calculateTrackRect();
            calculateTickRect();
            calculateLabelRect();
            calculateThumbLocation();

            scrollTimer = new Timer(0, scrollListener);
            scrollTimer.setInitialDelay(0);
        }

        @Override
        protected TrackListener createTrackListener(JSlider slider) {
            return new TrackListener();
        }

        @Override
        public void paintTrack(Graphics g) {
            Rectangle r = trackRect;
            r.height = thumbHeight + 6;
            g.setColor(Color.WHITE);
            g.fillRect(0, r.y, r.width + 6, r.height);
            g.setColor(new Color(145, 2, 146));
            g.fillRect(1, r.y + 1, r.width + 2, r.height - 2);
        }

        @Override
        public void paintThumb(Graphics g) {
            Rectangle r = thumbRect;
            if (r.x > 2) {
                g.setColor(Color.WHITE);
                g.fillRect(3, r.y + 3, r.x - r.width - 2, r.height);
            }
        }

        @Override
        protected Dimension getThumbSize() {
            Dimension size = new Dimension();
            size.width = 1;
            size.height = thumbHeight;
            return size;
        }

        @Override
        protected void scrollDueToClickInTrack(int direction) {
            int value = slider.getValue();
            if (slider.getOrientation() == JSlider.HORIZONTAL) {
                value = this.valueForXPosition(slider.getMousePosition().x);
            } else if (slider.getOrientation() == JSlider.VERTICAL) {
                value = this.valueForYPosition(slider.getMousePosition().y);
            }
            slider.setValue(value);
        }

        public class TrackListener extends BasicSliderUI.TrackListener {

            @Override
            public void mousePressed(MouseEvent e) {
                if (!slider.isEnabled()) {
                    return;
                }

                calculateGeometry();

                currentMouseX = e.getX();
                currentMouseY = e.getY();

                if (slider.isRequestFocusEnabled()) {
                    slider.requestFocus();
                }

                // Clicked in the Thumb area?
            /*if (thumbRect.contains(currentMouseX, currentMouseY)) {
                switch (slider.getOrientation()) {
                case JSlider.VERTICAL:
                    offset = currentMouseY - thumbRect.y;
                    break;
                case JSlider.HORIZONTAL:
                    offset = currentMouseX - thumbRect.x;
                    break;
                }
                isDragging = true;
                return;
            }*/
                isDragging = true;
                slider.setValueIsAdjusting(true);

                Dimension sbSize = slider.getSize();
                int direction = POSITIVE_SCROLL;

                switch (slider.getOrientation()) {
                    case JSlider.VERTICAL:
                        if (thumbRect.isEmpty()) {
                            int scrollbarCenter = sbSize.height / 2;
                            if (!drawInverted()) {
                                direction = (currentMouseY < scrollbarCenter) ?
                                        POSITIVE_SCROLL : NEGATIVE_SCROLL;
                            } else {
                                direction = (currentMouseY < scrollbarCenter) ?
                                        NEGATIVE_SCROLL : POSITIVE_SCROLL;
                            }
                        } else {
                            int thumbY = thumbRect.y;
                            if (!drawInverted()) {
                                direction = (currentMouseY < thumbY) ?
                                        POSITIVE_SCROLL : NEGATIVE_SCROLL;
                            } else {
                                direction = (currentMouseY < thumbY) ?
                                        NEGATIVE_SCROLL : POSITIVE_SCROLL;
                            }
                        }
                        break;
                    case JSlider.HORIZONTAL:
                        if (thumbRect.isEmpty()) {
                            int scrollbarCenter = sbSize.width / 2;
                            if (!drawInverted()) {
                                direction = (currentMouseX < scrollbarCenter) ?
                                        NEGATIVE_SCROLL : POSITIVE_SCROLL;
                            } else {
                                direction = (currentMouseX < scrollbarCenter) ?
                                        POSITIVE_SCROLL : NEGATIVE_SCROLL;
                            }
                        } else {
                            int thumbX = thumbRect.x;
                            if (!drawInverted()) {
                                direction = (currentMouseX < thumbX) ?
                                        NEGATIVE_SCROLL : POSITIVE_SCROLL;
                            } else {
                                direction = (currentMouseX < thumbX) ?
                                        POSITIVE_SCROLL : NEGATIVE_SCROLL;
                            }
                        }
                        break;
                }

                if (shouldScroll(direction)) {
                    scrollDueToClickInTrack(direction);
                }
                if (shouldScroll(direction)) {
                    scrollTimer.stop();
                    scrollListener.setDirection(direction);
                    scrollTimer.start();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int thumbMiddle = 0;

                if (!slider.isEnabled()) {
                    return;
                }

                currentMouseX = e.getX();
                currentMouseY = e.getY();

                if (!isDragging) {
                    return;
                }

                slider.setValueIsAdjusting(true);

                switch (slider.getOrientation()) {
                    case JSlider.VERTICAL:
                        int halfThumbHeight = thumbRect.height / 2;
                        int thumbTop = e.getY() - offset;
                        int trackTop = trackRect.y;
                        int trackBottom = trackRect.y + (trackRect.height - 1);
                        int vMax = yPositionForValue(slider.getMaximum() -
                                slider.getExtent());

                        if (drawInverted()) {
                            trackBottom = vMax;
                        } else {
                            trackTop = vMax;
                        }
                        thumbTop = Math.max(thumbTop, trackTop - halfThumbHeight);
                        thumbTop = Math.min(thumbTop, trackBottom - halfThumbHeight);

                        setThumbLocation(thumbRect.x, thumbTop);

                        thumbMiddle = thumbTop + halfThumbHeight;
                        slider.setValue(valueForYPosition(thumbMiddle));
                        break;
                    case JSlider.HORIZONTAL:
                        int halfThumbWidth = thumbRect.width / 2;
                        int thumbLeft = e.getX() - offset;
                        int trackLeft = trackRect.x;
                        int trackRight = trackRect.x + (trackRect.width - 1);
                        int hMax = xPositionForValue(slider.getMaximum() -
                                slider.getExtent());

                        if (drawInverted()) {
                            trackLeft = hMax;
                        } else {
                            trackRight = hMax;
                        }
                        thumbLeft = Math.max(thumbLeft, trackLeft - halfThumbWidth);
                        thumbLeft = Math.min(thumbLeft, trackRight - halfThumbWidth);

                        setThumbLocation(thumbLeft, thumbRect.y);

                        thumbMiddle = thumbLeft + halfThumbWidth;
                        slider.setValue(valueForXPosition(thumbMiddle));
                        break;
                    default:
                }
            }

        }

    }

    class FlatSplitPaneUI extends BasicSplitPaneUI {

        public FlatSplitPaneUI() {
            super();
        }

        public BasicSplitPaneDivider createDefaultDivider() {
            return new BasicSplitPaneDivider(this) {
                public void setBorder(Border b) {
                    //b = BorderFactory.createEmptyBorder(2, 0, 2, 0);
                }

                @Override
                public void paint(Graphics g) {
                    int iw = dividerImage.getWidth(this);
                    int ih = dividerImage.getHeight(this);
                    if (iw > 0 && ih > 0) {
                        for (int x = 0; x < getWidth(); x += iw) {
                            for (int y = 0; y < 1; y += ih) {
                                g.drawImage(dividerImage, x, y, iw, ih, this);
                            }
                        }
                    }
                    super.paint(g);
                }
            };
        }
    }

}
