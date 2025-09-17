package com.example.redditvault.client;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public class MediaExtractorRegistry {
    private final List<MediaExtractor> extractors = List.of(
            new VideoExtractor(),
            new GalleryExtractor(),
            new SingleImageExtractor()
    );

    public List<String> extract(JsonNode postData) {
        return extractors.stream()
                .filter(e -> e.supports(postData))
                .findFirst()
                .map(e -> e.extractMediaUrls(postData))
                .orElse(List.of());
    }
}
