package com.example.redditvault.client;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public class GalleryExtractor implements MediaExtractor {
    @Override
    public boolean supports(JsonNode postData) {
        return postData.has("is_gallery") && postData.get("is_gallery").asBoolean();
    }

    @Override
    public List<String> extractMediaUrls(JsonNode postData) {
        List<String> urls = new ArrayList<>();
        JsonNode mediaMetadata = postData.get("media_metadata");
        if (mediaMetadata != null) {
            mediaMetadata.fields().forEachRemaining(entry -> {
                JsonNode item = entry.getValue();
                if (item.has("s") && item.get("s").has("u")) {
                    String url = item.get("s").get("u").asText().replaceAll("&amp;", "&");
                    urls.add(url);
                }
            });
        }
        return urls;
    }
}
