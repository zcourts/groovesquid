package com.groovesquid;

import com.apple.eawt.*;
import com.google.gson.Gson;
import com.groovesquid.gui.AboutFrame;
import com.groovesquid.gui.MainFrame;
import com.groovesquid.gui.SettingsFrame;
import com.groovesquid.gui.style.DefaultStyle;
import com.groovesquid.gui.style.Style;
import com.groovesquid.model.Clients;
import com.groovesquid.service.DownloadService;
import com.groovesquid.service.PlayService;
import com.groovesquid.service.SearchService;
import com.groovesquid.util.I18n;
import com.groovesquid.util.Utils;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Groovesquid {

    private final static Logger log = Logger.getLogger(Groovesquid.class.getName());

    private static MainFrame mainFrame;
    private static SettingsFrame settingsFrame;
    private static AboutFrame aboutFrame;

    private static String version = "0.8.1";
    private static Clients clients = new Clients(new Clients.Client("htmlshark", "20130520", "nuggetsOfBaller"), new Clients.Client("jsqueue", "20130520", "chickenFingers"));
    private static Gson gson = new Gson();
    private static File dataDirectory = new File(Utils.dataDirectory() + File.separator + ".groovesquid");
    private static Config config;
    private static DownloadService downloadService;
    private static PlayService playService;
    private static SearchService searchService;
    private static GroovesharkClient groovesharkClient;
    private static Style style;

    public static void main(String[] args) {
        log.log(Level.INFO, "Groovesquid v{0} running on {1} {2} ({3}) in {4}", new Object[]{version, System.getProperty("java.vm.name"), System.getProperty("java.runtime.version"), System.getProperty("java.vm.vendor"), System.getProperty("java.home")});

        // load config
        if (!dataDirectory.exists()) {
            dataDirectory.mkdir();
        }
        loadConfig();

        // load locales
        I18n.load();

        // start services
        searchService = new SearchService();
        downloadService = new DownloadService();
        playService = new PlayService(downloadService);

        // GUI
        if (!GraphicsEnvironment.isHeadless()) {

            // platform specific stuff
            String OS = System.getProperty("os.name").toLowerCase();

            if (OS.indexOf("mac") >= 0) {
                // mac os x

                System.setProperty("apple.laf.useScreenMenuBar", "true");
                System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Groovesquid");

                Application.getApplication().setAboutHandler(new AboutHandler() {
                    public void handleAbout(AppEvent.AboutEvent aboutEvent) {
                        aboutFrame.setVisible(true);
                    }
                });

                Application.getApplication().setPreferencesHandler(new PreferencesHandler() {
                    public void handlePreferences(AppEvent.PreferencesEvent preferencesEvent) {
                        settingsFrame.setVisible(true);
                    }
                });

                Application.getApplication().setQuitHandler(new QuitHandler() {
                    public void handleQuitRequestWith(AppEvent.QuitEvent quitEvent, QuitResponse quitResponse) {
                        mainFrame.formWindowClosing(null);
                    }
                });

            }
            // antialising
            System.setProperty("awt.useSystemAAFontSettings", "lcd");
            System.setProperty("swing.aatext", "true");
            // flackering bg fix
            System.setProperty("sun.awt.noerasebackground", "true");
            System.setProperty("sun.java2d.noddraw", "true");

            Toolkit.getDefaultToolkit().setDynamicLayout(true);

            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                log.log(Level.SEVERE, null, ex);
            }

            style = new DefaultStyle();
            mainFrame = new MainFrame();
            settingsFrame = new SettingsFrame();
            aboutFrame = new AboutFrame();

            if (OS.indexOf("win") >= 0 || OS.indexOf("nux") >= 0) {
                // windows & linux
                mainFrame.addMenuBarButtons();
            }
        }

        // check for updates
        new UpdateCheckThread().start();

        // init grooveshark client
        groovesharkClient = new GroovesharkClient();
    }

    public static MainFrame getMainFrame() {
        return mainFrame;
    }
    
    public static void resetGui() {
        mainFrame.dispose();
        mainFrame = new MainFrame();
        mainFrame.initDone();
        aboutFrame.dispose();
        aboutFrame = new AboutFrame();
        settingsFrame.dispose();
        settingsFrame = new SettingsFrame();
    }

    public static void loadConfig() {
        File configFile = new File(dataDirectory, "config.json");

        if(configFile.exists()) {
            try {
                Config tempConfig = gson.fromJson(FileUtils.readFileToString(configFile), Config.class);
                if(tempConfig != null) {
                    config = tempConfig;
                }
            } catch (Exception ex) {
                configFile.delete();
                log.log(Level.SEVERE, null, ex);
            }
        } else {
            config = new Config();
        }
    }

    public static File getDataDirectory() {
        return dataDirectory;
    }

    public static void saveConfig() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                File configFile = new File(dataDirectory + File.separator + "config.json");
                try {
                    FileUtils.writeStringToFile(configFile, gson.toJson(config));
                } catch (IOException ex) {
                    log.log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };  
        worker.execute();
    }
    
    public static synchronized Config getConfig() {
        return config;
    }

    public static String getVersion() {
        return version;
    }
    
    public static Clients getClients() {
        return clients;
    }

    public static SettingsFrame getSettingsFrame() {
        return settingsFrame;
    }

    public static AboutFrame getAboutFrame() {
        return aboutFrame;
    }

    public static DownloadService getDownloadService() {
        return downloadService;
    }

    public static PlayService getPlayService() {
        return playService;
    }

    public static SearchService getSearchService() {
        return searchService;
    }

    public static GroovesharkClient getGroovesharkClient() {
        return groovesharkClient;
    }

    public static Style getStyle() {
        return style;
    }

    public static void setStyle(Style style) {
        Groovesquid.style = style;
    }

}
