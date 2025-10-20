package com.example.redditvault.client;

import com.example.redditvault.utils.DownloadUtils;

import java.io.IOException;

public class GalleryDownloader implements MediaDownloader {
    @Override
    public boolean supports(RedditSavedItem post) {
        return post.getIsGallery();
    }
    @Override
    public void download(String url) throws IOException {
        String filename = DownloadUtils.generateFilename(url);
        DownloadUtils.download(url, filename);
    }
}
