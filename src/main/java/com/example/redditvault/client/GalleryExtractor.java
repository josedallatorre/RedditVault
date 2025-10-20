package com.example.redditvault.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GalleryExtractor implements MediaExtractor {
    @Override
    public boolean supports(RedditSavedItem post) {
        return post.getIsGallery();
    }

    @Override
    public List<String> extractMediaUrls(RedditSavedItem post) {
        List<String> urls = new ArrayList<>();
        Map<String, MediaMetadata> mediaMetadata = post.getMedia_metadata();
        if (mediaMetadata != null) {
            for (MediaMetadata md : mediaMetadata.values()) {
                if(md.getSource()!=null && md.getSource().getUrl()!=null) {
                    String url = md.getSource().getUrl();
                    urls.add(url);
                }
            }
        }
        return urls;
    }
}
