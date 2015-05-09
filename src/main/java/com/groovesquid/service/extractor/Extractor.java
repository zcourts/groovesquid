package com.groovesquid.service.extractor;

import com.groovesquid.model.Track;

public interface Extractor {

    String getDownloadUrl(Track track);

}
