package com.example.redditvault.client;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public class SingleImageExtractor implements MediaExtractor {
    @Override
    public boolean supports(JsonNode postData) {
        return postData.has("url_overridden_by_dest");
    }

    @Override
    public List<String> extractMediaUrls(JsonNode postData) {
        return List.of(postData.get("url_overridden_by_dest").asText());
    }
}
