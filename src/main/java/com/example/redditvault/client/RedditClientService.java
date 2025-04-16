package com.example.redditvault.client;

import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@Service
public class RedditClientService {

    public String getMe(){
        String token = "YOUR_ACCESS_TOKEN"; // Replace this with your real token

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); // Automatically formats "Bearer <token>"
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = "https://oauth.reddit.com/api/v1/me";

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        return  response.getBody();
    }
}
