package com.example.redditvault.client;

import java.util.List;

public interface MediaExtractor {
    boolean supports(RedditSavedItem post);
    List<String> extractMediaUrls(RedditSavedItem post);
}
