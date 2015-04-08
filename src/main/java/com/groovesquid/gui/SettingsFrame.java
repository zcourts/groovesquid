package com.groovesquid.gui;

import com.bulenkov.iconloader.IconLoader;
import com.groovesquid.Config;
import com.groovesquid.Main;
import com.groovesquid.util.I18n;
import org.apache.commons.lang3.text.WordUtils;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Locale;

@SuppressWarnings({"serial", "rawtypes", "unchecked"})
public class SettingsFrame extends JFrame {

    private String originalDownloadDirectory, originalMaxParallelDownloads, originalFileNameScheme, originalProxyHost, originalProxyPort;
    private boolean originalAutocompleteEnabled;
    private int originalDownloadComplete;
    private Locale originalLocale;

    private JCheckBox searchAutocompleteCheckBox;
    private JComboBox downloadCompletedComboBox;
    private JButton downloadDirectoryButton;
    private JTextField downloadDirectoryTextField;
    private JComboBox fileExistsComboBox;
    private JTextField fileNameSchemeTextField;
    private JLabel downloadDirectoryLabel;
    private JLabel proxyPortLabel;
    private JLabel filenameSchemeLabel;
    private JLabel filenameSchemeDescriptionLabel;
    private JLabel searchAutocompleteLabel;
    private JLabel downloadCompletedLabel;
    private JLabel maxParallelDownloadsLabel;
    private JLabel languageLabel;
    private JLabel fileExistsLabel;
    private JLabel proxyHostLabel;
    private JComboBox languageComboBox;
    private JSpinner maxParallelDownloadsSpinner;
    private JTextField proxyHostTextField;
    private JTextField proxyPortTextField;
    private JButton reconnectButton;
    private JButton resetOriginalSettingsButton;
    private JButton saveSettingsButton;

