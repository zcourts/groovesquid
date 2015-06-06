package com.groovesquid.task;

import com.eclipsesource.json.JsonObject;
import com.groovesquid.Groovesquid;
import com.groovesquid.service.HttpService;
import com.groovesquid.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdateCheckTask extends HttpService implements Runnable {
    private final static Logger log = Logger.getLogger(Groovesquid.class.getName());
    private static String updateFile = "http://groovesquid.com/updatecheck.php";

    public UpdateCheckTask() {

    }

    public void run() {
        String response = get(updateFile);
        JsonObject json = JsonObject.readFrom(response);

        if (json != null) {
            String version = json.get("version").asString();
            if (Utils.compareVersions(version, Groovesquid.getVersion()) > 0) {
                if (JOptionPane.showConfirmDialog(null, "New version (v" + version + ") is available! Do you want to download the new version (recommended)?", "New version", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {
                    try {
                        Desktop.getDesktop().browse(java.net.URI.create("http://groovesquid.com/#download"));
                    } catch (IOException ex) {
                        log.log(Level.SEVERE, null, ex);
                    }
                }
            }

            try {
                String bannerUrl = json.get("ads").asObject().get("banner").asString();
                if (bannerUrl != null) {
                    String bannerContent = get(bannerUrl);
                    Groovesquid.getMainFrame().getAdPane().setText(bannerContent);
                }
            } catch (Exception ignored) {
            }
        }

    }
}
