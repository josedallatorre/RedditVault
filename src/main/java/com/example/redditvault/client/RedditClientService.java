package com.example.redditvault.client;

import com.example.redditvault.JwtService;
import com.example.redditvault.client.dto.*;
import com.example.redditvault.redditAccount.RedditAccountRepository;
import com.example.redditvault.redditAccount.RedditAccountService;
import com.example.redditvault.redditAuthentication.RedditStateCodeRepository;
import com.example.redditvault.redditAuthentication.RedditToken;
import com.example.redditvault.redditAuthentication.RedditTokenRepository;
import com.example.redditvault.redditPost.RedditPost;
import com.example.redditvault.redditPost.RedditPostRepository;
import com.example.redditvault.subreddit.Subreddit;
import com.example.redditvault.subreddit.SubredditService;
import com.example.redditvault.web.UserTestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import com.example.redditvault.utils.RateLimitException;

@Service
public class RedditClientService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper;
    private final WebClient webClient = WebClient.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
            .build();
    private final RedditTokenRepository redditTokenRepository;
    private final RedditPostRepository redditPostRepository;
    private final JobStatusRepository jobStatusRepository;
    private final SubredditService subredditService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private static final Logger jsonLogger = LoggerFactory.getLogger("JSON_LOGGER");

    @Autowired
    public RedditClientService(RedditProperties redditProperties, ObjectMapper objectMapper,
                               RedditTokenRepository redditTokenRepository, SubredditService subredditService,
                               RedditPostRepository redditPostRepository, JobStatusRepository jobStatusRepository,
                               RedditAccountService redditAccountService, RedditAccountRepository redditAccountRepository,
                               JwtService jwtService, UserDetailsService userDetailsService,
                               RedditStateCodeRepository redditStateCodeRepository, UserTestRepository userTestRepository) {
        this.objectMapper = objectMapper;
        this.redditTokenRepository = redditTokenRepository;
        this.subredditService = subredditService;
        this.redditPostRepository = redditPostRepository;
        this.jobStatusRepository = jobStatusRepository;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    public String getAccessToken(String redditUsername) {
        return redditTokenRepository.findByRedditUsername(redditUsername)
                .map(RedditToken::getAccessToken)
                .orElseThrow(() -> new RuntimeException("User not authorized"));
    }



    private String getAccessTokenfromJWT(String jwt, String redditUsername) {
        // TODO: control that redditUsername is in list of redditAccount of the User
        final String userEmail = jwtService.extractUsername(jwt);
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
        String username = userDetails.getUsername();
        System.out.println(username);
        System.out.println(userDetails);
        RedditToken token = redditTokenRepository.findByRedditUsername(redditUsername)
                .orElseThrow(() -> new RuntimeException("User not authorized"));
        return token.getAccessToken();
    }

    public String getUserInfo(String jwt, String username) {
        String accessToken = getAccessTokenfromJWT(jwt, username);
        System.out.println("Access token for " + jwt + ": " + accessToken);
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
                String subRedditId = item.getSubreddit().getSubredditId();
                Subreddit subReddit = new Subreddit(subRedditId);
                try {
                    subredditService.addNewSubreddit(subReddit);
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
                        subReddit,
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
                                String subredditId = item.getSubreddit().getSubredditId();
                                Subreddit subReddit = new Subreddit(subredditId);

                                // Save subreddit (blocking JPA) safely
                                Mono<Void> saveSubreddit = Mono.fromRunnable(() -> {
                                            try {
                                                subredditService.addNewSubreddit(subReddit);
                                            } catch (DataIntegrityViolationException ignored) {}
                                        })
                                        .subscribeOn(Schedulers.boundedElastic())
                                        .then();

                                String urlToSave =
                                        (item.getSecure_media() != null &&
                                                item.getSecure_media().getReddit_video() != null)
                                                ? item.getSecure_media().getReddit_video().getFallback_url()
                                                : item.getUrl();

                                RedditPost post = new RedditPost(
                                        item.getId(),
                                        item.getAuthor(),
                                        item.getTitle(),
                                        urlToSave,
                                        subReddit,
                                        username
                                );

                                Mono<RedditPost> savePost =
                                        Mono.fromCallable(() -> redditPostRepository.save(post))
                                                .subscribeOn(Schedulers.boundedElastic());

                                // Scrape media after save
                                Mono<Void> scrapePost = scrapeMediaFromPost(item)
                                        .onErrorResume(e -> {
                                            jsonLogger.info("Scrape failed for post " + item.getId() + ": " + e);
                                            return Mono.empty();
                                        });

                                return saveSubreddit.then(savePost)
                                        .flatMap(saved -> scrapePost.thenReturn(saved))
                                        .onErrorResume(e -> {
                                            jsonLogger.info("Failed to process post " + item.getId() + ": " + e);
                                            return Mono.empty();
                                        });
                            }, 1); // max 3 concurrent — tune this down if 429s persist
                     });
    }

    private Mono<RedditResponse> fetchPage(String username, String accessToken, String after) {
        String base = "https://oauth.reddit.com/user/" + username + "/saved?limit=25";
        String url = (after == null) ? base : base + "&after=" + after;

        return webClient.get()
                .uri(url)
                .headers(h -> {
                    h.setBearerAuth(accessToken);
                    h.add("User-Agent", "linux:test:v1.0 (by /u/33prova33)");
                })
                .retrieve()
                .onStatus(status -> status.value() == 429,
                        (ClientResponse resp) -> Mono.defer(() -> {
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
                        jsonLogger.info(String.valueOf(e));
                        throw new RuntimeException("Failed to parse Reddit response", e);
                    }
                })
                .delayElement(Duration.ofSeconds(2)) // gentle pacing between hits
                .retryWhen(
                        Retry.max(5)
                                .filter(ex -> ex instanceof RateLimitException)
                                .transientErrors(true)
                                .doBeforeRetryAsync(rs -> {
                                    long delay = (rs.failure() instanceof RateLimitException rle) ? rle.delaySeconds() : 2;
                                    return Mono.delay(Duration.ofSeconds(delay)).then();
                                })
                );
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


    public Mono<Void> scrapeMediaFromPost(RedditSavedItem post) {
        if (post.getUrl() == null || post.getUrl().isEmpty()) return Mono.empty();
        jsonLogger.info(post.getPermalink(), post.getUrl_overridden_by_dest(), post.getTitle());

        MediaExtractorRegistry registry = new MediaExtractorRegistry();
        List<String> mediaUrls = registry.extract(post);
        DownloaderRegistry downloaderRegistry = new DownloaderRegistry();
        //TODO: choose the filename then pass it to the downloader
        //TODO: check if posts has been already downloaded
        if (!mediaUrls.isEmpty()) {
            return Flux.fromIterable(mediaUrls)
                    .flatMap(mediaUrl ->
                                    Mono.fromRunnable(() -> {
                                                try {
                                                    downloaderRegistry.downloadAll(post, List.of(mediaUrl));
                                                } catch (IOException e) {
                                                    throw new RuntimeException(e); // preserves cause, incl. RateLimitException
                                                }
                                            })
                                            .subscribeOn(Schedulers.boundedElastic())
                                            .retryWhen(
                                                    Retry.max(5)
                                                            .filter(ex -> ex.getCause() instanceof RateLimitException)
                                                            .doBeforeRetryAsync(rs -> {
                                                                long delay = (rs.failure().getCause() instanceof RateLimitException rle)
                                                                        ? rle.delaySeconds() : 2;
                                                                return Mono.delay(Duration.ofSeconds(delay)).then();
                                                            })
                                            )
                                            .onErrorResume(e -> {
                                                jsonLogger.info("Giving up on post id " + post.getId() + "media url" + mediaUrl + " after retries: " + e );
                                                return Mono.empty();
                                            })
                            , 1) // one download at a time
                    .delayElements(Duration.ofMillis(500))
                    .then();
        }
        return Mono.empty();
    }


    // this is not utilized at the moment, but could be useful in the future
    private boolean isImage(String url) {
        return url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png") || url.endsWith(".gif");
    }
}