    public SettingsFrame() {
        initComponents();

        // center screen
        setLocationRelativeTo(null);
        
        // icon
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/gui/icon.png")));
        
        // update config settings
        resetSettings();
        
        // original settings
        setOriginalSettings();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(I18n.getLocaleString("SETTINGS"));
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        reconnectButton = new JButton(I18n.getLocaleString("RECONNECT"));
        reconnectButton.setFocusable(false);
        reconnectButton.setRequestFocusEnabled(false);
        reconnectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                reconnectButtonActionPerformed(evt);
            }
        });

        maxParallelDownloadsLabel = new JLabel(I18n.getLocaleString("MAX_PARALLEL_DOWNLOADS"));
        maxParallelDownloadsLabel.setFont(new Font("Lucida Grande", 1, 11));

        downloadDirectoryLabel = new JLabel(I18n.getLocaleString("DOWNLOAD_DIRECTORY"));
        downloadDirectoryLabel.setFont(new Font("Lucida Grande", 1, 11));

        downloadDirectoryTextField = new JTextField();
        downloadDirectoryTextField.setRequestFocusEnabled(false);

        maxParallelDownloadsSpinner = new JSpinner();
        maxParallelDownloadsSpinner.setModel(new SpinnerNumberModel(Integer.valueOf(10), Integer.valueOf(1), null, Integer.valueOf(1)));
        maxParallelDownloadsSpinner.setValue(10);

        saveSettingsButton = new JButton(I18n.getLocaleString("SAVE_AND_CLOSE"));
        saveSettingsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                saveSettingsButtonActionPerformed(evt);
            }
        });

        downloadDirectoryButton = new JButton("...");
        downloadDirectoryButton.setFocusable(false);
        downloadDirectoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                downloadDirectoryButtonActionPerformed(evt);
            }
        });

        fileNameSchemeTextField = new JTextField();
        fileNameSchemeTextField.setFont(new Font(Font.MONOSPACED, 0, 12));

        filenameSchemeLabel = new JLabel(I18n.getLocaleString("FILENAME_SCHEME"));
        filenameSchemeLabel.setFont(new Font("Lucida Grande", 1, 11));

        filenameSchemeDescriptionLabel = new JLabel();
        filenameSchemeDescriptionLabel.setFont(new Font("Lucida Grande", 0, 11));
        filenameSchemeDescriptionLabel.setLabelFor(fileNameSchemeTextField);
        String tagStyle = "font-family: Monospaced; font-weight: bold;";
        filenameSchemeDescriptionLabel.setText("<html><body>" + I18n.getLocaleString("FILENAME_SCHEME_DESCRIPTION").replace("&lt;", "<span style=\"" + tagStyle + "\">&lt;").replace("&gt;", "&gt;</span>").replaceFirst("/", "<span style=\"" + tagStyle + "\">/</span>") + "</body></html>");

        searchAutocompleteLabel = new JLabel(I18n.getLocaleString("SEARCH_AUTOCOMPLETE"));
        searchAutocompleteLabel.setFont(new Font("Lucida Grande", 1, 11));

        searchAutocompleteCheckBox = new JCheckBox(I18n.getLocaleString("ENABLED"));
        searchAutocompleteCheckBox.setFont(new Font("Arial", 0, 11));

        downloadCompletedLabel = new JLabel(I18n.getLocaleString("DOWNLOAD_COMPLETED"));
        downloadCompletedLabel.setFont(new Font("Lucida Grande", 1, 11));

        downloadCompletedComboBox = new JComboBox();

        languageLabel = new JLabel(I18n.getLocaleString("LANGUAGE"));
        languageLabel.setFont(new Font("Lucida Grande", 1, 11));

        languageComboBox = new JComboBox();
        DefaultComboBoxModel languageComboBoxModel = new DefaultComboBoxModel();
        languageComboBox.setModel(languageComboBoxModel);
        languageComboBox.setRenderer(new ComboBoxLocaleRenderer());
        for (Locale locale : I18n.getLocales()) {
            languageComboBoxModel.addElement(locale);
            if (locale.equals(I18n.getCurrentLocale())) {
                languageComboBox.setSelectedItem(locale);
            }
        }
        languageComboBox.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                JLabel label = new JLabel();
                label.setOpaque(false);
                if (isSelected) {
                    label.setOpaque(true);
                    label.setForeground(comp.getForeground());
                    label.setBackground(comp.getBackground());
                } else {
                    label.setBackground(comp.getBackground());
                }
                label.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
                list.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                list.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

                Locale locale = (Locale) value;

                Icon icon = IconLoader.getIcon("/gui/flags/" + locale.getCountry().toUpperCase() + ".png");
                if (icon == null) {
                    icon = IconLoader.getIcon("/gui/flags/" + locale.getLanguage().toUpperCase() + ".png");
                }

                String languageNameForeign = WordUtils.capitalize(locale.getDisplayName(locale));
                String languageNameOwn = WordUtils.capitalize(locale.getDisplayName(I18n.getCurrentLocale()));

                label.setIcon(icon);
                label.setText("<html>" + languageNameForeign + (!isSelected ? "<font color=gray>" : "") + " — " + languageNameOwn + (!isSelected ? "</font>" : "") + "</html>");

                return label;
            }
        });

        resetOriginalSettingsButton = new JButton(I18n.getLocaleString("RESET_ORIGINAL_SETTINGS"));
        resetOriginalSettingsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                resetOriginalSettingsButtonActionPerformed(evt);
            }
        });

        fileExistsLabel = new JLabel();
        fileExistsLabel.setFont(new Font("Lucida Grande", 1, 11));
        fileExistsLabel.setText(I18n.getLocaleString("FILE_EXISTS"));

        fileExistsComboBox = new JComboBox();

        proxyHostLabel = new JLabel(I18n.getLocaleString("PROXY_HOST"));
        proxyHostLabel.setFont(new Font("Lucida Grande", 1, 11));

        proxyHostTextField = new JTextField();

        proxyPortLabel = new JLabel(I18n.getLocaleString("PROXY_PORT"));
        proxyPortLabel.setFont(new Font("Lucida Grande", 1, 11));

        proxyPortTextField = new JTextField();

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                    .add(layout.createParallelGroup(GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(reconnectButton)
                            .addPreferredGap(LayoutStyle.RELATED, 75, Short.MAX_VALUE)
                            .add(resetOriginalSettingsButton, GroupLayout.PREFERRED_SIZE, 227, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.RELATED)
                            .add(saveSettingsButton, GroupLayout.PREFERRED_SIZE, 141, GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                            .add(layout.createParallelGroup(GroupLayout.TRAILING, false)
                                    .add(languageLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(fileExistsLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(maxParallelDownloadsLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(filenameSchemeLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(downloadDirectoryLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(searchAutocompleteLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(downloadCompletedLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(proxyHostLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(proxyPortLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addPreferredGap(LayoutStyle.RELATED)
                            .add(layout.createParallelGroup(GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(downloadDirectoryTextField)
                                    .addPreferredGap(LayoutStyle.RELATED)
                                    .add(downloadDirectoryButton, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE))
                                    .add(GroupLayout.TRAILING, filenameSchemeDescriptionLabel, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .add(GroupLayout.TRAILING, fileNameSchemeTextField)
                            .add(layout.createSequentialGroup()
                                    .add(layout.createParallelGroup(GroupLayout.LEADING, false)
                                            .add(downloadCompletedComboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .add(searchAutocompleteCheckBox)
                                            .add(maxParallelDownloadsSpinner, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                                            .add(fileExistsComboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .add(languageComboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(proxyHostTextField)
                                    .add(proxyPortTextField))
                                .add(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                    .addContainerGap()
                    .add(layout.createParallelGroup(GroupLayout.BASELINE)
                            .add(downloadDirectoryTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .add(downloadDirectoryButton)
                            .add(downloadDirectoryLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(LayoutStyle.UNRELATED)
                    .add(layout.createParallelGroup(GroupLayout.LEADING, false)
                            .add(maxParallelDownloadsSpinner)
                            .add(maxParallelDownloadsLabel, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
                    .add(18, 18, 18)
                    .add(layout.createParallelGroup(GroupLayout.BASELINE)
                            .add(filenameSchemeLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(fileNameSchemeTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.RELATED)
                    .add(filenameSchemeDescriptionLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.UNRELATED)
                    .add(layout.createParallelGroup(GroupLayout.BASELINE)
                            .add(searchAutocompleteLabel, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                            .add(searchAutocompleteCheckBox))
                    .add(18, 18, 18)
                    .add(layout.createParallelGroup(GroupLayout.BASELINE)
                            .add(downloadCompletedLabel)
                            .add(downloadCompletedComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .add(15, 15, 15)
                    .add(layout.createParallelGroup(GroupLayout.BASELINE)
                            .add(fileExistsLabel)
                            .add(fileExistsComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .add(18, 18, 18)
                    .add(layout.createParallelGroup(GroupLayout.BASELINE)
                            .add(languageLabel)
                            .add(languageComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .add(18, 18, 18)
                    .add(layout.createParallelGroup(GroupLayout.BASELINE)
                            .add(proxyHostLabel)
                            .add(proxyHostTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .add(18, 18, 18)
                    .add(layout.createParallelGroup(GroupLayout.BASELINE)
                            .add(proxyPortLabel)
                            .add(proxyPortTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .add(50, 50, 50)
                    .add(layout.createParallelGroup(GroupLayout.BASELINE)
                            .add(reconnectButton)
                            .add(saveSettingsButton)
                            .add(resetOriginalSettingsButton))
                    .addContainerGap())
        );

        setMinimumSize(new Dimension(700, 620));

        pack();
    }

    private void downloadDirectoryButtonActionPerformed(ActionEvent evt) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(Main.getConfig().getDownloadDirectory()));
        chooser.setDialogTitle(I18n.getLocaleString("SELECT_DOWNLOAD_DIRECTORY"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String downloadDirectory = chooser.getSelectedFile().getPath();
            Main.getConfig().setDownloadDirectory(downloadDirectory);
            downloadDirectoryTextField.setText(downloadDirectory);
        }
    }

    private void saveSettingsButtonActionPerformed(ActionEvent evt) {
        if(saveSettings()) {
            dispose();
        }
    }

    private void reconnectButtonActionPerformed(ActionEvent evt) {
        // init grooveshark
        Main.getGroovesharkClient().init();
    }

    private void formWindowClosing(WindowEvent evt) {
        if(settingsChanged()) {
            if (JOptionPane.showConfirmDialog(this, I18n.getLocaleString("ALERT_UNSAVED_CHANGES"), I18n.getLocaleString("ALERT"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {
                if(saveSettings()) {
                    dispose();
                }
            } else {
                resetSettings();
                dispose();
            }
        } else {
            dispose();
        }
    }

    private void resetOriginalSettingsButtonActionPerformed(ActionEvent evt) {
        Main.getConfig().resetSettings();
        Main.saveConfig();
        resetSettings();
    }

    public boolean saveSettings() {
        if(checkSettings()) {
            Main.getConfig().setDownloadDirectory(downloadDirectoryTextField.getText());
            Main.getConfig().setMaxParallelDownloads((Integer) (maxParallelDownloadsSpinner.getValue()));
            Main.getConfig().setFileNameScheme(fileNameSchemeTextField.getText());
            Main.getConfig().setAutocompleteEnabled(searchAutocompleteCheckBox.isSelected());
            Main.getConfig().setDownloadComplete(downloadCompletedComboBox.getSelectedIndex());
            if (originalLocale != languageComboBox.getSelectedItem()) {
                Locale locale = (Locale) languageComboBox.getSelectedItem();
                I18n.setCurrentLocale(locale);
                Main.resetGui();
            }
            if(!proxyHostTextField.getText().isEmpty() && !proxyPortTextField.getText().isEmpty()) {
                Main.getConfig().setProxyHost(proxyHostTextField.getText());
                try {
                    Main.getConfig().setProxyPort(Integer.parseInt(proxyPortTextField.getText()));
                } catch(NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, I18n.getLocaleString("INVALID_PROXY_PORT"));
                    return false;
                }
            } else {
                Main.getConfig().setProxyHost(null);
                Main.getConfig().setProxyPort(null);
            }
            setOriginalSettings();
            JOptionPane.showMessageDialog(this, I18n.getLocaleString("SETTINGS_SAVED"));
            return true;
        } else {
            return false;
        }
    }
    
    public boolean settingsChanged() {
        return !originalDownloadDirectory.equals(downloadDirectoryTextField.getText()) || !originalMaxParallelDownloads.equals(maxParallelDownloadsSpinner.getValue().toString()) || !originalFileNameScheme.equals(fileNameSchemeTextField.getText()) || originalAutocompleteEnabled != searchAutocompleteCheckBox.isSelected() || originalDownloadComplete != downloadCompletedComboBox.getSelectedIndex() || originalLocale != languageComboBox.getSelectedItem() && !originalProxyHost.equals(proxyHostTextField.getText()) && !originalProxyPort.equals(proxyPortTextField.getText());
    }
    
    public boolean checkSettings() {
        if (new File(downloadDirectoryTextField.getText()).exists()) {
            return true;
        } else {
            JOptionPane.showMessageDialog(this, I18n.getLocaleString("INVALID_DOWNLOAD_DIRECTORY"), I18n.getLocaleString("ERROR"), JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    
    public JTextField getDownloadDirectoryTextField() {
        return downloadDirectoryTextField;
    }

    public JSpinner getMaxParallelDownloadsSpinner() {
        return maxParallelDownloadsSpinner;
    }

    private void resetSettings() {
        downloadDirectoryTextField.setText(Main.getConfig().getDownloadDirectory());
        maxParallelDownloadsSpinner.setValue(Main.getConfig().getMaxParallelDownloads());
        fileNameSchemeTextField.setText(Main.getConfig().getFileNameScheme());
        searchAutocompleteCheckBox.setSelected(Main.getConfig().getAutocompleteEnabled());
        
        String[] downloadCompleteActions = Config.DownloadComplete.names();
        DefaultComboBoxModel downloadCompleteComboBoxModel = new DefaultComboBoxModel();
        downloadCompletedComboBox.setModel(downloadCompleteComboBoxModel);
        for(String downloadCompleteAction : downloadCompleteActions) {
            downloadCompleteComboBoxModel.addElement(I18n.getLocaleString(downloadCompleteAction));
        }
        downloadCompletedComboBox.setSelectedIndex(Main.getConfig().getDownloadComplete());
        
        String[] fileExistsActions = Config.FileExists.names();
        DefaultComboBoxModel fileExistsComboBoxModel = new DefaultComboBoxModel();
        fileExistsComboBox.setModel(fileExistsComboBoxModel);
        for(String fileExistsAction : fileExistsActions) {
            fileExistsComboBoxModel.addElement(I18n.getLocaleString(fileExistsAction));
        }
        fileExistsComboBox.setSelectedIndex(Main.getConfig().getFileExists());

        if(Main.getConfig().getProxyHost() != null && Main.getConfig().getProxyPort() != null) {
            proxyHostTextField.setText(Main.getConfig().getProxyHost());
            proxyPortTextField.setText(Main.getConfig().getProxyPort().toString());
        }
    }

    private void setOriginalSettings() {
        originalDownloadDirectory = downloadDirectoryTextField.getText();
        originalMaxParallelDownloads = maxParallelDownloadsSpinner.getValue().toString();
        originalFileNameScheme = fileNameSchemeTextField.getText();
        originalAutocompleteEnabled = searchAutocompleteCheckBox.isSelected();
        originalDownloadComplete = downloadCompletedComboBox.getSelectedIndex();
        originalLocale = (Locale) languageComboBox.getSelectedItem();
        originalProxyHost = proxyHostTextField.getText();
        originalProxyPort = proxyPortTextField.getText();
    }

}
