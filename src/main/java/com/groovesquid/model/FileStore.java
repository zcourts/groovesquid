package com.groovesquid.model;

import com.groovesquid.Groovesquid;
import com.groovesquid.util.Utils;
import com.mpatric.mp3agic.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;

public class FileStore implements Store {

    private static final Log log = LogFactory.getLog(FileStore.class);
    private static final Object directoryDeleteLock = new Object();

    private File downloadFile;
    private File outputFile;
    private final File downloadDir;

    public FileStore(String fileName, File downloadDir) {
        this.downloadFile = new File(downloadDir, fileName + ".download");
        this.outputFile = new File(downloadDir, fileName);
        this.downloadDir = downloadDir;

        if (downloadFile.exists()) {
            if (Groovesquid.getConfig().getFileExists() == Config.FileExists.RENAME.ordinal()) {
                int i = 1;
                fileName = FilenameUtils.removeExtension(downloadFile.getAbsolutePath());
                while (downloadFile.exists()) {
                    downloadFile = new File(downloadDir, fileName + "_" + i + ".mp3");
                    if (i >= 10) {
                        break;
                    }
                    i++;
                }
            }
        }
    }

    public OutputStream getOutputStream() throws IOException {
        File dir = downloadFile.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
            if (!dir.exists()) {
                throw new IOException("could not create directory " + dir);
            }
        }
        return new FileOutputStream(downloadFile);
    }

    public InputStream getInputStream() throws IOException {
        return new BufferedInputStream(new FileInputStream(outputFile));
    }

    public void writeTrackInfo(Track track) throws IOException {
        log.info("writing ID3 tags to " + track);
        try {
            Mp3File mp3file = new Mp3File(downloadFile);

            ID3v1 id3v1Tag;
            if (mp3file.hasId3v1Tag()) {
                id3v1Tag = mp3file.getId3v1Tag();
            } else {
                id3v1Tag = new ID3v1Tag();
                mp3file.setId3v1Tag(id3v1Tag);
            }

            ID3v2 id3v2Tag;
            if (mp3file.hasId3v2Tag()) {
                id3v2Tag = mp3file.getId3v2Tag();
            } else {
                id3v2Tag = new ID3v24Tag();
                mp3file.setId3v2Tag(id3v2Tag);
            }

            if (track.getSong().getName() != null) {
                id3v1Tag.setTitle(track.getSong().getName());
                id3v2Tag.setTitle(track.getSong().getName());
            }

            if (track.getSong().getArtistNames() != null) {
                id3v1Tag.setArtist(track.getSong().getArtistNames());
                id3v2Tag.setArtist(track.getSong().getArtistNames());
            }

            if (track.getSong().getAlbum().getName() != null) {
                id3v1Tag.setAlbum(track.getSong().getAlbum().getName());
                id3v2Tag.setAlbum(track.getSong().getAlbum().getName());
            }

            if (track.getSong().getYear() != null) {
                id3v1Tag.setYear(track.getSong().getAlbum().getName());
                id3v2Tag.setYear(track.getSong().getAlbum().getName());
            }

            Long trackNum = track.getSong().getTrackNum();
            if (trackNum != null) {
                id3v1Tag.setTrack(track.getSong().getAlbum().getName());
                id3v2Tag.setTrack(track.getSong().getAlbum().getName());
            }

            id3v1Tag.setGenre(12);
            id3v2Tag.setGenre(12);

            String comment = "Downloaded with Groovesquid (groovesquid.com)";
            id3v1Tag.setComment(comment);
            id3v2Tag.setComment(comment);

            mp3file.save(outputFile.getAbsolutePath());
            downloadFile.delete();

        } catch (Exception e) {
            downloadFile.renameTo(outputFile);
        }
    }

     public void deleteStore() {
         if (outputFile.exists()) {
             if (outputFile.delete())
                 log.debug("deleted: " + outputFile);
            else
                 log.debug("could not delete: " + outputFile);
        }

        // delete empty directories, recursively up to (but not including) the top download dir
         File dir = outputFile.getParentFile();
        synchronized (directoryDeleteLock) {
            while (dir != null && !dir.equals(downloadDir)) {
                File parent = dir.getParentFile();
                if (Utils.isEmptyDirectory(dir)) {
                    Utils.deleteQuietly(dir);
                    if (log.isDebugEnabled()) log.debug("deleted dir: " + dir);
                } else {
                    break;
                }
                dir = parent;
            }
        }
    }

    public String getDescription() {
        return outputFile.getAbsolutePath();
    }

    public boolean isSameLocation(Store other) {
        return other instanceof FileStore && outputFile.equals(((FileStore) other).outputFile);
    }
}
