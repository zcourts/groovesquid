package com.groovesquid.service;

import com.groovesquid.model.Track;
import com.groovesquid.service.extractor.DeezerExtractor;
import com.groovesquid.service.extractor.Extractor;
import com.groovesquid.service.extractor.NeteaseExtractor;
import com.groovesquid.service.extractor.SoundcloudExtractor;

import java.util.ArrayList;
import java.util.List;

public class ExtractorService {

    private List<Extractor> extractors = new ArrayList<Extractor>();

    public ExtractorService() {
        extractors.add(new NeteaseExtractor());
        extractors.add(new SoundcloudExtractor());
        extractors.add(new DeezerExtractor());
    }

    public String getDownloadUrl(Track track) {
        for (Extractor extractor : extractors) {
            String downloadUrl = extractor.getDownloadUrl(track);
            if (downloadUrl != null) {
                return downloadUrl;
            }
        }
        return null;
    }

}
