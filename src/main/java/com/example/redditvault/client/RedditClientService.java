package com.example.redditvault.client;

import com.example.redditvault.redditPost.RedditPost;
import com.example.redditvault.redditPost.RedditPostRepository;
import com.example.redditvault.redditPost.RedditPostService;
import com.example.redditvault.subreddit.Subreddit;
import com.example.redditvault.subreddit.SubredditRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RedditClientService {
    private final RedditProperties redditProperties;
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper;
    private final WebClient webClient = WebClient.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
            .build();
    private final RedditTokenRepository redditTokenRepository;
    private final SubredditRepository subredditRepository;
    private final RedditPostRepository redditPostRepository;
    private final JobStatusRepository jobStatusRepository;

    @Autowired
    public RedditClientService(RedditProperties redditProperties, ObjectMapper objectMapper,
                               RedditTokenRepository redditTokenRepository, SubredditRepository subredditRepository,
                               RedditPostRepository redditPostRepository, JobStatusRepository jobStatusRepository) {
        this.redditProperties = redditProperties;
        this.objectMapper = objectMapper;
        this.redditTokenRepository = redditTokenRepository;
        this.subredditRepository = subredditRepository;
        this.redditPostRepository = redditPostRepository;
        this.jobStatusRepository = jobStatusRepository;
    }

    public ResponseEntity<String> getAuthUrl() {
        //TODO: generate a random state and then check if a request of auth is valid
        String state = "prova";
        String url = String.format(
                redditProperties.getUserAuthUrl(state)
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    public String exchangeCodeForToken(String code, String state) {
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
            token.setRedditVaultToken("redditVaultToken");
            redditTokenRepository.save(token);

            return redditUsername;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to exchange code for token: " + e.getMessage();
        }
    }

    public String fetchUsername(String accessToken) throws IOException, InterruptedException {
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

    public SavedPageResponse fetchUserSaved(String username, String after) throws Exception {
        String accessToken = getAccessToken(username);
        String url = "https://oauth.reddit.com/user/" + username + "/saved?limit=25";
        if (after != null) {
            url += "&after=" + after;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Authorization", "Bearer " + accessToken)
                .header("User-Agent", "java:springboot.reddit.oauth:v1.0 (by /u/your_reddit_username)")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        RedditResponse redditResponse = objectMapper.readValue(response.body(), RedditResponse.class);

        List<RedditChildren> children = redditResponse.getData().getChildren();
        List<RedditPost> posts = new ArrayList<>();

        if (children != null) {
            for (RedditChildren redditChildren : children) {
                RedditSavedItem item = redditChildren.getRedditSavedItem();
                String subredditName = item.getSubreddit().getName();

                try {
                    subredditRepository.save(new Subreddit(subredditName));
                } catch (DataIntegrityViolationException ignored) {
                }

                String urlToSave = (item.getSecure_media() != null && item.getSecure_media().getReddit_video() != null)
                        ? item.getSecure_media().getReddit_video().getFallback_url()
                        : item.getUrl();

                RedditPost post = new RedditPost(
                        item.getId(),
                        item.getAuthor(),
                        item.getTitle(),
                        urlToSave,
                        subredditName,
                        username
                );

                posts.add(post);
                redditPostRepository.save(post);
            }
        }

        return new SavedPageResponse(redditResponse.getData().getAfter(), posts);
    }

    public Flux<RedditPost> fetchAllUserSaved(User user) throws Exception {
            String username = user.getUsername();
            String accessToken = getAccessToken(username); // your existing method (blocking)
            // If getAccessToken is blocking, wrap it:
            // String accessToken = Mono.fromCallable(() -> getAccessToken(username))
            //                          .subscribeOn(Schedulers.boundedElastic()).block();

            return fetchPage(username, accessToken, null)
                    .expand(resp -> {
                        String after = resp.getData().getAfter();
                        if (after == null) return Mono.empty();
                        return fetchPage(username, accessToken, after);
                    })
                    .flatMap(resp -> {
                        List<RedditChildren> children = resp.getData().getChildren();
                        return Flux.fromIterable(children)
                                .flatMap(rc -> {
                                    RedditSavedItem item = rc.getRedditSavedItem();
                                    String subredditName = item.getSubreddit().getName();

                                    // Save subreddit (blocking JPA) safely
                                    Mono<Void> saveSubreddit = Mono.fromRunnable(() -> {
                                                try {
                                                    subredditRepository.save(new Subreddit(subredditName));
                                                } catch (DataIntegrityViolationException ignored) {}
                                            })
                                            .subscribeOn(Schedulers.boundedElastic())
                                            .then();

                                    String urlToSave =
                                            (item.getSecure_media() != null &&
                                                    item.getSecure_media().getReddit_video() != null)
                                                    ? item.getSecure_media().getReddit_video().getFallback_url()
                                                    : item.getUrl();
                                    // add here
                                    scrapeMediaFromPost(accessToken, item.getId());
                                    RedditPost post = new RedditPost(
                                            item.getId(),
                                            item.getAuthor(),
                                            item.getTitle(),
                                            urlToSave,
                                            subredditName,
                                            username
                                    );

                                    Mono<RedditPost> savePost =
                                            Mono.fromCallable(() -> redditPostRepository.save(post))
                                                    .subscribeOn(Schedulers.boundedElastic());

                                    return saveSubreddit.then(savePost);
                                });
                    });
        }

        private Mono<RedditResponse> fetchPage(String username, String accessToken, String after) {
            String base = "https://oauth.reddit.com/user/" + username + "/saved?limit=25";
            String url = (after == null) ? base : base + "&after=" + after;

            return webClient.get()
                    .uri(url)
                    .headers(h -> {
                        h.setBearerAuth(accessToken);
                        h.add("User-Agent", "Mozilla/5.0");
                    })
                    .retrieve()
                    .onStatus(status -> status.value() == 429,
                            (ClientResponse resp) -> Mono.defer(() -> {
                                // Try to respect Retry-After if Reddit provides it
                                String ra = resp.headers().asHttpHeaders().getFirst("Retry-After");
                                long delay = 2;
                                try { if (ra != null) delay = Long.parseLong(ra); } catch (NumberFormatException ignored) {}
                                return Mono.error(new RateLimitException("429 Too Many Requests, retrying in " + delay + "s", delay));
                            })
                    )
                    .bodyToMono(String.class)
                    .map(json -> {
                        try {
                            return objectMapper.readValue(json, RedditResponse.class);
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to parse Reddit response", e);
                        }
                    })
                    .delayElement(Duration.ofSeconds(1)) // gentle pacing between hits
                    .retryWhen(
                            reactor.util.retry.Retry.max(3)
                                    .filter(ex -> ex instanceof RateLimitException)
                                    .transientErrors(true)
                                    .doBeforeRetry(rs -> {
                                        if (rs.failure() instanceof RateLimitException rle) {
                                            // delay per exception info
                                            try { Thread.sleep(rle.delaySeconds() * 1000L); } catch (InterruptedException ignored) {}
                                        }
                                    })
                    );
        }

        // Minimal custom exception to carry delay seconds
        static class RateLimitException extends RuntimeException {
            private final long delaySeconds;
            RateLimitException(String msg, long delaySeconds) {
                super(msg);
                this.delaySeconds = delaySeconds;
            }
            long delaySeconds() { return delaySeconds; }
        }

    @Async
    public void startFetchJob(String jobId, User user) throws Exception {
        AtomicInteger counter = new AtomicInteger(0);

        // Mark job as RUNNING once at the start
        updateStatusAsync(jobId, "RUNNING", 0, null).subscribe();

        fetchAllUserSaved(user) // <-- your existing Flux<RedditPost> that also persists posts
                // Progress update every N items (here: 10)
                .doOnNext(post -> {
                    int c = counter.incrementAndGet();
                    if (c % 10 == 0) {
                        updateProgressAsync(jobId, c).subscribe(); // fire-and-forget, scheduled on elastic
                    }
                })
                // If anything fails, store FAILED + final count + error message
                .doOnError(err -> {
                    int soFar = counter.get();
                    updateStatusAsync(jobId, "FAILED", soFar, err.getMessage()).subscribe();
                })
                // On success, store COMPLETED + final exact count
                .doOnComplete(() -> {
                    int finalCount = counter.get();
                    updateStatusAsync(jobId, "COMPLETED", finalCount, null).subscribe();
                })
                .subscribe(); // trigger the pipeline (since we're in an @Async void method)
    }
    // Update only the fetchedCount (used for periodic progress)
    private Mono<Void> updateProgressAsync(String jobId, int count) {
        return Mono.fromRunnable(() -> {
                    JobStatus js = jobStatusRepository.findById(jobId).orElseThrow();
                    js.setFetchedCount(count);
                    jobStatusRepository.save(js);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    // Update status (+ optional count/message)
    private Mono<Void> updateStatusAsync(String jobId, String status, Integer count, String message) {
        return Mono.fromRunnable(() -> {
                    JobStatus js = jobStatusRepository.findById(jobId).orElseThrow();
                    js.setStatus(status);
                    if (count != null) js.setFetchedCount(count);
                    if (message != null) js.setMessage(message);
                    jobStatusRepository.save(js);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }



    public List<Optional<RedditPost>> getUserSaved(String username, String after) throws Exception {
        List<Optional<RedditPost>> postOptional = redditPostRepository.findPostsByRedditUsername(username);
        if (postOptional.isEmpty()) {
            throw new IllegalStateException("The user didn't save any posts");
        }
        return postOptional;
    }


    public List<DownloadRequest> scrapeMediaFromPost(String accessToken, String redditPostUrl) {
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
