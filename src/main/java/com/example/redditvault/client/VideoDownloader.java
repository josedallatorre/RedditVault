package com.example.redditvault.client;

import com.example.redditvault.utils.DownloadUtils;

import java.io.IOException;

public class VideoDownloader implements MediaDownloader{
    @Override
    public boolean supports(RedditSavedItem post){
        return post.getUrl_overridden_by_dest().startsWith("https://v.redd.it/");
    }
    @Override
    public void download(String url) throws IOException{
        // TODO: download audio if present
        String filename = DownloadUtils.generateFilename(url);
        DownloadUtils.download(url, filename);
    }
}
