package com.groovesquid.gui;

import com.groovesquid.Config;
import com.groovesquid.InitThread;
import com.groovesquid.Main;
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
    
    /**
     * Creates new form Settings
     */
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
        reconnectButton = new JButton();
        jLabel6 = new JLabel();
        jLabel1 = new JLabel();
        downloadDirectoryTextField = new JTextField();
        maxParallelDownloadsSpinner = new JSpinner();
        saveSettingsButton = new JButton();
        downloadDirectoryButton = new JButton();
        fileNameSchemeTextField = new JTextField();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        jLabel4 = new JLabel();
        autocompleteEnabledCheckBox = new JCheckBox();
        jLabel5 = new JLabel();
        downloadCompleteComboBox = new JComboBox();
        jLabel7 = new JLabel();
        languageComboBox = new JComboBox();
        resetOriginalSettingsButton = new JButton();
        jLabel8 = new JLabel();
        fileExistsComboBox = new JComboBox();
        jLabel9 = new JLabel();
        proxyHostTextField = new JTextField();
        jLabel10 = new JLabel();
        proxyPortTextField = new JTextField();

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Main.getLocaleString("SETTINGS"));
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        reconnectButton.setText(Main.getLocaleString("RECONNECT"));
        reconnectButton.setFocusable(false);
        reconnectButton.setRequestFocusEnabled(false);
        reconnectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                reconnectButtonActionPerformed(evt);
            }
        });

        jLabel6.setFont(new Font("Lucida Grande", 1, 11)); // NOI18N
        jLabel6.setText(Main.getLocaleString("MAX_PARALLEL_DOWNLOADS"));

        jLabel1.setFont(new Font("Lucida Grande", 1, 11)); // NOI18N
        jLabel1.setText(Main.getLocaleString("DOWNLOAD_DIRECTORY"));

        downloadDirectoryTextField.setRequestFocusEnabled(false);

        maxParallelDownloadsSpinner.setModel(new SpinnerNumberModel(Integer.valueOf(10), Integer.valueOf(1), null, Integer.valueOf(1)));
        maxParallelDownloadsSpinner.setValue(10);

        saveSettingsButton.setText(Main.getLocaleString("SAVE_AND_CLOSE"));
        saveSettingsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                saveSettingsButtonActionPerformed(evt);
            }
        });

        downloadDirectoryButton.setText("...");
        downloadDirectoryButton.setFocusable(false);
        downloadDirectoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                downloadDirectoryButtonActionPerformed(evt);
            }
        });

        jLabel2.setFont(new Font("Lucida Grande", 1, 11)); // NOI18N
        jLabel2.setText(Main.getLocaleString("FILENAME_SCHEME"));

        jLabel3.setFont(new Font("Lucida Grande", 0, 10)); // NOI18N
        jLabel3.setLabelFor(fileNameSchemeTextField);
        jLabel3.setText("<html><body>This is a format string for file path of downloaded files. Use / to create a subdirectory. Use &lt;Artist&gt; to insert the artist's name, &lt;Album&gt; to insert the album title, &lt;Title&gt; to insert the track's name, &lt;#&gt; to insert the track number (if any), &lt;%&gt; to insert the track order (for playlists only). Use &lt;##&gt; or &lt;###&gt; to pad the track number with leading zeroes (&lt;%%&gt; or &lt;%%%&gt; respectively for playlist track order). &lt;ALBUM&gt; inserts the album name in uppercase, &lt;album&gt; in lowercase (likewise for &lt;Artist&gt; and &lt;Title&gt;). &lt;Album?text&gt; inserts 'text' only if the album name is not empty (likewise for &lt;Artist&gt;, &lt;Title&gt;, &lt;#&gt; and &lt;%&gt;).</body></html>");

        jLabel4.setFont(new Font("Lucida Grande", 1, 11)); // NOI18N
        jLabel4.setText(Main.getLocaleString("SEARCH_AUTOCOMPLETE"));

        autocompleteEnabledCheckBox.setFont(new Font("Arial", 0, 11)); // NOI18N
        autocompleteEnabledCheckBox.setText(Main.getLocaleString("ENABLED"));

        jLabel5.setFont(new Font("Lucida Grande", 1, 11)); // NOI18N
        jLabel5.setText(Main.getLocaleString("DOWNLOAD_COMPLETED"));

        jLabel7.setFont(new Font("Lucida Grande", 1, 11)); // NOI18N
        jLabel7.setText(Main.getLocaleString("LANGUAGE"));

        resetOriginalSettingsButton.setText(Main.getLocaleString("RESET_ORIGINAL_SETTINGS"));
        resetOriginalSettingsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                resetOriginalSettingsButtonActionPerformed(evt);
            }
        });

        jLabel8.setFont(new Font("Lucida Grande", 1, 11)); // NOI18N
        jLabel8.setText(Main.getLocaleString("FILE_EXISTS"));

        jLabel9.setFont(new Font("Lucida Grande", 1, 11)); // NOI18N
        jLabel9.setText(Main.getLocaleString("PROXY_HOST"));

        jLabel10.setFont(new Font("Lucida Grande", 1, 11)); // NOI18N
        jLabel10.setText(Main.getLocaleString("PROXY_PORT"));

        org.jdesktop.layout.GroupLayout layout = new GroupLayout(getContentPane());
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
                                    .add(jLabel7, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(jLabel8, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(jLabel6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(jLabel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(jLabel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(jLabel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(jLabel5, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(jLabel9, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(jLabel10, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addPreferredGap(LayoutStyle.RELATED)
                            .add(layout.createParallelGroup(GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(downloadDirectoryTextField)
                                    .addPreferredGap(LayoutStyle.RELATED)
                                    .add(downloadDirectoryButton, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE))
                                    .add(GroupLayout.TRAILING, jLabel3, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .add(GroupLayout.TRAILING, fileNameSchemeTextField)
                            .add(layout.createSequentialGroup()
                                    .add(layout.createParallelGroup(GroupLayout.LEADING, false)
                                            .add(downloadCompleteComboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(autocompleteEnabledCheckBox)
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
                            .add(jLabel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(LayoutStyle.UNRELATED)
                    .add(layout.createParallelGroup(GroupLayout.LEADING, false)
                    .add(maxParallelDownloadsSpinner)
                            .add(jLabel6, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                    .add(layout.createParallelGroup(GroupLayout.BASELINE)
                            .add(jLabel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(fileNameSchemeTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.RELATED)
                    .add(jLabel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.UNRELATED)
                    .add(layout.createParallelGroup(GroupLayout.BASELINE)
                            .add(jLabel4, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                    .add(autocompleteEnabledCheckBox))
                .add(18, 18, 18)
                    .add(layout.createParallelGroup(GroupLayout.BASELINE)
                    .add(jLabel5)
                            .add(downloadCompleteComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .add(15, 15, 15)
                    .add(layout.createParallelGroup(GroupLayout.BASELINE)
                    .add(jLabel8)
                            .add(fileExistsComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                    .add(layout.createParallelGroup(GroupLayout.BASELINE)
                    .add(jLabel7)
                            .add(languageComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                    .add(layout.createParallelGroup(GroupLayout.BASELINE)
                    .add(jLabel9)
                            .add(proxyHostTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                    .add(layout.createParallelGroup(GroupLayout.BASELINE)
                    .add(jLabel10)
                            .add(proxyPortTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .add(131, 131, 131)
                    .add(layout.createParallelGroup(GroupLayout.BASELINE)
                    .add(reconnectButton)
                    .add(saveSettingsButton)
                    .add(resetOriginalSettingsButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void downloadDirectoryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadDirectoryButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(Main.getConfig().getDownloadDirectory()));
        chooser.setDialogTitle("Select Download Directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String downloadDirectory = chooser.getSelectedFile().getPath();
            Main.getConfig().setDownloadDirectory(downloadDirectory);
            downloadDirectoryTextField.setText(downloadDirectory);
        }
    }//GEN-LAST:event_downloadDirectoryButtonActionPerformed

    private void saveSettingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSettingsButtonActionPerformed
        if(saveSettings()) {
            dispose();
        }
    }//GEN-LAST:event_saveSettingsButtonActionPerformed

    private void reconnectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reconnectButtonActionPerformed
        // init grooveshark
        new InitThread().start();
    }//GEN-LAST:event_reconnectButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if(settingsChanged()) {
            if(JOptionPane.showConfirmDialog(this, "You did made changes to the settings. Do you want to save them?", "Save", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {
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
    }//GEN-LAST:event_formWindowClosing

    private void resetOriginalSettingsButtonActionPerformed(java.awt.event.ActionEvent evt) {
        Main.getConfig().resetSettings();
        Main.saveConfig();
        resetSettings();
    }

    private javax.swing.JCheckBox autocompleteEnabledCheckBox;
    private javax.swing.JComboBox downloadCompleteComboBox;
    private javax.swing.JButton downloadDirectoryButton;
    private javax.swing.JTextField downloadDirectoryTextField;
    private javax.swing.JComboBox fileExistsComboBox;
    private javax.swing.JTextField fileNameSchemeTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JComboBox languageComboBox;
    private javax.swing.JSpinner maxParallelDownloadsSpinner;
    private javax.swing.JTextField proxyHostTextField;
    private javax.swing.JTextField proxyPortTextField;
    private javax.swing.JButton reconnectButton;
    private javax.swing.JButton resetOriginalSettingsButton;
    private javax.swing.JButton saveSettingsButton;

    public boolean saveSettings() {
        if(checkSettings()) {
            Main.getConfig().setDownloadDirectory(downloadDirectoryTextField.getText());
            Main.getConfig().setMaxParallelDownloads((Integer) (maxParallelDownloadsSpinner.getValue()));
            Main.getConfig().setFileNameScheme(fileNameSchemeTextField.getText());
            Main.getConfig().setAutocompleteEnabled(autocompleteEnabledCheckBox.isSelected());
            Main.getConfig().setDownloadComplete(downloadCompleteComboBox.getSelectedIndex());
            if (originalLocale != languageComboBox.getSelectedItem()) {
                Locale locale = (Locale) languageComboBox.getSelectedItem();
                Main.setCurrentLocale(locale);
                Main.getConfig().setLocale(locale.toString());
                Main.resetGui();
            }
            if(!proxyHostTextField.getText().isEmpty() && !proxyPortTextField.getText().isEmpty()) {
                Main.getConfig().setProxyHost(proxyHostTextField.getText());
                try {
                    Main.getConfig().setProxyPort(Integer.parseInt(proxyPortTextField.getText()));
                } catch(NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Proxy Port must be numeric.");
                    return false;
                }
            } else {
                Main.getConfig().setProxyHost(null);
                Main.getConfig().setProxyPort(null);
            }
            setOriginalSettings();
            JOptionPane.showMessageDialog(this, "Settings have been successfully saved.");
            return true;
        } else {
            return false;
        }
    }
    
    public boolean settingsChanged() {
        return !originalDownloadDirectory.equals(downloadDirectoryTextField.getText()) || !originalMaxParallelDownloads.equals(maxParallelDownloadsSpinner.getValue().toString()) || !originalFileNameScheme.equals(fileNameSchemeTextField.getText()) || originalAutocompleteEnabled != autocompleteEnabledCheckBox.isSelected() || originalDownloadComplete != downloadCompleteComboBox.getSelectedIndex() || originalLocale != languageComboBox.getSelectedItem() && !originalProxyHost.equals(proxyHostTextField.getText()) && !originalProxyPort.equals(proxyPortTextField.getText());
    }
    
    public boolean checkSettings() {
        if(new File(downloadDirectoryTextField.getText()).exists()) {
            return true;
        } else {
            JOptionPane.showMessageDialog(this, "The given download directory does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
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
        autocompleteEnabledCheckBox.setSelected(Main.getConfig().getAutocompleteEnabled());
        
        String[] downloadCompleteActions = Config.DownloadComplete.names();
        DefaultComboBoxModel downloadCompleteComboBoxModel = new DefaultComboBoxModel();
        downloadCompleteComboBox.setModel(downloadCompleteComboBoxModel);
        for(String downloadCompleteAction : downloadCompleteActions) {
            downloadCompleteComboBoxModel.addElement(Main.getLocaleString(downloadCompleteAction));
        }
        downloadCompleteComboBox.setSelectedIndex(Main.getConfig().getDownloadComplete());
        
        String[] fileExistsActions = Config.FileExists.names();
        DefaultComboBoxModel fileExistsComboBoxModel = new DefaultComboBoxModel();
        fileExistsComboBox.setModel(fileExistsComboBoxModel);
        for(String fileExistsAction : fileExistsActions) {
            fileExistsComboBoxModel.addElement(Main.getLocaleString(fileExistsAction));
        }
        fileExistsComboBox.setSelectedIndex(Main.getConfig().getFileExists());
        
        DefaultComboBoxModel languageComboBoxModel = new DefaultComboBoxModel();
        languageComboBox.setModel(languageComboBoxModel);
        languageComboBox.setRenderer(new ComboBoxLocaleRenderer());
        for (Locale locale : Main.getLocales()) {
            languageComboBoxModel.addElement(locale);
            if (locale.equals(Main.getCurrentLocale())) {
                languageComboBox.setSelectedItem(locale);
            }
        }
        
        if(Main.getConfig().getProxyHost() != null && Main.getConfig().getProxyPort() != null) {
            proxyHostTextField.setText(Main.getConfig().getProxyHost());
            proxyPortTextField.setText(Main.getConfig().getProxyPort().toString());
        }
    }

    private void setOriginalSettings() {
        originalDownloadDirectory = downloadDirectoryTextField.getText();
        originalMaxParallelDownloads = maxParallelDownloadsSpinner.getValue().toString();
        originalFileNameScheme = fileNameSchemeTextField.getText();
        originalAutocompleteEnabled = autocompleteEnabledCheckBox.isSelected();
        originalDownloadComplete = downloadCompleteComboBox.getSelectedIndex();
        originalLocale = (Locale) languageComboBox.getSelectedItem();
        originalProxyHost = proxyHostTextField.getText();
        originalProxyPort = proxyPortTextField.getText();
    }

}
