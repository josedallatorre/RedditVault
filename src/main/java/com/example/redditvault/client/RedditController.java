package com.example.redditvault.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class RedditController {
    private final RedditClientService redditClientService;

    @Autowired
    public RedditController(RedditClientService redditClientService) {
        this.redditClientService = redditClientService;
    }

     @GetMapping(path = "api/v1/me")
    public ResponseEntity<String> getRedditClient(Model model) {
        return redditClientService.getMe();
     }

     @GetMapping(path = "/auth")
    public ResponseEntity<Void> getAuth(Model model) {
         URI redditAuthUri = URI.create(RedditClientService.getAuth());
         HttpHeaders headers = new HttpHeaders();
         headers.setLocation(redditAuthUri);
         return new ResponseEntity<>(headers, HttpStatus.FOUND);
     }
}
