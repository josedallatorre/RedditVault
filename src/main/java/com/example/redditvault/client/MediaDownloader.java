package com.example.redditvault.client;

import java.io.IOException;

public interface MediaDownloader {
    boolean supports(RedditSavedItem post);
    void download(String url) throws IOException;
}
