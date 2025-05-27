package com.example.redditvault.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RedditData {
    private String after;
    private int dist;
    private String modhash;
    private String geo_filter;

    @JsonProperty("children")
    private List<RedditChildren> children;

    // Getters and setters
    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    public int getDist() {
        return dist;
    }

    public void setDist(int dist) {
        this.dist = dist;
    }

    public String getModhash() {
        return modhash;
    }

    public void setModhash(String modhash) {
        this.modhash = modhash;
    }

    public String getGeo_filter() {
        return geo_filter;
    }

    public void setGeo_filter(String geo_filter) {
        this.geo_filter = geo_filter;
    }

    public List<RedditChildren> getChildren() {
        return children;
    }

    public void setChildren(List<RedditChildren> children) {
        this.children = children;
    }
}

