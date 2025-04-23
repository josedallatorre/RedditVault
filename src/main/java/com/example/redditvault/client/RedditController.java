package com.example.redditvault.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


     @GetMapping(path = "/auth")
    public ResponseEntity<Void> getAuth() {
         String url = redditClientService.getAuthUrl();
         HttpHeaders headers = new HttpHeaders();
         headers.setLocation(URI.create(url));
         return new ResponseEntity<>(headers, HttpStatus.FOUND);
     }
}
