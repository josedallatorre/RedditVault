package com.example.redditvault.client;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public class VideoExtractor implements MediaExtractor {
    @Override
    public boolean supports(JsonNode postData) {
        return postData.has("is_video") && postData.get("is_video").asBoolean();
    }

    @Override
    public List<String> extractMediaUrls(JsonNode postData) {
        List<String> urls = new ArrayList<>();
        JsonNode media = postData.get("secure_media");
        if (media != null && media.has("reddit_video")) {
            urls.add(media.get("reddit_video").get("fallback_url").asText());
        }
        return urls;
    }
}
