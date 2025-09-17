package com.example.redditvault.client;

import com.example.redditvault.utils.DownloadUtils;

import java.io.IOException;

public class VideoDownloader implements MediaDownloader{
    @Override
    public boolean supports(String url){
        return url.contains("v.redd.it");
    }
    @Override
    public void download(String url) throws IOException{
        int i = url.indexOf("?source=fallback");
        if (i != -1) {
            url = url.substring(0, i);
        }
        // TODO: download audio if present
        String filename = DownloadUtils.generateFilename(url);
        DownloadUtils.download(url, filename);
    }
}
