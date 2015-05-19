package com.groovesquid.service.hoster;

import com.groovesquid.model.Track;
import com.groovesquid.service.HttpService;
import com.groovesquid.util.Utils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static java.lang.String.format;

public class Hoster extends HttpService {

    protected String name;

    public String getDownloadUrl(Track track) {
        return null;
    }

    public void download(Track track, OutputStream outputStream) throws IOException {
        HttpGet httpGet = new HttpGet(track.getDownloadUrl());
        httpGet.setHeaders(browserHeaders);
        HttpResponse httpResponse = httpClient.execute(httpGet);
        HttpEntity httpEntity = httpResponse.getEntity();
        try {
            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                track.setTotalBytes(httpEntity.getContentLength());

                InputStream instream = httpEntity.getContent();
                byte[] buf = new byte[10240];
                int l;
                while ((l = instream.read(buf)) != -1) {
                    outputStream.write(buf, 0, l);
                }
                // need to close immediately otherwise we cannot write ID tags
                outputStream.close();
                outputStream = null;
                // write ID tags
                track.getStore().writeTrackInfo(track);
            } else {
                throw new HttpResponseException(statusCode, format("%s: %d %s", track.getDownloadUrl(), statusCode, statusLine.getReasonPhrase()));
            }
        } finally {
            try {
                EntityUtils.consume(httpEntity);
            } catch (IOException ignore) {
                // ignored
            }
            Utils.closeQuietly(outputStream, track.getStore().getDescription());
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
