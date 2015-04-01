package com.groovesquid.util;

import com.groovesquid.Main;
import org.apache.commons.lang3.LocaleUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

public class I18n {

    private final static Logger log = Logger.getLogger(I18n.class.getName());
    private static Map<Locale, Properties> translations;
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
                    if (name.startsWith(path + "/") && name.endsWith("/") && name.length() > path.length() + 1) { // filter according to the path
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

        translations = new HashMap<Locale, Properties>();
        for (String fileName : fileNames) {
            String localeString = new File(fileName).getName();
            String parts[] = localeString.split("-", -1);
            Locale locale;
            if (parts.length == 1) locale = new Locale(parts[0]);
            else if (parts.length == 2) locale = new Locale(parts[0], parts[1]);
            else if (parts.length == 3) locale = new Locale(parts[0], parts[1], parts[2]);
            else locale = defaultLocale;

            InputStream stream;
            try {
                stream = I18n.class.getResourceAsStream("/" + fileName + "general.properties");
                if (stream == null) {
                    stream = new FileInputStream(new File(fileName, "general.properties").getAbsolutePath());
                }
                Properties properties = new Properties();
                properties.load(stream);
                translations.put(locale, properties);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Locale configLocale = LocaleUtils.toLocale(Main.getConfig().getLocale());
        Locale configLangLocale = new Locale(configLocale.getLanguage());
        if (translations.keySet().contains(configLocale)) {
            currentLocale = configLocale;
        } else if (translations.keySet().contains(configLangLocale)) {
            currentLocale = configLangLocale;
        } else {
            Locale defaultLocale = Locale.getDefault();
            if (translations.keySet().contains(defaultLocale)) {
                currentLocale = defaultLocale;
            } else {
                currentLocale = defaultLocale;
            }
        }

        // sort locales
        Collections.sort(new ArrayList<Locale>(translations.keySet()), new Comparator<Locale>() {
            public int compare(Locale l1, Locale l2) {
                return l1.getDisplayName(l1).compareTo(l2.getDisplayName(l2));
            }
        });

        for (Locale locale : translations.keySet()) {
            log.info(locale.getDisplayName());
        }
    }

    public static Set<Locale> getLocales() {
        return translations.keySet();
    }


    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    public static void setCurrentLocale(Locale locale) {
        currentLocale = locale;
        Main.getConfig().setLocale(locale.toString());
    }

    public static String getLocaleString(String string) {
        Properties properties = translations.get(currentLocale);
        if (properties == null) {
            properties = translations.get(defaultLocale);
        }
        if (properties != null) {
            String translation = properties.getProperty(string);
            log.info(translation);
            if (translation != null && !translation.isEmpty()) {
                return translation;
            } else {
                return translations.get(defaultLocale).getProperty(string);
            }
        }
        return string;
    }
}
