package com.example.redditvault.client;

import com.example.redditvault.redditPost.RedditPost;
import com.example.redditvault.redditPost.RedditPostRepository;
import com.example.redditvault.subreddit.Subreddit;
import com.example.redditvault.subreddit.SubredditRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class RedditClientService {
    private final RedditProperties redditProperties;
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper;
    private final WebClient webClient = WebClient.builder()
            .build();
    private final RedditTokenRepository redditTokenRepository;
    private final SubredditRepository subredditRepository;
    private final RedditPostRepository redditPostRepository;
    @Autowired
    public RedditClientService(RedditProperties redditProperties, ObjectMapper objectMapper,
                               RedditTokenRepository redditTokenRepository, SubredditRepository subredditRepository,
                               RedditPostRepository redditPostRepository) {
        this.redditProperties = redditProperties;
        this.objectMapper = objectMapper;
        this.redditTokenRepository = redditTokenRepository;
        this.subredditRepository = subredditRepository;
        this.redditPostRepository = redditPostRepository;
    }

    public ResponseEntity<String> getAuthUrl(){
        //TODO: generate a random state and then check if a request of auth is valid
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
            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed to get token: " + response.body());
            }

            // Parse response JSON
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.body());

            String accessToken = jsonNode.get("access_token").asText();
            String refreshToken = jsonNode.has("refresh_token") ? jsonNode.get("refresh_token").asText() : null;
            int expiresIn = jsonNode.get("expires_in").asInt();

            // Optional: fetch username with access token
            String redditUsername = fetchUsername(accessToken);

            //TODO: modify logic, token should be unique for user, rn is causing error in DB
            //TODO: create a logic to refresh token if the user is still sending requests
            // Store to DB
            RedditToken token = new RedditToken();
            token.setAccessToken(accessToken);
            token.setRefreshToken(refreshToken);
            token.setExpiresAt(Instant.now().plusSeconds(expiresIn));
            token.setRedditUsername(redditUsername);

            redditTokenRepository.save(token);

            return redditUsername;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to exchange code for token: " + e.getMessage();
        }
    }
    private String fetchUsername(String accessToken) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://oauth.reddit.com/api/v1/me"))
                .header("Authorization", "bearer " + accessToken)
                .header("User-Agent", "myapp/0.0.1")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode jsonNode = new ObjectMapper().readTree(response.body());
        return jsonNode.get("name").asText(); // "name" is the Reddit username
    }

    public String getAccessToken(String redditUsername) {
        return redditTokenRepository.findByRedditUsername(redditUsername)
                .map(RedditToken::getAccessToken)
                .orElseThrow(() -> new RuntimeException("User not authorized"));
    }

    public String getUserInfo(String username) {
        String accessToken = getAccessToken(username);
        System.out.println("Access token for " + username + ": " + accessToken);
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
    public List<RedditPost> getUserSaved(String username)throws Exception {
        String accessToken = getAccessToken(username);
        System.out.println("Access token for " + username + ": " + accessToken);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(String.format("https://oauth.reddit.com/user/%s/saved", username)))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("User-Agent", "java:springboot.reddit.oauth:v1.0 (by /u/your_reddit_username)")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.body());
            RedditResponse redditResponse;
            redditResponse = objectMapper.readValue(response.body(), RedditResponse.class);
            List<RedditChildren> redditChildrenn = redditResponse.getData().getChildren();
            List<RedditPost> posts = new ArrayList<>();
            for (RedditChildren redditChildren : redditChildrenn) {
                //Optional<Subreddit> subredditOptional = subredditRepository.
                //if (postOptional.isPresent()) {
                    //throw new IllegalStateException("Post author already exists");
                //}
                subredditRepository.save(redditChildren.getRedditSavedItem().getSubreddit());
                String url;
                if (redditChildren.getRedditSavedItem().getSecure_media() != null &&
                        redditChildren.getRedditSavedItem().getSecure_media().getReddit_video() != null){
                    url = redditChildren.getRedditSavedItem().getSecure_media().getReddit_video().getFallback_url();
                }else {
                    url = redditChildren.getRedditSavedItem().getUrl();
                }
                RedditPost post = new RedditPost(
                        redditChildren.getRedditSavedItem().getId(),
                        redditChildren.getRedditSavedItem().getAuthor(),
                        redditChildren.getRedditSavedItem().getTitle(),
                        url
                );
                posts.add(post);
                redditPostRepository.save(post);
            }

            return posts;

    }

    public List<DownloadRequest> scrapeMediaFromPost(String accessToken,String redditPostUrl) {
        List<DownloadRequest> mediaItems = new ArrayList<>();
        String jsonUrl = redditPostUrl + ".json";

        String json = null;
        int attempt = 0;
        try {
                json = webClient.get()
                        .uri(jsonUrl)
                        .header("Authorization", "Bearer " + accessToken)
                        .header("User-Agent", "Mozilla/5.0")

                        //.doOnSuccess(clientResponse -> System.out.println("clientResponse.statusCode() = " + clientResponse.statusCode()))
                        .retrieve()
                        .onStatus(
                                status -> status.value() == 429,
                                response -> {
                                    System.err.println("429 Too Many Requests: " + jsonUrl);
                                    System.err.println(response.headers().toString());
                                    // Retry after a delay
                                    return Mono.delay(Duration.ofSeconds(2)) // delay 2 seconds
                                            .flatMap(aLong -> Mono.error(new RuntimeException("Rate limit reached, retrying...")));
                                }
                        )
                        .bodyToMono(String.class)
                        .delaySubscription(Duration.ofSeconds(1)) //Just add this before the repeat
                        .block(); // blocking because scrape must finish before download

            } catch (Exception e) {
                System.err.println("Retrying after error: " + e.getMessage());
            }

        // Process the retrieved JSON if the request was successful
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
            System.err.println("Error parsing Reddit post JSON: " + e.getMessage());
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
    public void download(String urlStr, String file) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
            throw new FileNotFoundException("404 Not Found: " + urlStr);
        } else if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to download: HTTP " + responseCode + " for " + urlStr);
        }

        try (BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
             FileOutputStream fis = new FileOutputStream(file)) {

            byte[] buffer = new byte[1024];
            int count;
            while ((count = bis.read(buffer)) != -1) {
                fis.write(buffer, 0, count);
            }
        } finally {
            connection.disconnect();
        }
    }
}
