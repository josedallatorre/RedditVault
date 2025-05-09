package com.example.redditvault.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
