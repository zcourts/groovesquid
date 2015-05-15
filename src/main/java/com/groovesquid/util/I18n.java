package com.groovesquid.util;

import com.google.common.io.ByteStreams;
import com.groovesquid.Groovesquid;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;

public class I18n {

    private final static Logger log = Logger.getLogger(I18n.class.getName());
    private static Map<Locale, Properties> translations;
    private static Properties fallback = new Properties();
    private static Locale currentLocale, defaultLocale = new Locale("en", "US");
    private static File localTranslations = new File(Groovesquid.getDataDirectory(), "translations.zip");
    private static String propertiesFilename = "general.properties";

    public static void load() {
        log.info("Loading language files!");


        try {
            InputStream in;

            if (localTranslations.exists() && new Date().getTime() - localTranslations.lastModified() < 3 * 24 * 60 * 60 * 1000) {
                log.info("Loading local translations!");
                in = new FileInputStream(localTranslations);
            } else {
                log.info("Loading translations from crowdin!");
                URLConnection conn = new URL("http://crowdin.com/download/project/groovesquid.zip").openConnection();

                String redirect = conn.getHeaderField("Location");

                if (redirect != null) {
                    conn = new URL(redirect).openConnection();
                }

                in = conn.getInputStream();
            }

            byte[] buffer = ByteStreams.toByteArray(in);
            in = new ByteArrayInputStream(buffer);
            ZipInputStream zip = new ZipInputStream(in);

            Comparator<Locale> comparator = new Comparator<Locale>() {
                public int compare(Locale l1, Locale l2) {
                    return WordUtils.capitalize(l1.getDisplayName(l1)).compareTo(WordUtils.capitalize(l2.getDisplayName(l2)));
                }
            };

            translations = new TreeMap<Locale, Properties>(comparator);

            ZipUtils.listStreams(zip, "", new ZipUtils.Consumer() {
                public void consume(String name, InputStream stream) {
                    if (!name.endsWith(propertiesFilename)) {
                        return;
                    }

                    try {
                        stream = new ByteArrayInputStream(ByteStreams.toByteArray(stream));

                        String localeString = name.substring(0, name.length() - propertiesFilename.length() - 1);
                        String parts[] = localeString.split("-", -1);
                        Locale locale;
                        if (parts.length == 1) locale = new Locale(parts[0]);
                        else if (parts.length == 2) locale = new Locale(parts[0], parts[1]);
                        else if (parts.length == 3) locale = new Locale(parts[0], parts[1], parts[2]);
                        else locale = defaultLocale;

                        InputStreamReader reader = new InputStreamReader(stream, "UTF-8");

                        Properties properties = new Properties();
                        properties.load(new BufferedReader(reader));

                        translations.put(locale, properties);

                    } catch (IOException e) {
                        log.severe(e.getMessage());
                    }
                }
            });

            in.reset();

            if (in.available() > 0) {
                FileOutputStream out = FileUtils.openOutputStream(localTranslations);
                IOUtils.copy(in, out);
                out.close();
            }

            fallback.load(I18n.class.getResourceAsStream("/i18n/" + propertiesFilename));

            log.info("Loaded the following languages: " + translations.keySet().toString());

            in.close();
        } catch (Exception e) {
            log.warning("Failed to load translations!");
            return;
        }

        Locale configLocale = LocaleUtils.toLocale(Groovesquid.getConfig().getLocale());
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
    }

    public static Set<Locale> getLocales() {
        return translations.keySet();
    }


    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    public static void setCurrentLocale(Locale locale) {
        currentLocale = locale;
        Groovesquid.getConfig().setLocale(locale.toString());
    }

    public static String getLocaleString(String string) {
        Properties properties = translations.get(currentLocale);
        String translation;
        if (properties == null) {
            properties = translations.get(defaultLocale);
        }
        if (properties != null) {
            translation = properties.getProperty(string);
            if (translation != null && !translation.isEmpty()) {
                return translation;
            } else {
                translation = translations.get(defaultLocale).getProperty(string);
                if (translation != null) {
                    return translation;
                }
            }
        }
        if (fallback != null) {
            translation = fallback.getProperty(string);
            if (translation != null) {
                return translation;
            }
        }
        return string;
    }
}
