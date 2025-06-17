package com.example.redditvault.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(path = "api/v1/redditclient")
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

     @GetMapping(path = "/oauth/callback")
    public ResponseEntity<Void>  oauthCallback(@RequestParam("code") String code, @RequestParam("state") String state) {
         String redditUsername;

         try {
             redditUsername =redditClientService.exchangeCodeForToken(code, state); // returns username
         } catch (Exception e) {
             // In case of error, redirect with an error message
             URI errorRedirect = URI.create("http://localhost:5173/?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
             return ResponseEntity.status(HttpStatus.FOUND).location(errorRedirect).build();
         }

         // Redirect with the username
         URI redirectUri = URI.create("http://localhost:5173/?username=" + URLEncoder.encode(redditUsername, StandardCharsets.UTF_8));
         return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();

     }

    @GetMapping("/info")
    public Map<String, Object> userInfo(OAuth2AuthenticationToken authentication) {
        // Return the user's attributes as a map
        return authentication.getPrincipal().getAttributes();
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/me")
    public ResponseEntity<String> getUserInfo(@RequestBody User user) {
        String username = user.getUsername();
        try {
            String userJson = redditClientService.getUserInfo(username);
            return ResponseEntity.ok(userJson);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/saved")
    public RedditResponse getUserSaved(@RequestBody User user)throws Exception {
        String username = user.getUsername();
        return redditClientService.getUserSaved(username);
        //return ResponseEntity.ok(userJson);
    }
    @PostMapping("/download")
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

    @PostMapping("/scrape")
    public String scrapeAndDownload(@RequestHeader("Authorization") String bearerToken,@RequestBody List<Post> posts) throws FileNotFoundException {
        List<DownloadRequest> allMediaItems = new ArrayList<>();

        for (Post post : posts) {
            List<DownloadRequest> mediaItems = redditClientService.scrapeMediaFromPost(bearerToken, post.getUrl());
            allMediaItems.addAll(mediaItems);
        }

        CompletableFuture.allOf(
                allMediaItems.stream()
                        .map(item -> CompletableFuture.runAsync(() -> {
                            try {
                                redditClientService.download(item.getUrl(), item.getFilename().split("\\?")[0]);
                            } catch (FileNotFoundException e) {
                                System.err.println("Skipped (not found): " + item.getUrl());
                            }  catch (IOException e) {
                                e.printStackTrace();
                            }
                        }))
                        .toArray(CompletableFuture[]::new)
        ).join();

        return "Scraped and downloaded " + allMediaItems.size() + " items.";
    }
}
