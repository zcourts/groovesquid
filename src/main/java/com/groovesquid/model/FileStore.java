package com.groovesquid.model;

import com.groovesquid.Config;
import com.groovesquid.Main;
import com.groovesquid.util.Utils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagConstant;
import org.farng.mp3.TagException;
import org.farng.mp3.TagOptionSingleton;
import org.farng.mp3.id3.*;

import java.io.*;

public class FileStore implements Store {

    private static final Log log = LogFactory.getLog(FileStore.class);
    private static final Object directoryDeleteLock = new Object();

    private File file;
    private final File downloadDir;

    public FileStore(String fileName, File downloadDir) {
        // remove special characters
        fileName = fileName.replaceAll("[^a-zA-Z0-9.-]", "_");

        this.file = new File(downloadDir, fileName);
        this.downloadDir = downloadDir;

        if (file.exists()) {
            if (Main.getConfig().getFileExists() == Config.FileExists.RENAME.ordinal()) {
                int i = 1;
                fileName = FilenameUtils.removeExtension(file.getAbsolutePath());
                while (file.exists()) {
                    file = new File(downloadDir, fileName + "_" + i + ".mp3");
                    if (i >= 10) {
                        break;
                    }
                    i++;
                }
            }
        }
    }

    public OutputStream getOutputStream() throws IOException {
        File dir = file.getParentFile();
        if (!dir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
            if (!dir.exists()) {
                throw new IOException("could not create directory " + dir);
            }
        }
        return new FileOutputStream(file);
    }

    public InputStream getInputStream() throws IOException {
        return new BufferedInputStream(new FileInputStream(file));
    }

    public void writeTrackInfo(Track track) throws IOException {
        log.info("writing ID3 tags to " + track);

        try {
            ID3v1 id3v1 = new ID3v1_1();
            ID3v2_3 id3v2 = new ID3v2_3();

            String artistName = track.getSong().getArtist().getName();
            if (artistName != null) {
                id3v1.setArtist(artistName);
                ID3v2_3Frame frame;
                AbstractID3v2FrameBody frameBody;
                frameBody = new FrameBodyTPE1((byte) 0, artistName);
                frame = new ID3v2_3Frame(frameBody);
                id3v2.setFrame(frame);
            }

            String albumName = track.getSong().getAlbum().getName();
            if (albumName != null) {
                id3v1.setAlbum(albumName);
                ID3v2_3Frame frame;
                AbstractID3v2FrameBody frameBody;
                frameBody = new FrameBodyTALB((byte) 0, albumName);
                frame = new ID3v2_3Frame(frameBody);
                id3v2.setFrame(frame);
            }

            String songName = track.getSong().getName();
            if (songName != null) {
                id3v1.setTitle(songName);
                ID3v2_3Frame frame;
                AbstractID3v2FrameBody frameBody;
                frameBody = new FrameBodyTIT2((byte) 0, songName);
                frame = new ID3v2_3Frame(frameBody);
                id3v2.setFrame(frame);
            }

            String year = track.getSong().getYear();
            if (year != null) {
                id3v1.setYear(year);
                try {
                    ID3v2_3Frame frame;
                    AbstractID3v2FrameBody frameBody;
                    frameBody = new FrameBodyTDRC((byte) 0, year);
                    frame = new ID3v2_3Frame(frameBody);
                    id3v2.setFrame(frame);
                } catch (NumberFormatException ignore) {
                    // ignored
                }
            }

            Long trackNum = track.getSong().getTrackNum();
            if (trackNum != null) {
                ID3v2_3Frame frame;
                AbstractID3v2FrameBody frameBody;
                frameBody = new FrameBodyTRCK((byte) 0, trackNum.toString());
                frame = new ID3v2_3Frame(frameBody);
                id3v2.setFrame(frame);
            }
                        
            // let's advertise ourself a bit
            String comment = "Downloaded with Groovesquid (com.groovesquid.com)";
            id3v1.setComment(comment);
            ID3v2_3Frame frame;
            AbstractID3v2FrameBody frameBody;
            frameBody = new FrameBodyCOMM((byte) 0, "eng", comment, comment);
            frame = new ID3v2_3Frame(frameBody);
            id3v2.setFrame(frame);

            MP3File mp3File = new MP3File(file);
            TagOptionSingleton.getInstance().setDefaultSaveMode(TagConstant.MP3_FILE_SAVE_OVERWRITE);
            mp3File.setID3v1Tag(id3v1);
            mp3File.setID3v2Tag(id3v2);
            mp3File.save();
            // very dirty, TODO: prevent Jid3lib to generate the ".original" files
            File originalFile = new File(FilenameUtils.removeExtension(file.toString()) + ".original.mp3");
            if(originalFile.exists())
                originalFile.delete();
        } catch (TagException ex) {
            throw new IOException("cannot write ID3 tags to file " + file + "; track: " + track + "; reason: " + ex, ex);
        }
    }

     public void deleteStore() {
        if (file.exists()) {
            if (file.delete())
                log.debug("deleted: " + file);
            else
                log.debug("could not delete: " + file);
        }

        // delete empty directories, recursively up to (but not including) the top download dir
        File dir = file.getParentFile();
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
        return file.getAbsolutePath();
    }

    public boolean isSameLocation(Store other) {
        return other instanceof FileStore && file.equals(((FileStore)other).file);
    }
}
