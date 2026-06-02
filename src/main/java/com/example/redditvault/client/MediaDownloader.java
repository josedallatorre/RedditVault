package com.example.redditvault.client;

import com.example.redditvault.client.dto.RedditSavedItem;

import java.io.IOException;

public interface MediaDownloader {
    boolean supports(RedditSavedItem post);
    void download(String url) throws IOException;
}
