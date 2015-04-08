package com.groovesquid.gui;

import com.groovesquid.Config.DownloadComplete;
import com.groovesquid.Main;
import com.groovesquid.gui.style.Style;
import com.groovesquid.model.*;
import com.groovesquid.service.DownloadListener;
import com.groovesquid.service.PlayService;
import com.groovesquid.service.PlaybackListener;
import com.groovesquid.util.I18n;
import com.groovesquid.util.Utils;
import org.apache.commons.lang3.ArrayUtils;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.hyperlink.AbstractHyperlinkAction;
import org.jdesktop.swingx.renderer.CellContext;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.HyperlinkProvider;
import org.jdesktop.swingx.renderer.JXRendererHyperlink;
import org.jdesktop.swingx.rollover.RolloverProducer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings({"serial", "rawtypes", "unchecked"})
public class MainFrame extends JFrame {
    
    private ArrayList<String> autocompleteList = new ArrayList<String>();

    protected ImageIcon plusIcon, plusIconHover, facebookIcon, twitterIcon;
    private Style style;
    
    /**
     * Creates new form GUI
     */
    public MainFrame() {
        style = Main.getStyle();

        loadResources();
        initComponents();

        // title
        setTitle("Groovesquid");

        // center screen
        setLocationRelativeTo(null);

        setVisible(true);

        // background fix
        getContentPane().setBackground(getBackground());

        // icon
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/gui/icon.png")));

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        // tables
        ((DownloadTableModel)downloadTable.getModel()).setSongDownloads(Main.getConfig().getDownloads());


        downloadTable.getSelectionModel().addListSelectionListener(downloadListSelectionListener);
        downloadTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent evt) {
                downloadTableMouseReleased(evt);
            }
        });
        downloadTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt) {
                downloadTableKeyReleased(evt);
            }
        });

        Main.getPlayService().setListener(playbackListener);
    }

    protected void loadResources() {
        plusIcon = new ImageIcon(getClass().getResource("/gui/plus.png"));
        plusIconHover = new ImageIcon(getClass().getResource("/gui/plusHover.png"));
    }

    protected void initComponents() {
        setBackground(style.getMainFrameBackground());

        downloadMenuItem = new JMenuItem(I18n.getLocaleString("DOWNLOAD"));
        downloadMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                downloadMenuItemActionPerformed(evt);
            }
        });

        playMenuItem = new JMenuItem(I18n.getLocaleString("PLAY"));
        playMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                playMenuItemActionPerformed(evt);
            }
        });

        searchTablePopupMenu = new JPopupMenu();
        searchTablePopupMenu.add(downloadMenuItem);
        searchTablePopupMenu.add(playMenuItem);

        removeFromListMenuItem = new JMenuItem(I18n.getLocaleString("REMOVE_FROM_LIST"));
        removeFromListMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                removeFromListMenuItemActionPerformed(evt);
            }
        });

        removeFromDiskMenuItem = new JMenuItem(I18n.getLocaleString("REMOVE_FROM_DISK"));
        removeFromDiskMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                removeFromDiskMenuItemActionPerformed(evt);
            }
        });

        openFileMenuItem = new JMenuItem(I18n.getLocaleString("OPEN_FILE"));
        openFileMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                openFileMenuItemActionPerformed(evt);
            }
        });

        openDirectoryMenuItem = new JMenuItem(I18n.getLocaleString("OPEN_DIRECTORY"));
        openDirectoryMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                openDirectoryMenuItemActionPerformed(evt);
            }
        });

        downloadTablePopupMenu = new JPopupMenu();
        downloadTablePopupMenu.add(removeFromListMenuItem);
        downloadTablePopupMenu.add(removeFromDiskMenuItem);
        downloadTablePopupMenu.add(openFileMenuItem);
        downloadTablePopupMenu.add(openDirectoryMenuItem);

        retryFailedDownloadsButton = new JButton();
        adScrollPane = new JScrollPane();
        adPane = new JEditorPane();

        playerPanel = new JPanel();
        playerPanel.setBackground(style.getPlayerPanelBackground());
        playerPanel.setForeground(style.getPlayerPanelForeground());
        playerPanel.setPreferredSize(new Dimension(673, 60));

        playPauseButton = new JButton();
        playPauseButton.setIcon(style.getPlayIcon());
        playPauseButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        playPauseButton.setContentAreaFilled(false);
        playPauseButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        playPauseButton.setFocusable(false);
        playPauseButton.setRequestFocusEnabled(false);
        playPauseButton.setVerticalTextPosition(SwingConstants.TOP);
        playPauseButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                playPauseButtonMousePressed(evt);
            }
        });
        playPauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                playPauseButtonActionPerformed(evt);
            }
        });

        currentDurationLabel = new JLabel("00:00");
        currentDurationLabel.setFont(style.getFont(9));
        currentDurationLabel.setOpaque(false);
        currentDurationLabel.setForeground(style.getPlayerPanelForeground());
        currentDurationLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 3));

        currentlyPlayingLabel = new JLabel();
        currentlyPlayingLabel.setForeground(style.getPlayerPanelForeground());
        currentlyPlayingLabel.setBorder(BorderFactory.createEmptyBorder(-10, 0, 0, 0));
        currentlyPlayingLabel.setFont(style.getFont(12));

        trackSlider = new JSlider();
        trackSlider.setUI(style.getSliderUI(trackSlider, 11));
        trackSlider.setOpaque(false);
        trackSlider.setValue(0);
        trackSlider.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        trackSlider.setEnabled(false);
        trackSlider.setFocusable(false);
        trackSlider.setRequestFocusEnabled(false);
        trackSlider.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent evt) {
                trackSliderMouseDragged(evt);
            }
        });

        durationLabel = new JLabel("00:00");
        durationLabel.setFont(style.getFont(9));
        durationLabel.setForeground(style.getPlayerPanelForeground());
        durationLabel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 10));

        albumCoverLabel = new JLabel();
        albumCoverLabel.setOpaque(false);

        volumeSlider = new JSlider();
        volumeSlider.setUI(style.getSliderUI(volumeSlider, 7));
        volumeSlider.setOpaque(false);
        volumeSlider.setMaximum(6);
        volumeSlider.setMinimum(-80);
        volumeSlider.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        volumeSlider.setFocusable(false);
        volumeSlider.setRequestFocusEnabled(false);
        volumeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                volumeSliderStateChanged(evt);
            }
        });

        volumeOffLabel = new JLabel();
        volumeOffLabel.setIcon(style.getVolumeOffIcon());

        volumeOnLabel = new JLabel();
        volumeOnLabel.setIcon(style.getVolumeOnIcon());

        previousButton = new JButton();
        previousButton.setIcon(style.getPreviousIcon());
        previousButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        previousButton.setContentAreaFilled(false);
        previousButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        previousButton.setFocusable(false);
        previousButton.setRequestFocusEnabled(false);
        previousButton.setVerticalTextPosition(SwingConstants.TOP);
        previousButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                previousButtonMousePressed(evt);
            }
        });
        previousButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                previousButtonActionPerformed(evt);
            }
        });

        nextButton = new JButton();
        nextButton.setIcon(style.getNextIcon());
        nextButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        nextButton.setContentAreaFilled(false);
        nextButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        nextButton.setFocusable(false);
        nextButton.setRequestFocusEnabled(false);
        nextButton.setVerticalTextPosition(SwingConstants.TOP);
        nextButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                nextButtonMousePressed(evt);
            }
        });
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        GroupLayout playerPanelLayout = new GroupLayout(playerPanel);
        playerPanel.setLayout(playerPanelLayout);
        playerPanelLayout.setHorizontalGroup(
                playerPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(playerPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGap(18, 18, 18)
                                .addComponent(previousButton, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(playPauseButton, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nextButton, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                                .addGap(66, 66, 66)
                                .addComponent(currentlyPlayingLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(volumeOffLabel, GroupLayout.PREFERRED_SIZE, 13, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(volumeSlider, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(volumeOnLabel, GroupLayout.PREFERRED_SIZE, 13, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(currentDurationLabel, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(trackSlider, GroupLayout.PREFERRED_SIZE, 251, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(durationLabel)
                                .addGap(63, 63, 63)
                                .addComponent(albumCoverLabel, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE))
        );
        playerPanelLayout.setVerticalGroup(
                playerPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(previousButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(playPauseButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(nextButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(GroupLayout.Alignment.TRAILING, playerPanelLayout.createSequentialGroup()
                                .addGroup(playerPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(currentDurationLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(volumeOnLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(volumeOffLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(trackSlider, GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                                        .addComponent(volumeSlider, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(durationLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(currentlyPlayingLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(1, 1, 1))
                        .addComponent(albumCoverLabel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        /*splitPane = new JSplitPane();
        splitPane.setUI(style.getSplitPaneUI(splitPane));
        splitPane.setBorder(null);
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setFocusable(false);
        splitPane.setOpaque(false);
        splitPane.setRequestFocusEnabled(false);*/

        searchPanel = new JPanel();
        searchPanel.setOpaque(false);

        searchTable = new SquidTable();
        if (style.getSearchTableHeaderCellRendererColor() != null) {
            searchTable.getTableHeader().setDefaultRenderer(new TableHeaderCellRenderer(searchTable.getTableHeader().getDefaultRenderer(), style.getSearchTableHeaderCellRendererColor()));
        }
        searchTable.setAutoCreateRowSorter(true);
        searchTable.setFont(style.getFont());
        searchTable.setModel(new SongSearchTableModel());
        searchTable.setFillsViewportHeight(true);
        searchTable.setGridColor(new Color(204, 204, 204));
        searchTable.setIntercellSpacing(new Dimension(0, 0));
        searchTable.setRowHeight(20);
        searchTable.setSelectionBackground(style.getSearchTableSelectionBackground());
        searchTable.setShowHorizontalLines(false);
        searchTable.setShowVerticalLines(false);
        searchTable.getTableHeader().setReorderingAllowed(false);
        searchTable.setSelectionBackground(style.getSearchTableSelectionBackground());
        searchTable.setSelectionForeground(style.getSearchTableSelectionForeground());
        searchTable.getSelectionModel().addListSelectionListener(searchListSelectionListener);
        searchTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                searchTableMousePressed(evt);
            }
            @Override
            public void mouseReleased(MouseEvent evt) {
                searchTableMouseReleased(evt);
            }
        });
        AbstractHyperlinkAction<Object> act = new AbstractHyperlinkAction<Object>() {
            public void actionPerformed(ActionEvent ev) {
                if (target instanceof Artist) {
                    SwingWorker<List<Song>, Void> worker = new SwingWorker<List<Song>, Void>() {
                        @Override
                        protected List<Song> doInBackground() {
                            List<Song> songs = new ArrayList<Song>();
                            songs.addAll(Main.getSearchService().searchSongsByArtist((Artist) target));
                            return songs;
                        }

                        @Override
                        protected void done() {
                            try {
                                searchTable.setModel(new SongSearchTableModel(get()));
                            } catch (InterruptedException ex) {
                                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (ExecutionException ex) {
                                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    };
                    worker.execute();
                } else if (target instanceof Album) {
                    SwingWorker<List<Song>, Void> worker = new SwingWorker<List<Song>, Void>() {
                        @Override
                        protected List<Song> doInBackground() {
                            List<Song> songs = new ArrayList<Song>();
                            songs.addAll(Main.getSearchService().searchSongsByAlbum((Album) target));
                            return songs;
                        }

                        @Override
                        protected void done() {
                            try {
                                searchTable.setModel(new SongSearchTableModel(get()));
                            } catch (InterruptedException ex) {
                                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (ExecutionException ex) {
                                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    };
                    worker.execute();
                } else if (target instanceof Playlist) {
                    SwingWorker<List<Song>, Void> worker = new SwingWorker<List<Song>, Void>() {
                        @Override
                        protected List<Song> doInBackground() {
                            List<Song> songs = new ArrayList<Song>();
                            songs.addAll(Main.getSearchService().searchSongsByPlaylist((Playlist) target));
                            return songs;
                        }

                        @Override
                        protected void done() {
                            try {
                                searchTable.setModel(new SongSearchTableModel(get()));
                            } catch (InterruptedException ex) {
                                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (ExecutionException ex) {
                                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    };
                    worker.execute();
                }
            }
        };
        HyperlinkProvider hp = new HyperlinkProvider(act) {
            @Override
            protected JXHyperlink createRendererComponent() {
                JXRendererHyperlink renderer = new JXRendererHyperlink();
                renderer.setUnclickedColor(Color.BLACK);
                return renderer;
            }

            @Override
            protected void configureState(CellContext context) {
                if (context.getComponent() == null) {
                    return;
                }
                Point p = (Point) context.getComponent().getClientProperty(RolloverProducer.ROLLOVER_KEY);
                if (p != null && (p.x >= 0)
                        && (p.x == context.getColumn()) && (p.y == context.getRow())
                        && SquidTable.isRolloverText((JXTable) context.getComponent(), p.y, p.x)
                        && !rendererComponent.getModel().isRollover()) {
                    rendererComponent.getModel().setRollover(true);
                } else if (rendererComponent.getModel().isRollover()) {
                    rendererComponent.getModel().setRollover(false);
                }
            }
        };
        searchTable.setDefaultRenderer(Album.class, new DefaultTableRenderer(hp));
        searchTable.setDefaultRenderer(Artist.class, new DefaultTableRenderer(hp));
        searchTable.setDefaultRenderer(Playlist.class, new DefaultTableRenderer(hp));

        searchScrollPane = new JScrollPane();
        searchScrollPane.getVerticalScrollBar().setUI(style.getSearchScrollBarUI(searchScrollPane.getVerticalScrollBar()));
        searchScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        searchScrollPane.setOpaque(false);
        searchScrollPane.setViewportView(searchTable);

        downloadButton = new JButton(I18n.getLocaleString("DOWNLOAD"));
        if (style.usesButtonBackgrounds()) {
            downloadButton.setIcon(Utils.stretchImage(style.getSearchButtonsBackground(), 90, 27, this));
            downloadButton.setRolloverIcon(Utils.stretchImage(style.getSearchButtonsHoverBackground(), 90, 27, this));
            downloadButton.setPressedIcon(Utils.stretchImage(style.getSearchButtonsPressedBackground(), 90, 27, this));
            downloadButton.setBorder(null);
            downloadButton.setBorderPainted(false);
            downloadButton.setContentAreaFilled(false);
            downloadButton.setForeground(style.getSearchButtonsForeground());
        }
        downloadButton.setFont(style.getFont());
        downloadButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        downloadButton.setEnabled(false);
        downloadButton.setFocusable(false);
        downloadButton.setHorizontalTextPosition(SwingConstants.CENTER);
        downloadButton.setRequestFocusEnabled(false);
        downloadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                downloadButtonActionPerformed(evt);
            }
        });

        playButton = new JButton(I18n.getLocaleString("PLAY"));
        if (style.usesButtonBackgrounds()) {
            playButton.setIcon(Utils.stretchImage(style.getSearchButtonsBackground(), 90, 27, this));
            playButton.setRolloverIcon(Utils.stretchImage(style.getSearchButtonsHoverBackground(), 90, 27, this));
            playButton.setPressedIcon(Utils.stretchImage(style.getSearchButtonsPressedBackground(), 90, 27, this));
            playButton.setBorder(null);
            playButton.setBorderPainted(false);
            playButton.setContentAreaFilled(false);
            playButton.setForeground(style.getSearchButtonsForeground());
        }
        playButton.setFont(style.getFont());
        playButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        playButton.setEnabled(false);
        playButton.setFocusable(false);
        playButton.setHorizontalTextPosition(SwingConstants.CENTER);
        playButton.setRequestFocusEnabled(false);
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                playButtonActionPerformed(evt);
            }
        });

        searchTypeComboBox = new JComboBox();
        searchTypeComboBox.setUI(style.getSearchTypeComboBoxUI(searchTypeComboBox));
        DefaultComboBoxModel searchTypeComboBoxModel = new DefaultComboBoxModel();
        searchTypeComboBox.setModel(searchTypeComboBoxModel);
        searchTypeComboBoxModel.addElement(I18n.getLocaleString("SONGS"));
        searchTypeComboBoxModel.addElement(I18n.getLocaleString("POPULAR"));
        searchTypeComboBoxModel.addElement(I18n.getLocaleString("ALBUMS"));
        searchTypeComboBoxModel.addElement(I18n.getLocaleString("PLAYLISTS"));
        searchTypeComboBoxModel.addElement(I18n.getLocaleString("ARTISTS"));
        searchTypeComboBox.setFont(style.getFont());
        searchTypeComboBox.setBorder(style.getSearchTypeComboBoxBorder());
        searchTypeComboBox.setEnabled(false);
        searchTypeComboBox.setFocusable(false);
        searchTypeComboBox.setPreferredSize(new Dimension(63, 26));
        searchTypeComboBox.setRequestFocusEnabled(false);
        searchTypeComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                searchTypeComboBoxActionPerformed(evt);
            }
        });

        searchButton = new JButton(I18n.getLocaleString("SEARCH"));
        if (style.usesButtonBackgrounds()) {
            searchButton.setIcon(Utils.stretchImage(style.getSearchButtonsBackground(), 90, 27, this));
            searchButton.setRolloverIcon(Utils.stretchImage(style.getSearchButtonsHoverBackground(), 90, 27, this));
            searchButton.setPressedIcon(Utils.stretchImage(style.getSearchButtonsPressedBackground(), 90, 27, this));
            searchButton.setBorder(null);
            searchButton.setBorderPainted(false);
            searchButton.setContentAreaFilled(false);
            searchButton.setForeground(style.getSearchButtonsForeground());
        }
        searchButton.setFont(style.getFont());
        searchButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        searchButton.setEnabled(false);
        searchButton.setFocusable(false);
        searchButton.setHorizontalTextPosition(SwingConstants.CENTER);
        searchButton.setPreferredSize(new Dimension(90, 27));
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        searchTextField = new JTextField(I18n.getLocaleString("LOADING"));
        searchTextField.setUI(style.getSearchTextFieldUI(searchTextField));
        searchTextField.setFont(style.getFont(12));
        if (style.getSearchTextFieldBorder() != null) {
            searchTextField.setBorder(style.getSearchTextFieldBorder());
        }
        searchTextField.setEnabled(false);
        searchTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                searchTextFieldActionPerformed(evt);
            }
        });
        searchTextField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent evt) {
                searchTextFieldKeyReleased(evt);
            }
        });

        GroupLayout searchPanelLayout = new GroupLayout(searchPanel);
        searchPanel.setLayout(searchPanelLayout);
        searchPanelLayout.setHorizontalGroup(
                searchPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, searchPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(searchPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(searchScrollPane, GroupLayout.DEFAULT_SIZE, 849, Short.MAX_VALUE)
                                        .addGroup(searchPanelLayout.createSequentialGroup()
                                                .addComponent(searchTypeComboBox, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(searchPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addGroup(searchPanelLayout.createSequentialGroup()
                                                                .addComponent(downloadButton, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(playButton, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
                                                                .addGap(0, 0, Short.MAX_VALUE))
                                                        .addComponent(searchTextField))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(searchButton, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
        );
        searchPanelLayout.setVerticalGroup(
                searchPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, searchPanelLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(searchPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(searchTextField, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(searchButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(searchTypeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(searchPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(downloadButton, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(playButton, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(searchScrollPane, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                .addContainerGap())
        );

        downloadPanel = new JPanel();
        downloadPanel.setOpaque(false);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab(I18n.getLocaleString("SEARCH"), null, searchPanel, null);
        tabbedPane.addTab(I18n.getLocaleString("DOWNLOADS"), null, downloadPanel, null);


        removeFromDiskButton = new JButton(I18n.getLocaleString("REMOVE_FROM_DISK"));
        if (style.usesButtonBackgrounds()) {
            removeFromDiskButton.setIcon(Utils.stretchImage(style.getDownloadButtonsBackground(), 151, 27, this));
            removeFromDiskButton.setRolloverIcon(Utils.stretchImage(style.getDownloadButtonsHoverBackground(), 151, 27, this));
            removeFromDiskButton.setPressedIcon(Utils.stretchImage(style.getDownloadButtonsPressedBackground(), 151, 27, this));
            removeFromDiskButton.setBorder(null);
            removeFromDiskButton.setBorderPainted(false);
            removeFromDiskButton.setContentAreaFilled(false);
            removeFromDiskButton.setForeground(style.getDownloadButtonsForeground());
        }
        removeFromDiskButton.setFont(style.getFont());
        removeFromDiskButton.setEnabled(false);
        removeFromDiskButton.setFocusable(false);
        removeFromDiskButton.setHorizontalTextPosition(SwingConstants.CENTER);
        removeFromDiskButton.setRequestFocusEnabled(false);
        removeFromDiskButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                removeFromDiskButtonActionPerformed(evt);
            }
        });

        removeFromListButton = new JButton(I18n.getLocaleString("REMOVE_FROM_LIST"));
        if (style.usesButtonBackgrounds()) {
            removeFromListButton.setIcon(Utils.stretchImage(style.getDownloadButtonsBackground(), 148, 27, this));
            removeFromListButton.setRolloverIcon(Utils.stretchImage(style.getDownloadButtonsHoverBackground(), 148, 27, this));
            removeFromListButton.setPressedIcon(Utils.stretchImage(style.getDownloadButtonsPressedBackground(), 148, 27, this));
            removeFromListButton.setBorder(null);
            removeFromListButton.setBorderPainted(false);
            removeFromListButton.setContentAreaFilled(false);
            removeFromListButton.setForeground(style.getDownloadButtonsForeground());
        }
        removeFromListButton.setFont(style.getFont());
        removeFromListButton.setEnabled(false);
        removeFromListButton.setFocusable(false);
        removeFromListButton.setHorizontalTextPosition(SwingConstants.CENTER);
        removeFromListButton.setRequestFocusEnabled(false);
        removeFromListButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                removeFromListButtonActionPerformed(evt);
            }
        });

        downloadScrollPane = new JScrollPane();
        downloadScrollPane.getVerticalScrollBar().setUI(style.getDownloadScrollBarUI(downloadScrollPane.getVerticalScrollBar()));
        downloadScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        downloadScrollPane.setOpaque(false);

        downloadTable = new SquidTable();
        if (style.getDownloadTableHeaderCellRendererColor() != null) {
            downloadTable.getTableHeader().setDefaultRenderer(new TableHeaderCellRenderer(downloadTable.getTableHeader().getDefaultRenderer(), style.getDownloadTableHeaderCellRendererColor()));
        }
        downloadTable.setAutoCreateRowSorter(true);
        downloadTable.setFont(style.getFont());
        downloadTable.setModel(new DownloadTableModel());
        downloadTable.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        downloadTable.setFillsViewportHeight(true);
        downloadTable.setGridColor(new Color(204, 204, 204));
        downloadTable.setIntercellSpacing(new Dimension(0, 0));
        downloadTable.setRowHeight(20);
        downloadTable.setSelectionBackground(style.getDownloadTableSelectionBackground());
        downloadTable.setSelectionForeground(style.getDownloadTableSelectionForeground());
        downloadTable.setShowHorizontalLines(false);
        downloadTable.setShowVerticalLines(false);
        downloadTable.getTableHeader().setReorderingAllowed(false);
        downloadScrollPane.setViewportView(downloadTable);

        selectComboBox = new JComboBox();
        selectComboBox.setUI(style.getSelectComboBoxUI(selectComboBox));
        DefaultComboBoxModel selectComboBoxModel = new DefaultComboBoxModel();
        selectComboBox.setModel(selectComboBoxModel);
        selectComboBoxModel.addElement(I18n.getLocaleString("SELECT"));
        selectComboBoxModel.addElement(I18n.getLocaleString("ALL"));
        selectComboBoxModel.addElement(I18n.getLocaleString("COMPLETED"));
        selectComboBoxModel.addElement(I18n.getLocaleString("FAILED"));
        selectComboBox.setFont(style.getFont());
        selectComboBox.setBorder(style.getSelectComboBoxBorder());
        selectComboBox.setFocusable(false);
        selectComboBox.setPreferredSize(new Dimension(90, 26));
        selectComboBox.setRequestFocusEnabled(false);
        selectComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                selectComboBoxActionPerformed(evt);
            }
        });

        retryFailedDownloadsButton = new JButton(I18n.getLocaleString("RETRY_FAILED_DOWNLOADS"));
        if (style.usesButtonBackgrounds()) {
            retryFailedDownloadsButton.setIcon(Utils.stretchImage(style.getDownloadButtonsBackground(), 151, 27, this));
            retryFailedDownloadsButton.setRolloverIcon(Utils.stretchImage(style.getDownloadButtonsHoverBackground(), 151, 27, this));
            retryFailedDownloadsButton.setPressedIcon(Utils.stretchImage(style.getDownloadButtonsPressedBackground(), 151, 27, this));
            retryFailedDownloadsButton.setBorder(null);
            retryFailedDownloadsButton.setBorderPainted(false);
            retryFailedDownloadsButton.setContentAreaFilled(false);
            retryFailedDownloadsButton.setForeground(style.getDownloadButtonsForeground());
        }
        retryFailedDownloadsButton.setFont(style.getFont());
        retryFailedDownloadsButton.setFocusable(false);
        retryFailedDownloadsButton.setHorizontalTextPosition(SwingConstants.CENTER);
        retryFailedDownloadsButton.setRequestFocusEnabled(false);
        retryFailedDownloadsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                retryFailedDownloadsButtonActionPerformed(evt);
            }
        });

        GroupLayout downloadPanelLayout = new GroupLayout(downloadPanel);
        downloadPanel.setLayout(downloadPanelLayout);
        downloadPanelLayout.setHorizontalGroup(
                downloadPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(downloadPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(downloadPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(downloadPanelLayout.createSequentialGroup()
                                                .addComponent(removeFromListButton, GroupLayout.PREFERRED_SIZE, 148, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(removeFromDiskButton, GroupLayout.PREFERRED_SIZE, 151, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(retryFailedDownloadsButton, GroupLayout.PREFERRED_SIZE, 193, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(selectComboBox, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(downloadScrollPane, GroupLayout.DEFAULT_SIZE, 849, Short.MAX_VALUE))
                                .addContainerGap())
        );
        downloadPanelLayout.setVerticalGroup(
                downloadPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(downloadPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGap(18, 18, 18)
                                .addGroup(downloadPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(selectComboBox, GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                                        .addGroup(downloadPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(removeFromListButton, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(removeFromDiskButton, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(retryFailedDownloadsButton, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(downloadScrollPane, GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                                .addContainerGap())
        );

        menuBar = new JMenuBar();
        menuBar.setBackground(new Color(230, 230, 230));
        setJMenuBar(menuBar);

        fileMenu = new JMenu(I18n.getLocaleString("FILE"));
        editMenu = new JMenu(I18n.getLocaleString("EDIT"));
        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        JMenuItem batchMenuItem = new JMenuItem(I18n.getLocaleString("BATCH"));
        batchMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new BatchFrame().setVisible(true);
            }
        });
        fileMenu.add(batchMenuItem);

        if (style.isUndecorated()) {
            /*titleBarPanel = new JPanel();
            titleBarPanel.setBackground(new Color(255, 255, 255));
            titleBarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(198, 2, 196)));
            titleBarPanel.setPreferredSize(new Dimension(0, 24));
            titleBarPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        maximize();
                    }
                }
            });

            setUndecorated(true);
            ComponentMover cm = new ComponentMover(this, titleBarPanel);
            cm.setEdgeInsets(null);
            cm.setChangeCursor(false);
            ComponentResizer cr = new ComponentResizer(this);
            cr.setMinimumSize(new Dimension(820, 480));

            closeButton = new JButton();
            closeButton.setIcon(style.getCloseButtonIcon());
            closeButton.setRolloverIcon(style.getCloseButtonHoverIcon());
            closeButton.setBorderPainted(false);
            closeButton.setContentAreaFilled(false);
            closeButton.setFocusPainted(false);
            closeButton.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    closeButtonMouseClicked(evt);
                }
            });

            minimizeButton = new JButton();
            minimizeButton.setIcon(style.getMinimizeButtonIcon());
            minimizeButton.setRolloverIcon(style.getMinimizeButtonHoverIcon());
            minimizeButton.setBorderPainted(false);
            minimizeButton.setContentAreaFilled(false);
            minimizeButton.setFocusPainted(false);
            minimizeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    minimizeButtonActionPerformed(evt);
                }
            });

            maximizeButton = new JButton();
            maximizeButton.setIcon(style.getMaximizeButtonIcon());
            maximizeButton.setRolloverIcon(style.getMaximizeButtonHoverIcon());
            maximizeButton.setBorderPainted(false);
            maximizeButton.setContentAreaFilled(false);
            maximizeButton.setFocusPainted(false);
            maximizeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    maximizeButtonActionPerformed(evt);
                }
            });

            titleBarLabel = new JLabel();
            titleBarLabel.setIcon(style.getTitleBarIcon());

            GroupLayout titleBarPanelLayout = new GroupLayout(titleBarPanel);
            titleBarPanel.setLayout(titleBarPanelLayout);
            titleBarPanelLayout.setHorizontalGroup(
                    titleBarPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(titleBarPanelLayout.createSequentialGroup()
                                    .addComponent(titleBarLabel, GroupLayout.PREFERRED_SIZE, 159, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(minimizeButton, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(maximizeButton, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(closeButton, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                    .addContainerGap())
            );
            titleBarPanelLayout.setVerticalGroup(
                    titleBarPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(titleBarLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(closeButton, GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                            .addComponent(maximizeButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(minimizeButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            );*/
        }

        aboutButton = new JButton(I18n.getLocaleString("ABOUT"));
        aboutButton.setFont(style.getFont());
        aboutButton.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        aboutButton.setBorderPainted(false);
        aboutButton.setContentAreaFilled(false);
        aboutButton.setMargin(new Insets(0, 14, 2, 14));
        aboutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                aboutButtonActionPerformed(evt);
            }
        });

        settingsButton = new JButton(I18n.getLocaleString("SETTINGS"));
        settingsButton.setFont(style.getFont());
        settingsButton.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        settingsButton.setBorderPainted(false);
        settingsButton.setContentAreaFilled(false);
        settingsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                settingsButtonActionPerformed(evt);
            }
        });

        donateLabel = new JLabel();
        donateLabel = new JLabel(I18n.getLocaleString("DONATE"));
        donateLabel.setFont(style.getFont());
        donateLabel.setToolTipText("http://groovesquid.com/#donate");
        donateLabel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                linkLabelMousePressed(evt);
            }
        });

        batchButton = new JButton(I18n.getLocaleString("BATCH"));
        batchButton.setFont(style.getFont());
        batchButton.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        batchButton.setBorderPainted(false);
        batchButton.setContentAreaFilled(false);

        /*GroupLayout menuPanelLayout = new GroupLayout(menuBar);
        menuBar.setLayout(menuPanelLayout);
        menuPanelLayout.setHorizontalGroup(
                menuPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(titleBarPanel, GroupLayout.DEFAULT_SIZE, 1059, Short.MAX_VALUE)
                        .addGroup(menuPanelLayout.createSequentialGroup()
                                .addComponent(aboutButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(settingsButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(batchButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(donateLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(facebookLabel, GroupLayout.PREFERRED_SIZE, 12, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(twitterLabel, GroupLayout.PREFERRED_SIZE, 12, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
        menuPanelLayout.setVerticalGroup(
                menuPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(menuPanelLayout.createSequentialGroup()
                                .addComponent(titleBarPanel, GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(menuPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(facebookLabel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(donateLabel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 13, Short.MAX_VALUE)
                                        .addGroup(menuPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(aboutButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(twitterLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(settingsButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(batchButton))))
        );*/

        batchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                batchButtonActionPerformed(evt);
            }
        });

        adScrollPane.setBackground(new Color(204, 204, 204));
        adScrollPane.setBorder(null);
        adScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        adScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        adScrollPane.setOpaque(false);
        adScrollPane.setRequestFocusEnabled(false);

        /*adPane.setEditable(false);
        adPane.setBackground(new Color(204, 204, 204));
        adPane.setBorder(null);
        adPane.setContentType("text/html");
        adPane.setAlignmentY(JComponent.CENTER_ALIGNMENT);
        adPane.setMaximumSize(new Dimension(160, 600));
        adPane.setMinimumSize(new Dimension(160, 600));
        adPane.setOpaque(false);
        adPane.setPreferredSize(new Dimension(160, 600));
        adPane.setSize(new Dimension(160, 600));
        adScrollPane.setViewportView(adPane);
        new GetAdsThread(adPane).start();

        adPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        Desktop.getDesktop().browse(e.getURL().toURI());
                    } catch (Exception ex) {
                        Logger.getLogger(UpdateCheckThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });*/

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(playerPanel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 1059, Short.MAX_VALUE)
                                //.addComponent(menuBar, GroupLayout.DEFAULT_SIZE, 1059, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(tabbedPane)
                                /*.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(adScrollPane, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)*/
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addComponent(tabbedPane)
                                        /*.addComponent(adScrollPane, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)*/)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(playerPanel, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE))
        );

        adScrollPane.getViewport().setOpaque(false);
        setMinimumSize(new Dimension(600, 300));

        pack();
    }


    private ListSelectionListener downloadListSelectionListener = new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent event) {
            if (!event.getValueIsAdjusting()) {
                int[] selectedRows = downloadTable.getSelectedRows();
                if(selectedRows.length > 0) {
                    removeFromListButton.setEnabled(true);
                    removeFromListButton.setText(I18n.getLocaleString("REMOVE_FROM_LIST") + " (" + selectedRows.length + ")");
                    removeFromDiskButton.setEnabled(true);
                    removeFromDiskButton.setText(I18n.getLocaleString("REMOVE_FROM_DISK") + " (" + selectedRows.length + ")");
                } else {
                    removeFromListButton.setEnabled(false);
                    removeFromListButton.setText(I18n.getLocaleString("REMOVE_FROM_LIST"));
                    removeFromDiskButton.setEnabled(false);
                    removeFromDiskButton.setText(I18n.getLocaleString("REMOVE_FROM_DISK"));
                }
            }
        }
    };

    private ListSelectionListener searchListSelectionListener = new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent event) {
            if (!event.getValueIsAdjusting()) {
                int[] selectedRows = searchTable.getSelectedRows();

                String playButtonText = I18n.getLocaleString("PLAY");

                if(searchTable.getModel() instanceof AlbumSearchTableModel || searchTable.getModel() instanceof PlaylistSearchTableModel || searchTable.getModel() instanceof ArtistSearchTableModel) {
                    playButtonText = I18n.getLocaleString("SHOW_SONGS");
                }
                if(selectedRows.length > 0) {
                    downloadButton.setEnabled(true);
                    downloadButton.setText(I18n.getLocaleString("DOWNLOAD") + " (" + selectedRows.length + ")");
                    playButton.setEnabled(true);
                    playButton.setText(playButtonText + " (" + selectedRows.length + ")");
                } else {
                    downloadButton.setEnabled(false);
                    downloadButton.setText(I18n.getLocaleString("DOWNLOAD"));
                    playButton.setEnabled(false);
                    playButton.setText(playButtonText);
                }
            }
        }
    };

    private final PlaybackListener playbackListener = new PlaybackListener() {
        public void playbackStarted(Track track) {
            playPauseButton.setIcon(style.getPauseIcon());
        }

        public void playbackPaused(Track track, int audioPosition) {
            playPauseButton.setIcon(style.getPlayIcon());
        }

        public void playbackFinished(Track track, int audioPosition) {
            resetPlayInfo();
        }

        public void positionChanged(Track track, int audioPosition) {
            trackSlider.setValue(audioPosition / 1000);
            String currentPos = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(audioPosition), TimeUnit.MILLISECONDS.toSeconds(audioPosition) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(audioPosition)));
            currentDurationLabel.setText(currentPos);
        }

        public void exception(Track track, Exception ex) {

        }

        public void statusChanged(Track track) {
            if (track.getStatus() == Track.Status.ERROR) {
                resetPlayInfo();
                Main.getPlayService().stop();
            } else if(track.getStatus() == Track.Status.INITIALIZING) {
                updateCurrentlyPlayingTrack(track);
            } else if(track.getStatus() == Track.Status.DOWNLOADING) {
                trackSlider.setMaximum(track.getSong().getDuration().intValue());
                durationLabel.setText(track.getSong().getReadableDuration());
                //System.out.println(track.getTotalBytes() * 8 / track.getSong().getDuration() / 1000 + "KBP/S");
                
            } else if(track.getStatus() == Track.Status.FINISHED) {
                //currentlyPlayingLabel.setText(currentlyPlayingLabel.getText() + " (" + ((MemoryStore)track.getStore()) + "kbps)");
            }
        }

        public void downloadedBytesChanged(Track track) {

        }

        private void updateCurrentlyPlayingTrack(final Track track) {
            currentlyPlayingLabel.setText(String.format("<html><b>%s</b><br/><em>%s</em></html>", track.getSong().getName(), track.getSong().getArtist().getName()));
            trackSlider.setEnabled(true);
            trackSlider.setMaximum(track.getSong().getDuration().intValue());
            durationLabel.setText(track.getSong().getReadableDuration());
            
            SwingWorker<Image, Void> worker = new SwingWorker<Image, Void>(){
                @Override
                protected Image doInBackground() {
                    return Main.getSearchService().getLastFmCover(track.getSong());
                }

                @Override
                protected void done() {
                    try {
                        Image img = get();
                        if(img != null) {
                            img = img.getScaledInstance(albumCoverLabel.getSize().width, albumCoverLabel.getSize().height,  java.awt.Image.SCALE_SMOOTH ) ; 
                            albumCoverLabel.setIcon(new ImageIcon(img));
                        } else {
                            
                            albumCoverLabel.setIcon(null);
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            };
            worker.execute();
        }
    };

    public void playButtonActionPerformed(ActionEvent evt) {
        int[] selectedRows = searchTable.getSelectedRows();

        if (searchTable.getModel() instanceof SongSearchTableModel) {
            SongSearchTableModel model = (SongSearchTableModel) searchTable.getModel();
            List<Song> songs = new ArrayList<Song>();

            for(int selectedRow : selectedRows) {
                selectedRow = searchTable.convertRowIndexToModel(selectedRow);
                songs.add(model.getSongs().get(selectedRow));
            }
        
            play(songs);
        } else if (searchTable.getModel() instanceof AlbumSearchTableModel) {
            searchTable.setEnabled(false);
            searchTypeComboBox.setEnabled(false);
            searchTextField.setEnabled(false);
            searchButton.setEnabled(false);

            AlbumSearchTableModel model = (AlbumSearchTableModel) searchTable.getModel();
            final List<Album> albums = new ArrayList<Album>();

            for(int selectedRow : selectedRows) {
                selectedRow = searchTable.convertRowIndexToModel(selectedRow);
                albums.add(model.getAlbums().get(selectedRow));
            }
            
            SwingWorker<List<Song>, Void> worker = new SwingWorker<List<Song>, Void>(){

                @Override
                protected List<Song> doInBackground() {
                    List<Song> songs = new ArrayList<Song>();
                    for(Album album : albums) {
                        songs.addAll(Main.getSearchService().searchSongsByAlbum(album));
                    }
                    return songs;
                }

                @Override
                protected void done() {
                    try {
                        searchTable.setModel(new SongSearchTableModel(get()));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    searchTable.setEnabled(true);
                    searchTypeComboBox.setEnabled(true);
                    searchTextField.setEnabled(true);
                    searchButton.setEnabled(true);
                }
            };
            worker.execute();
            
        } else if (searchTable.getModel() instanceof PlaylistSearchTableModel) {
            searchTable.setEnabled(false);
            searchTypeComboBox.setEnabled(false);
            searchTextField.setEnabled(false);
            searchButton.setEnabled(false);

            PlaylistSearchTableModel model = (PlaylistSearchTableModel) searchTable.getModel();
            final List<Playlist> playlists = new ArrayList<Playlist>();

            for(int selectedRow : selectedRows) {
                selectedRow = searchTable.convertRowIndexToModel(selectedRow);
                playlists.add(model.getPlaylists().get(selectedRow));
            }
            
            SwingWorker<List<Song>, Void> worker = new SwingWorker<List<Song>, Void>(){

                @Override
                protected List<Song> doInBackground() {
                    List<Song> songs = new ArrayList<Song>();
                    for(Playlist playlist : playlists) {
                        songs.addAll(Main.getSearchService().searchSongsByPlaylist(playlist));
                    }
                    return songs;
                }

                @Override
                protected void done() {
                    try {
                        searchTable.setModel(new SongSearchTableModel(get()));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    searchTable.setEnabled(true);
                    searchTypeComboBox.setEnabled(true);
                    searchTextField.setEnabled(true);
                    searchButton.setEnabled(true);
                }
            };
            worker.execute();
            
        } else if (searchTable.getModel() instanceof ArtistSearchTableModel) {
            searchTable.setEnabled(false);
            searchTypeComboBox.setEnabled(false);
            searchTextField.setEnabled(false);
            searchButton.setEnabled(false);

            ArtistSearchTableModel model = (ArtistSearchTableModel) searchTable.getModel();
            final List<Artist> artists = new ArrayList<Artist>();

            for(int selectedRow : selectedRows) {
                selectedRow = searchTable.convertRowIndexToModel(selectedRow);
                artists.add(model.getArtists().get(selectedRow));
            }
            
            SwingWorker<List<Song>, Void> worker = new SwingWorker<List<Song>, Void>(){

                @Override
                protected List<Song> doInBackground() {
                    List<Song> songs = new ArrayList<Song>();
                    for(Artist artist : artists) {
                        songs.addAll(Main.getSearchService().searchSongsByArtist(artist));
                    }
                    return songs;
                }

                @Override
                protected void done() {
                    try {
                        searchTable.setModel(new SongSearchTableModel(get()));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    searchTable.setEnabled(true);
                    searchTypeComboBox.setEnabled(true);
                    searchTextField.setEnabled(true);
                    searchButton.setEnabled(true);
                }
            };
            worker.execute();
        }
        
        searchTable.getSelectionModel().clearSelection();
    }
    
    public DownloadListener getDownloadListener(final DownloadTableModel downloadTableModel) {
        final DownloadListener downloadListener = new DownloadListener() {
            public void downloadedBytesChanged(Track track) {
                int row = downloadTableModel.getSongDownloads().indexOf(track);
                if(row >= 0) {
                    downloadTableModel.fireTableCellUpdated(row, 5);
                }
            }

            public void statusChanged(Track track) {
                int row = downloadTableModel.getSongDownloads().indexOf(track);
                if(row >= 0) {
                    downloadTableModel.fireTableCellUpdated(row, 5);

                }
                downloadTableModel.updateSongDownloads();

                // fire download completed action
                if(track.getStatus() == Track.Status.FINISHED) {
                    if(Main.getConfig().getDownloadComplete() == DownloadComplete.OPEN_FILE.ordinal()) {
                        try {
                            // open file
                            Desktop.getDesktop().open(new File(track.getPath()));
                        } catch (IOException ex) {
                            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else if(Main.getConfig().getDownloadComplete() == DownloadComplete.OPEN_DIRECTORY.ordinal()) {
                        try {
                            // open dir
                            Desktop.getDesktop().open(new File(track.getPath()).getParentFile());
                        } catch (IOException ex) {
                            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        };
        return downloadListener;
    }

    public void downloadButtonActionPerformed(ActionEvent evt) {
        int[] selectedRows = searchTable.getSelectedRows();

        final DownloadTableModel downloadTableModel = (DownloadTableModel) downloadTable.getModel();
        for (int selectedRow : selectedRows) {
            selectedRow = searchTable.convertRowIndexToModel(selectedRow);
            
            if (searchTable.getModel() instanceof SongSearchTableModel) {
                SongSearchTableModel songSearchTableModel = (SongSearchTableModel) searchTable.getModel();
                Song song = songSearchTableModel.getSongs().get(selectedRow);
                downloadTableModel.addRow(0, Main.getDownloadService().download(song, getDownloadListener(downloadTableModel)));
            } else if (searchTable.getModel() instanceof AlbumSearchTableModel) {
                AlbumSearchTableModel albumSearchTableModel = (AlbumSearchTableModel) searchTable.getModel();
                final Album album = albumSearchTableModel.getAlbums().get(selectedRow);
                SwingWorker<List<Song>, Void> worker = new SwingWorker<List<Song>, Void>(){

                    @Override
                    protected List<Song> doInBackground() {
                        return Main.getSearchService().searchSongsByAlbum(album);
                    }

                    @Override
                    protected void done() {
                        try {
                            Iterator<Song> iterator = get().iterator();
                            while (iterator.hasNext()) {
                                downloadTableModel.addRow(0, Main.getDownloadService().download(iterator.next(), getDownloadListener(downloadTableModel)));
                            }
                        } catch (InterruptedException ex) {
                            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ExecutionException ex) {
                            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                };
                worker.execute();

            } else if (searchTable.getModel() instanceof PlaylistSearchTableModel) {
                PlaylistSearchTableModel playlistSearchTableModel = (PlaylistSearchTableModel) searchTable.getModel();
                final Playlist playlist = playlistSearchTableModel.getPlaylists().get(selectedRow);
                SwingWorker<List<Song>, Void> worker = new SwingWorker<List<Song>, Void>(){

                    @Override
                    protected List<Song> doInBackground() {
                        return Main.getSearchService().searchSongsByPlaylist(playlist);
                    }

                    @Override
                    protected void done() {
                        try {
                            Iterator<Song> iterator = get().iterator();
                            while (iterator.hasNext()) {
                                downloadTableModel.addRow(0, Main.getDownloadService().download(iterator.next(), getDownloadListener(downloadTableModel)));
                            }
                        } catch (InterruptedException ex) {
                            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ExecutionException ex) {
                            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                };
                worker.execute();

            }
        }
        searchTable.getSelectionModel().clearSelection();
    }

    public void searchTableMousePressed(MouseEvent evt) {
        /*
         * if(evt.getClickCount() >= 2) { JTable table =
         * (JTable)evt.getSource(); Point p = evt.getPoint(); int row =
         * table.rowAtPoint(p); int col = table.columnAtPoint(p); String value =
         * (String)table.getValueAt(row,col); SearchTableModel model =
         * (SearchTableModel) searchTable.getModel(); Song song =
         * model.getSongs().get(row); new DownloadThread(song).start(); }
         */

    }

    public void removeFromListButtonActionPerformed(ActionEvent evt) {
        removeFromList(false);
    }

    public void removeFromDiskButtonActionPerformed(ActionEvent evt) {
        int[] selectedRows = downloadTable.getSelectedRows();
        Object[] options = {I18n.getLocaleString("YES"), I18n.getLocaleString("NO")};
        if (JOptionPane.showOptionDialog(null, String.format(I18n.getLocaleString("ALERT_REMOVE_FILES_FROM_DISK"), Integer.toString(selectedRows.length)), I18n.getLocaleString("ALERT"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]) == 0) {
            removeFromList(true);
        }
    }

    public void retryFailedDownloadsButtonActionPerformed(ActionEvent evt) {
        List<Track> failedDownloads = new ArrayList<Track>();
        DownloadTableModel model = (DownloadTableModel) downloadTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            int row = downloadTable.convertRowIndexToModel(i);
            Track track = model.getSongDownloads().get(row);
            if(track.getStatus() == Track.Status.ERROR || track.getProgress() == 0) {
                failedDownloads.add(track);
            }
        }
        
        final DownloadTableModel downloadTableModel = (DownloadTableModel) downloadTable.getModel();
        for (Track track : failedDownloads) {
            Main.getDownloadService().cancelDownload(track, false);
            downloadTableModel.removeRow(track);
            downloadTableModel.addRow(0, Main.getDownloadService().download(track.getSong(), getDownloadListener(downloadTableModel)));
        }
        downloadTable.clearSelection();
        
    }

    public void searchTypeComboBoxActionPerformed(ActionEvent evt) {
        // POPULAR
        if (searchTypeComboBox.getSelectedIndex() == 1) {
            searchTextField.setText("");
            // fire actionPerformed event at searchButton
            for (ActionListener a : searchButton.getActionListeners()) {
                a.actionPerformed(evt);
            }
        } else {
            searchTextField.setEnabled(true);
            searchTextField.requestFocus();
        }
    }                                                  

    public void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {                                             
        searchTypeComboBox.setEnabled(false);
        searchTextField.setEnabled(false);
        searchButton.setEnabled(false);
        playButton.setText(I18n.getLocaleString("PLAY"));

        // Songs
        if (searchTypeComboBox.getSelectedIndex() == 0) {
            SwingWorker<List<Song>, Void> worker = new SwingWorker<List<Song>, Void>(){

                @Override
                protected List<Song> doInBackground() {
                    return Main.getSearchService().searchSongsByQuery(searchTextField.getText());
                }

                @Override
                protected void done() {
                    try {
                        searchTable.setModel(new SongSearchTableModel(get()));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    searchTable.setEnabled(true);
                    searchTypeComboBox.setEnabled(true);
                    searchTextField.setEnabled(true);
                    searchButton.setEnabled(true);
                }
            };
            worker.execute();
        // Popular
        } else if (searchTypeComboBox.getSelectedIndex() == 1) {
            SwingWorker<List<Song>, Void> worker = new SwingWorker<List<Song>, Void>(){

                @Override
                protected List<Song> doInBackground() {
                    return Main.getSearchService().searchPopular();
                }

                @Override
                protected void done() {
                    try {
                        searchTable.setModel(new SongSearchTableModel(get()));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    searchTable.setEnabled(true);
                    searchTypeComboBox.setEnabled(true);
                    searchButton.setEnabled(true);
                }
            };
            worker.execute();
        // Albums
        } else if (searchTypeComboBox.getSelectedIndex() == 2) {
            SwingWorker<List<Album>, Void> worker = new SwingWorker<List<Album>, Void>(){

                @Override
                protected List<Album> doInBackground() {
                    return Main.getSearchService().searchAlbumsByQuery(searchTextField.getText());
                }

                @Override
                protected void done() {
                    try {
                        searchTable.setModel(new AlbumSearchTableModel(get()));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    searchTable.setEnabled(true);
                    searchTypeComboBox.setEnabled(true);
                    searchTextField.setEnabled(true);
                    searchButton.setEnabled(true);
                }
            };
            worker.execute();
        // Playlists
        } else if (searchTypeComboBox.getSelectedIndex() == 3) {
            SwingWorker<List<Playlist>, Void> worker = new SwingWorker<List<Playlist>, Void>(){

                @Override
                protected List<Playlist> doInBackground() {
                    return Main.getSearchService().searchPlaylistsByQuery(searchTextField.getText());
                }

                @Override
                protected void done() {
                    try {
                        searchTable.setModel(new PlaylistSearchTableModel(get()));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    searchTable.setEnabled(true);
                    searchTypeComboBox.setEnabled(true);
                    searchTextField.setEnabled(true);
                    searchButton.setEnabled(true);
                }
            };
            worker.execute();
            
        // Artists
        } else if (searchTypeComboBox.getSelectedIndex() == 4) {
            SwingWorker<List<Artist>, Void> worker = new SwingWorker<List<Artist>, Void>(){

                @Override
                protected List<Artist> doInBackground() {
                    return Main.getSearchService().searchArtistsByQuery(searchTextField.getText());
                }

                @Override
                protected void done() {
                    try {
                        searchTable.setModel(new ArtistSearchTableModel(get()));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    searchTable.setEnabled(true);
                    searchTypeComboBox.setEnabled(true);
                    searchTextField.setEnabled(true);
                    searchButton.setEnabled(true);
                }
            };
            worker.execute();
        }
        
    }

    public void selectComboBoxActionPerformed(ActionEvent evt) {
        // All
        if(selectComboBox.getSelectedIndex() == 1) {
            DownloadTableModel model = (DownloadTableModel) downloadTable.getModel();
            downloadTable.setRowSelectionInterval(0, model.getRowCount() - 1);
        // Completed
        } else if(selectComboBox.getSelectedIndex() == 2) {
            DownloadTableModel model = (DownloadTableModel) downloadTable.getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                if(model.getSongDownloads().get(i).getProgress() == 100) {
                    if(downloadTable.getSelectedRows().length > 0) {
                        downloadTable.addRowSelectionInterval(i, i);
                    } else {
                        downloadTable.setRowSelectionInterval(i, i);
                    }
                }
            }
        // Failed
        } else if(selectComboBox.getSelectedIndex() == 3) {
            DownloadTableModel model = (DownloadTableModel) downloadTable.getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                if(model.getSongDownloads().get(i).getStatus() == Track.Status.ERROR) {
                    if(downloadTable.getSelectedRows().length > 0) {
                        downloadTable.addRowSelectionInterval(i, i);
                    } else {
                        downloadTable.setRowSelectionInterval(i, i);
                    }
                }
            }
        }
        selectComboBox.setSelectedIndex(0);
    }

    public void formWindowClosing(java.awt.event.WindowEvent evt) {
        if (Main.getDownloadService().areCurrentlyRunningDownloads()) {
            if (JOptionPane.showConfirmDialog(this, I18n.getLocaleString("ALERT_DOWNLOADS_IN_PROGRESS"), I18n.getLocaleString("ALERT"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
    }

    public void searchTextFieldKeyReleased(KeyEvent evt) {
        if(Main.getConfig().getAutocompleteEnabled()) {
            if (evt.getKeyCode() >= KeyEvent.VK_A && evt.getKeyCode() <= KeyEvent.VK_Z && (evt.getModifiers() & ActionEvent.CTRL_MASK) != ActionEvent.CTRL_MASK && ! evt.isControlDown()) {
                SwingWorker<List<String>, Void> worker = new SwingWorker<List<String>, Void>() {

                    @Override
                    protected List<String> doInBackground() {
                        return Main.getSearchService().autocompleteByQuery(searchTextField.getText());
                    }

                    @Override
                    protected void done() {
                        try {
                            autocompleteList = (ArrayList<String>) get();
                            if(autocompleteList.size() > 0) {
                                String autocomplete = autocompleteList.get(0);
                                String autocompleteSub = autocomplete.substring(searchTextField.getText().length());
                                String newText = searchTextField.getText() + autocompleteSub;
                                searchTextField.setText(newText);
                                searchTextField.select(newText.length() - autocompleteSub.length(), newText.length());
                            }
                        } catch (InterruptedException ex) {
                            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ExecutionException ex) {
                            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                };
                worker.execute();
            }
        }
    }

    public void searchTextFieldActionPerformed(ActionEvent evt) {
        for (ActionListener a : searchButton.getActionListeners()) {
            a.actionPerformed(evt);
        }
    }

    public void downloadTableKeyReleased(KeyEvent evt) {
        if(evt.getKeyCode() == KeyEvent.VK_DELETE) {
            int[] selectedRows = downloadTable.getSelectedRows();
            Object[] options = {I18n.getLocaleString("REMOVE_FROM_LIST"), I18n.getLocaleString("REMOVE_FROM_LIST_AND_DISK"), I18n.getLocaleString("CANCEL")};
            int selectedValue = JOptionPane.showOptionDialog(this, String.format(I18n.getLocaleString("ALERT_REMOVE_FROM_LIST_OR_DISK"), Integer.valueOf(selectedRows.length)), I18n.getLocaleString("ALERT"), JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            if(selectedValue == 0) {
                removeFromList(false);
            } else if(selectedValue == 1) {
                removeFromList(true);
            } else {
                System.out.println(selectedValue);
            }
        }
    }

    public void playPauseButtonMousePressed(MouseEvent evt) {
        if (Main.getPlayService().isPlaying()) {
            playPauseButton.setIcon(style.getPauseIconActive());
        } else {
            playPauseButton.setIcon(style.getPlayIconActive());
        }
    }

    public void playPauseButtonActionPerformed(ActionEvent evt) {
        if (Main.getPlayService().isPaused()) {
            Main.getPlayService().resume();
        } else if (Main.getPlayService().isPlaying()) {
            Main.getPlayService().pause();
        } else {
            if (Main.getPlayService().getPlaylist().size() > 0) {
                Main.getPlayService().play();
            } else {
                playPauseButton.setIcon(style.getPlayIcon());
            }
        }
    }

    public void nextButtonMousePressed(MouseEvent evt) {
        nextButton.setIcon(style.getNextIconActive());
    }

    public void nextButtonActionPerformed(ActionEvent evt) {
        nextButton.setIcon(style.getNextIcon());
        if (Main.getPlayService().getCurrentSongIndex() < Main.getPlayService().getPlaylist().size() - 1) {
            Main.getPlayService().skipForward();
        } else {
            Main.getPlayService().clearPlaylist();
            resetPlayInfo();
        }
    }

    public void previousButtonMousePressed(MouseEvent evt) {
        previousButton.setIcon(style.getPreviousIconActive());
    }

    public void previousButtonActionPerformed(ActionEvent evt) {
        previousButton.setIcon(style.getPreviousIcon());
        Main.getPlayService().skipBackward();
    }

    public void trackSliderMouseDragged(MouseEvent evt) {
        if (Main.getPlayService().getCurrentTrack() != null) {
            //Services.getPlayService().setCurrentPosition(trackSlider.getValue());
        }
    }                                        

    public void closeButtonMouseClicked(java.awt.event.MouseEvent evt) {                                         
        for (WindowListener a : this.getWindowListeners()) {
            a.windowClosing(null);
        }
    }

    public void maximizeButtonActionPerformed(ActionEvent evt) {
        maximize();
    }

    public void minimizeButtonActionPerformed(ActionEvent evt) {
        setState(Frame.ICONIFIED);
    }

    public void settingsButtonActionPerformed(ActionEvent evt) {
        Main.getSettingsFrame().setVisible(true);
    }

    public void aboutButtonActionPerformed(ActionEvent evt) {
        Main.getAboutFrame().setVisible(true);
    }

    public void batchButtonActionPerformed(ActionEvent evt) {
        new BatchFrame().setVisible(true);
    }

    public void volumeSliderStateChanged(ChangeEvent evt) {
        Main.getPlayService().setVolume(volumeSlider.getValue());
    }

    public void removeFromListMenuItemActionPerformed(ActionEvent evt) {
        removeFromList(false);
    }

    public void removeFromDiskMenuItemActionPerformed(ActionEvent evt) {
        removeFromDiskButtonActionPerformed(evt);
    }

    public void downloadTableMouseReleased(MouseEvent evt) {
        int r = downloadTable.rowAtPoint(evt.getPoint());
        if (SwingUtilities.isRightMouseButton(evt) && r >= 0 && r < downloadTable.getRowCount() && !ArrayUtils.contains(downloadTable.getSelectedRows(), r) && !evt.isControlDown()) {
            downloadTable.setRowSelectionInterval(r, r);
        }

        int rowindex = downloadTable.getSelectedRow();
        if (rowindex < 0)
            return;
        if (evt.isPopupTrigger() && evt.getComponent() instanceof JTable) {
            downloadTablePopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }

    public void searchTableMouseReleased(MouseEvent evt) {
        int r = searchTable.rowAtPoint(evt.getPoint());
        if (SwingUtilities.isRightMouseButton(evt) && r >= 0 && r < searchTable.getRowCount() && !ArrayUtils.contains(searchTable.getSelectedRows(), r) && !evt.isControlDown()) {
            searchTable.setRowSelectionInterval(r, r);
        }

        int rowindex = searchTable.getSelectedRow();
        if (rowindex < 0)
            return;
        if (evt.isPopupTrigger() && evt.getComponent() instanceof JTable) {
            searchTablePopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }

    public void openDirectoryMenuItemActionPerformed(ActionEvent evt) {
        int[] selectedRows = downloadTable.getSelectedRows();
        DownloadTableModel model = (DownloadTableModel) downloadTable.getModel();
        for (int i = 0; i < selectedRows.length; i++) {
            int selectedRow = downloadTable.convertRowIndexToModel(selectedRows[i] - i);
            Track track = model.getSongDownloads().get(selectedRow);
            try {
                // open dir
                Desktop.getDesktop().open(new File(track.getPath()).getParentFile());
            } catch (IOException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        downloadTable.clearSelection();
    }

    public void openFileMenuItemActionPerformed(ActionEvent evt) {
        int[] selectedRows = downloadTable.getSelectedRows();
        DownloadTableModel model = (DownloadTableModel) downloadTable.getModel();
        for (int i = 0; i < selectedRows.length; i++) {
            int selectedRow = downloadTable.convertRowIndexToModel(selectedRows[i] - i);
            Track track = model.getSongDownloads().get(selectedRow);
            try {
                // open file
                Desktop.getDesktop().open(new File(track.getPath()));
            } catch (IOException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        downloadTable.clearSelection();
    }

    public void linkLabelMousePressed(MouseEvent evt) {
        try {
            Desktop.getDesktop().browse(java.net.URI.create(((JLabel) evt.getSource()).getToolTipText()));
        } catch (IOException ex) {
            Logger.getLogger(AboutFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void downloadMenuItemActionPerformed(ActionEvent evt) {
        for (ActionListener a : downloadButton.getActionListeners()) {
            a.actionPerformed(evt);
        }
    }

    public void playMenuItemActionPerformed(ActionEvent evt) {
        for (ActionListener a : playButton.getActionListeners()) {
            a.actionPerformed(evt);
        }
    }

    // variables
    protected JButton aboutButton;
    protected JLabel albumCoverLabel;
    protected JButton batchButton;
    protected JButton closeButton;
    protected JLabel currentDurationLabel;
    protected JLabel currentlyPlayingLabel;
    protected JLabel donateLabel;
    protected JButton downloadButton;
    protected JMenuItem downloadMenuItem;
    protected JPanel downloadPanel;
    protected JScrollPane downloadScrollPane;
    protected JTable downloadTable;
    protected JPopupMenu downloadTablePopupMenu;
    protected JLabel durationLabel;
    protected JLabel facebookLabel;
    protected JMenuBar menuBar;
    protected JMenu fileMenu;
    protected JMenu editMenu;
    protected JMenuItem openDirectoryMenuItem;
    protected JMenuItem openFileMenuItem;
    protected JButton playButton;
    protected JMenuItem playMenuItem;
    protected JButton playPauseButton;
    protected JButton nextButton;
    protected JButton previousButton;
    protected JPanel playerPanel;
    protected JButton removeFromDiskButton;
    protected JMenuItem removeFromDiskMenuItem;
    protected JButton removeFromListButton;
    protected JMenuItem removeFromListMenuItem;
    protected JButton retryFailedDownloadsButton;
    protected JButton searchButton;
    protected JPanel searchPanel;
    protected JScrollPane searchScrollPane;
    protected JTable searchTable;
    protected JPopupMenu searchTablePopupMenu;
    protected JTextField searchTextField;
    protected JComboBox searchTypeComboBox;
    protected JComboBox selectComboBox;
    protected JButton settingsButton;
    protected JSlider trackSlider;
    protected JLabel twitterLabel;
    protected JLabel volumeOffLabel;
    protected JLabel volumeOnLabel;
    protected JSlider volumeSlider;
    protected JEditorPane adPane;
    protected JScrollPane adScrollPane;
    protected JTabbedPane tabbedPane;

    private void removeFromList(boolean andDisk) {
        int[] selectedRows = downloadTable.getSelectedRows();
        DownloadTableModel model = (DownloadTableModel) downloadTable.getModel();
        for (int i = 0; i < selectedRows.length; i++) {
            int selectedRow = downloadTable.convertRowIndexToModel(selectedRows[i] - i);
            Track track = model.getSongDownloads().get(selectedRow);
            if(andDisk) {
                Main.getDownloadService().cancelDownload(track, true, true);
            } else {
                Main.getDownloadService().cancelDownload(track, false);
            }
            model.removeRow(selectedRow);
        }
        downloadTable.getSelectionModel().clearSelection();
    }

    public void play(List<Song> songs) {
        final Song song = songs.get(0);
        Track track = Main.getPlayService().getCurrentTrack();

        if (track != null && track.getSong().equals(song) && Main.getPlayService().isPaused()) {
            Main.getPlayService().resume();
        } else {
            if (Main.getPlayService().isPlaying()) {
                Object[] options = {I18n.getLocaleString("PLAY_NOW"), I18n.getLocaleString("ADD_TO_QUEUE"), I18n.getLocaleString("CANCEL")};
                int selectedValue = JOptionPane.showOptionDialog(this, I18n.getLocaleString("ALERT_PLAY_NOW_OR_QUEUE"), I18n.getLocaleString("PLAY"), JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                if(selectedValue == 0) {
                    Main.getPlayService().add(songs, PlayService.AddMode.NOW);
                } else if(selectedValue == 1) {
                    Main.getPlayService().add(songs, PlayService.AddMode.NEXT);
                }
            } else {
                Main.getPlayService().add(songs, PlayService.AddMode.NOW);
            }
        }
    }
    
    public void resetPlayInfo() {
        currentlyPlayingLabel.setText("");
        playPauseButton.setIcon(style.getPlayIcon());
        trackSlider.setValue(0);
        trackSlider.setEnabled(false);
        currentlyPlayingLabel.setText("");
        currentDurationLabel.setText("00:00");
        durationLabel.setText("00:00");
        albumCoverLabel.setIcon(null);
    }
    
    public JTextField getSearchTextField() {
        return searchTextField;
    }
    
    public JButton getSearchButton() {
        return searchButton;
    }
    
    public JTable getSearchTable() {
        return searchTable;
    }
    
    public JTable getDownloadTable() {
        return downloadTable;
    }

    public JComboBox getSearchTypeComboBox() {
        return searchTypeComboBox;
    }
    
    public void initDone() {
        searchTypeComboBox.setEnabled(true);
        searchTextField.setText("");
        searchTextField.setEnabled(true);
        searchTextField.requestFocus();
        searchButton.setEnabled(true);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, I18n.getLocaleString("ERROR"), JOptionPane.ERROR_MESSAGE);
    }

    private void maximize() {
        if(getExtendedState() == JFrame.MAXIMIZED_BOTH) {
            setExtendedState(JFrame.NORMAL);
        } else {
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();  
            setMaximizedBounds(env.getMaximumWindowBounds()); 
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
    }

    public Style getStyle() {
        return style;
    }

    public void addMenuBarButtons() {
        JMenuItem aboutMenuItem = new JMenuItem(I18n.getLocaleString("ABOUT"));
        aboutMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Main.getAboutFrame().setVisible(true);
            }
        });
        fileMenu.add(aboutMenuItem);

        JMenuItem quitMenuItem = new JMenuItem(I18n.getLocaleString("QUIT"));
        quitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                formWindowClosing(null);
            }
        });
        fileMenu.add(quitMenuItem);

        JMenuItem settingsMenuItem = new JMenuItem(I18n.getLocaleString("SETTINGS"));
        settingsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Main.getSettingsFrame().setVisible(true);
            }
        });
        editMenu.add(settingsMenuItem);
    }
}
