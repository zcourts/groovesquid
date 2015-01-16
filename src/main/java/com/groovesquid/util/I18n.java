package com.groovesquid.util;

import com.groovesquid.Main;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.LocaleUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

public class I18n {

    private final static Logger log = Logger.getLogger(I18n.class.getName());
    private static LinkedList<Locale> locales;
    private static Locale currentLocale, defaultLocale = new Locale("en");

    public static void load() {
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
            else locale = defaultLocale;
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
                currentLocale = defaultLocale;
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


    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    public static void setCurrentLocale(Locale locale) {
        currentLocale = locale;
        Main.getConfig().setLocale(locale.toString());
    }

    public static String getLocaleString(String string) {
        try {
            return ResourceBundle.getBundle("locales.general", currentLocale).getString(string);
        } catch (MissingResourceException ex) {
            try {
                return ResourceBundle.getBundle("locales.general", defaultLocale).getString(string);
            } catch (Exception ex2) {
                return string;
            }
        }
    }
}
