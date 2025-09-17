package com.example.redditvault.client;

import java.io.IOException;
import java.util.List;

public class DownloaderRegistry {
    private final List<MediaDownloader> downloader = List.of(
            new ImageDownloader(),
            new VideoDownloader()
    );

    public void downloadAll(List<String> urls) throws IOException {
        for (String url : urls) {
            MediaDownloader downloader = this.downloader.stream()
                    .filter(d -> d.supports(url))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No downloader for url: " + url));
            downloader.download(url);
        }
    }
}
