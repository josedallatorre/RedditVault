package com.example.redditvault.client;

import java.io.IOException;

public interface MediaDownloader {
    boolean supports(String url);
    void download(String url) throws IOException;
}
