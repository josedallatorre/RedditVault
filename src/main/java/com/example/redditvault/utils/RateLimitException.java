package com.example.redditvault.utils;

import java.io.IOException;

public class RateLimitException extends IOException {
    private final long delaySeconds;

    public RateLimitException(String message, long delaySeconds) {
        super(message);
        this.delaySeconds = delaySeconds;
    }

    public long delaySeconds() {
        return delaySeconds;
    }
}