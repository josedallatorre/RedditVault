package com.example.redditvault.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

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
    @GetMapping("/saved")
    public RedditResponse getUserSaved(@RequestHeader("Authorization") String bearerToken)throws Exception {
        String token = bearerToken.replace("Bearer ", "").trim();
        return redditClientService.getUserSaved(token);
        //return ResponseEntity.ok(userJson);
    }
    @PostMapping("download")
    public void downloadRedditMedia(@RequestBody String urlStr, String file) throws IOException {
        URL url = new URL(urlStr);
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count=0;
        while((count = bis.read(buffer,0,1024)) != -1)
        {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
    }

}
