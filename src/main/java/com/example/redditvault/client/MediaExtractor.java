package com.example.redditvault.client;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface MediaExtractor {
    boolean supports(JsonNode postData);
    List<String> extractMediaUrls(JsonNode postData);
}
