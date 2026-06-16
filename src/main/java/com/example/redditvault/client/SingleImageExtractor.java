package com.example.redditvault.client;

import com.example.redditvault.client.dto.RedditSavedItem;

import java.util.ArrayList;
import java.util.List;

public class SingleImageExtractor implements MediaExtractor {
    @Override
    public boolean supports(RedditSavedItem post) {
        return post.getIsRedditMediaDomain() && post.getUrl_overridden_by_dest() != null;
    }

    @Override
    public List<String> extractMediaUrls(RedditSavedItem post) {
        List<String> urls = new ArrayList<>();
        String url = post.getUrl_overridden_by_dest();
        int i = url.indexOf("?source=fallback");
        if (i != -1) {
            url = url.substring(0, i);
        }
        urls.add(url);
        return urls;
    }
}
