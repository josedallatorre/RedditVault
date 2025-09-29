package com.example.redditvault.client;

import java.io.IOException;
import java.util.List;

public class DownloaderRegistry {
    private final List<MediaDownloader> downloader = List.of(
            new ImageDownloader(),
            new VideoDownloader(),
            new GalleryDownloader()
    );

    public void downloadAll(RedditSavedItem post, List<String> urls) throws IOException {
        for (String url : urls) {
            MediaDownloader downloader = this.downloader.stream()
                    .filter(d -> d.supports(post))
                    .findFirst()
                    //TODO: handle exception with a logger
                    .orElseThrow(() -> new IllegalArgumentException("No downloader for url: " + url));
            downloader.download(url);
        }
    }
}
