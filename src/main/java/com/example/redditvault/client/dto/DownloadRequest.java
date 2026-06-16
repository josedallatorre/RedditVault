package com.example.redditvault.client.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DownloadRequest {
    private final String filename;
    private final String url;

    public DownloadRequest(String url, String filename) {
        this.url = url;
        this.filename = filename;
    }

}
