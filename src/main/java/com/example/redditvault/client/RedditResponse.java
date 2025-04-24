package com.example.redditvault.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RedditResponse {
    private String kind;

    @JsonProperty("data")
    private RedditData data;

    // Getters and setters
    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public RedditData getData() {
        return data;
    }

    public void setData(RedditData data) {
        this.data = data;
    }
}
