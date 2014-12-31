package com.groovesquid;

import com.google.gson.Gson;
import com.groovesquid.gui.AboutFrame;
import com.groovesquid.gui.MainFrame;
import com.groovesquid.gui.SettingsFrame;
import com.groovesquid.gui.style.Flat;
import com.groovesquid.model.Clients;
import com.groovesquid.service.DownloadService;
import com.groovesquid.service.PlayService;
import com.groovesquid.service.SearchService;
import com.groovesquid.util.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.LocaleUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marius Gebhardt
 */
public class Main {
    
    private final static Logger log = Logger.getLogger(Main.class.getName());

    private static MainFrame mainFrame;
    private static SettingsFrame settingsFrame;
    private static AboutFrame aboutFrame;
      
    private static String version = "0.7.0";
    private static Clients clients = new Clients(new Clients.Client("htmlshark", "20130520", "nuggetsOfBaller"), new Clients.Client("jsqueue", "20130520", "chickenFingers"));
    private static Gson gson = new Gson();
    private static File configDir;
    private static Config config;
    private static LinkedList<Locale> locales;
    private static Locale currentLocale;
    private static DownloadService downloadService;
    private static PlayService playService;
    private static SearchService searchService;

    public static void main(String[] args) {
        log.log(Level.INFO, "Groovesquid v{0} running on {1} {2} ({3}) in {4}", new Object[]{version, System.getProperty("java.vm.name"), System.getProperty("java.runtime.version"), System.getProperty("java.vm.vendor"), System.getProperty("java.home")});

        // load config
        loadConfig();

        // load locales
        loadLocales();

        // start services
        searchService = new SearchService();
        downloadService = new DownloadService();
        playService = new PlayService(downloadService);
        
        // GUI
        if (!GraphicsEnvironment.isHeadless()) {
            // apple os x
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.aboutFrame.name", "Groovesquid");
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

            try {
                mainFrame = new Flat();
            } catch (Exception ex) {
                log.log(Level.SEVERE, null, ex);
            }
            settingsFrame = new SettingsFrame();
            aboutFrame = new AboutFrame();
        }

        // check for updates
        new UpdateCheckThread().start();

        // init grooveshark session (needed every 25min)
        new InitThread().start();
    }

    public static MainFrame getMainFrame() {
        return Main.mainFrame;
    }
    
    public static void resetGui() {
        mainFrame.dispose();
        mainFrame = new Flat();
        mainFrame.initDone();
        aboutFrame.dispose();
        aboutFrame = new AboutFrame();
        settingsFrame.dispose();
        settingsFrame = new SettingsFrame();
    }

    public static void loadConfig() {
        configDir = new File(Utils.dataDirectory() + File.separator + ".groovesquid");
        if(!configDir.exists()) {
            configDir.mkdir();
        }
        
        File oldConfigFile = new File("config.json");
        File configFile = new File(configDir + File.separator + "config.json");
        
        if(oldConfigFile.exists() && !configFile.exists()) {
            try {
                FileUtils.copyFile(oldConfigFile, configFile);
            } catch (IOException ex) {
                log.log(Level.SEVERE, null, ex);
            }
            oldConfigFile.delete();
        }

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
    
    public static void saveConfig() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                File configFile = new File(configDir + File.separator + "config.json");
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

    public static void loadLocales() {
        // find available locales

        String path = "locales";
        File jarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        List<String> fileNames = new ArrayList<String>();

        if (jarFile.isFile()) { // Run with JAR file
            try {
                final JarFile jar = new JarFile(jarFile);
                final Enumeration<JarEntry> entries = jar.entries(); // gives ALL entries in jar
                while (entries.hasMoreElements()) {
                    final String name = entries.nextElement().getName();
                    if (name.startsWith(path + "/")) { // filter according to the path
                        fileNames.add(name);
                    }
                }
                jar.close();
            } catch (IOException ex) {
                log.log(Level.SEVERE, null, log);
            }
        } else { // Run with IDE
            final URL url = Main.class.getResource("/" + path);
            if (url != null) {
                try {
                    final File files = new File(url.toURI());
                    for (File file : files.listFiles()) {
                        fileNames.add(file.getAbsolutePath());
                    }
                } catch (URISyntaxException ex) {
                    // never happens
                }
            }
        }

        locales = new LinkedList<Locale>();
        for (String fileName : fileNames) {
            String localeString = FilenameUtils.getBaseName(fileName);
            String parts[] = localeString.split("_", -1);
            Locale locale;
            if (parts.length == 2) locale = new Locale(parts[1]);
            else if (parts.length == 3) locale = new Locale(parts[1], parts[2]);
            else if (parts.length == 4) locale = new Locale(parts[1], parts[2], parts[3]);
            else locale = new Locale("en");
            locales.add(locale);
        }

        Locale configLocale = LocaleUtils.toLocale(Main.getConfig().getLocale());
        if (locales.contains(configLocale)) {
            currentLocale = configLocale;
        } else {
            Locale defaultLocale = Locale.getDefault();
            if (locales.contains(defaultLocale)) {
                currentLocale = defaultLocale;
            } else {
                currentLocale = new Locale("en", "US");
            }
        }

        // sort locales
        Collections.sort(locales, new Comparator<Locale>() {
            public int compare(Locale l1, Locale l2) {
                return l1.getDisplayName(l1).compareTo(l2.getDisplayName(l2));
            }
        });
    }

    public static List<Locale> getLocales() {
        return locales;
    }

    public static String getLocaleString(String string) {
        try {
            return ResourceBundle.getBundle("locales.general", currentLocale).getString(string);
        } catch (MissingResourceException ex) {
            try {
                return ResourceBundle.getBundle("locales.general", new Locale("en", "US")).getString(string);
            } catch (Exception ex2) {
                return string;
            }
        }
    }

    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    public static void setCurrentLocale(Locale locale) {
        currentLocale = locale;
    }
    
}
