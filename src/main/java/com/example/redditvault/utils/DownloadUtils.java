package com.example.redditvault.utils;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class DownloadUtils {
    public static String generateFilename(String url) {
        String extension = url.substring(url.lastIndexOf("."));
        return UUID.randomUUID().toString() + extension;
    }

    public static void download(String urlStr, String file) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        // Add a proper User-Agent (otherwise Reddit often rejects with 400/403)
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; RedditDownloader/1.0)");

        int responseCode = connection.getResponseCode();
        // TODO: handle 404 adding a flag in post (database)
        if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
            throw new FileNotFoundException("404 Not Found: " + urlStr);
        } else if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to download: HTTP " + responseCode + " for " + urlStr);
        }

        try (BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
             FileOutputStream fos = new FileOutputStream(file)) {

            byte[] buffer = new byte[8192]; // bigger buffer for faster downloads
            int count;
            while ((count = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, count);
            }
        } finally {
            connection.disconnect();
        }
    }

}
