package com.example.redditvault.client;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public class VideoExtractor implements MediaExtractor {
    @Override
    public boolean supports(RedditSavedItem post) {
        return post.getIsVideo();
    }

    @Override
    public List<String> extractMediaUrls(RedditSavedItem post) {
        List<String> urls = new ArrayList<>();
        RedditMedia media = post.getSecure_media();
        if (media != null && media.getReddit_video() != null) {
            String url = media.getReddit_video().getFallback_url();
            int i = url.indexOf("?source=fallback");
            if (i != -1) {
                url = url.substring(0, i);
            }
            urls.add(url);
        }
        return urls;
    }
}
