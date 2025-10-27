package com.example.redditvault.client;

import java.util.List;

public class MediaExtractorRegistry {
    private final List<MediaExtractor> extractors = List.of(
        //TODO: add media extarnal to Reddit (e.g., Imgur, Gfycat, etc.)
            new VideoExtractor(),
            new GalleryExtractor(),
            new SingleImageExtractor()
    );

    public List<String> extract(RedditSavedItem post) {
        return extractors.stream()
                .filter(e -> e.supports(post))
                .findFirst()
                .map(e -> e.extractMediaUrls(post))
                .orElse(List.of());
    }
}
