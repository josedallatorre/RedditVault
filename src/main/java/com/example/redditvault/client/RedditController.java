package com.example.redditvault.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@RestController
public class RedditController {
    private final RedditClientService redditClientService;

    @Autowired
    public RedditController(RedditClientService redditClientService) {
        this.redditClientService = redditClientService;
    }

     @GetMapping(path = "/auth")
    public ResponseEntity<String> getAuth() {
         return redditClientService.getAuthUrl();
     }

     @GetMapping(path = "oauth/callback")
    public String oauthCallback(@RequestParam("code") String code, @RequestParam("state") String state) {
         return redditClientService.exchangeCodeForToken(code, state);
     }

    @GetMapping("/me")
    public ResponseEntity<String> getUserInfo(@RequestHeader("Authorization") String bearerToken) {
        try {
            String token = bearerToken.replace("Bearer ", "").trim();
            String userJson = redditClientService.getUserInfo(token);
            return ResponseEntity.ok(userJson);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    @PostMapping("/saved")
    public RedditResponse getUserSaved(@RequestHeader("Authorization") String bearerToken, @RequestParam("username") String username)throws Exception {
        String token = bearerToken.replace("Bearer ", "").trim();
        return redditClientService.getUserSaved(token, username);
        //return ResponseEntity.ok(userJson);
    }
    @PostMapping("download")
    public void downloadRedditMedia(@RequestBody List<DownloadRequest> requests) throws IOException {
        CompletableFuture.allOf(
                requests.stream()
                        .map(request -> CompletableFuture.runAsync(() -> {
                            try {
                                redditClientService.download(request.getUrl(), request.getFilename());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }))
                        .toArray(CompletableFuture[]::new)
        ).join();
    }

    @GetMapping("/scrape")
    public String scrapeAndDownload(@RequestParam String postUrl) throws IOException {
        List<DownloadRequest> mediaItems = redditClientService.scrapeMediaFromPost(postUrl);

        for (DownloadRequest item : mediaItems) {
            redditClientService.download(item.getUrl(), item.getFilename());
        }

        return "Scraped and downloaded " + mediaItems.size() + " items.";
    }
}
