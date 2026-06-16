package com.example.redditvault.client;

import com.example.redditvault.client.dto.RedditSavedItem;

import java.util.List;

public interface MediaExtractor {
    boolean supports(RedditSavedItem post);
    List<String> extractMediaUrls(RedditSavedItem post);
}
