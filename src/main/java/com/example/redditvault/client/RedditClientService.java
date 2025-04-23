package com.example.redditvault.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class RedditClientService {
    private final RedditProperties redditProperties;
    private final HttpClient client = HttpClient.newHttpClient();

    public RedditClientService(RedditProperties redditProperties) {
        this.redditProperties = redditProperties;
    }

    public String getMe(){
//        RestTemplate restTemplate = new RestTemplate();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(token); // Automatically formats "Bearer <token>"
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//
//        String url = "https://oauth.reddit.com/api/v1/me";
//
//        ResponseEntity<String> response = restTemplate.exchange(
//                url,
//                HttpMethod.GET,
//                entity,
//                String.class
//        );
//
//        return  response.getBody();
        //RedditProperties r = new RedditProperties("1","ciao",true);
        //return r.getCode("ciao");
        return "";
    }
    public ResponseEntity<String> getAuthUrl(){
        String state = "prova";
        String url = String.format(
                redditProperties.getUserAuthUrl(state)
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    public String exchangeCodeForToken(String code, String state){
        try {
            String credentials = redditProperties.getClientId() + ":" + redditProperties.getClientSecret();
            String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

            String formData = "grant_type=authorization_code" +
                    "&code=" + code +
                    "&redirect_uri=" + redditProperties.getRedirectUri();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(RedditProperties.OAUTH_TOKEN_URL))
                    .header("Authorization", "Basic " + encoded)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formData))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body(); // You can return token JSON or extract it

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to exchange code for token: " + e.getMessage();
        }
    }
    public String getUserInfo(String accessToken) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(RedditProperties.ME_URL))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("User-Agent", "java:springboot.reddit.oauth:v1.0 (by /u/your_reddit_username)")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to fetch user info: " + e.getMessage();
        }
    }

}
