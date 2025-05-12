package com.example.redditvault.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class RedditClientService {
    private final RedditProperties redditProperties;
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper;
    private final WebClient webClient = WebClient.builder()

            .build();

    public RedditClientService(RedditProperties redditProperties, ObjectMapper objectMapper) {
        this.redditProperties = redditProperties;
        this.objectMapper = objectMapper;
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
    public RedditResponse getUserSaved(String accessToken, String username)throws Exception {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(String.format("https://oauth.reddit.com/user/%s/saved", username)))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("User-Agent", "java:springboot.reddit.oauth:v1.0 (by /u/your_reddit_username)")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.body());
            return objectMapper.readValue(response.body(), RedditResponse.class);

    }

    public List<DownloadRequest> scrapeMediaFromPost(String redditPostUrl) {
        List<DownloadRequest> mediaItems = new ArrayList<>();

        String jsonUrl = redditPostUrl + ".json";

        String json = webClient.get()
                .uri(jsonUrl)
                .header("User-Agent", "Mozilla/5.0")
                .retrieve()
                .bodyToMono(String.class)
                .block(); // blocking because scrape must finish before download

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            JsonNode postData = root.get(0).get("data").get("children").get(0).get("data");

            String url = postData.get("url").asText();
            boolean isVideo = postData.get("is_video").asBoolean();

            if (isImage(url)) {
                String filename = generateFilename(url);
                mediaItems.add(new DownloadRequest(url, filename));
            } else if (isVideo) {
                JsonNode media = postData.get("media");
                if (media != null && media.get("reddit_video") != null) {
                    String videoUrl = media.get("reddit_video").get("fallback_url").asText();
                    String filename = generateFilename(videoUrl);
                    mediaItems.add(new DownloadRequest(videoUrl, filename));
                }
            }
        } catch (Exception e) {
            System.err.println("Error scraping Reddit post: " + e.getMessage());
        }

        return mediaItems;
    }

    private boolean isImage(String url) {
        return url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png") || url.endsWith(".gif");
    }

    private String generateFilename(String url) {
        String extension = url.substring(url.lastIndexOf("."));
        return UUID.randomUUID().toString() + extension;
    }
    public void download(String urlStr, String file)throws IOException {
        URL url = new URL(urlStr);
        try (BufferedInputStream bis = new BufferedInputStream(url.openStream());
             FileOutputStream fis = new FileOutputStream(file)) {

            byte[] buffer = new byte[1024];
            int count;
            while ((count = bis.read(buffer)) != -1) {
                fis.write(buffer, 0, count);
            }
        }
    }
}
