package com.example.redditvault.client;

import com.example.redditvault.redditPost.RedditPost;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.WebAsyncTask;
import reactor.core.publisher.Flux;

import java.awt.print.Pageable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(path = "api/v1/redditclient")
public class RedditController {
    private final RedditClientService redditClientService;
    private final JobStatusRepository jobStatusRepository;

    @Autowired
    public RedditController(RedditClientService redditClientService, JobStatusRepository jobStatusRepository) {
        this.redditClientService = redditClientService;
        this.jobStatusRepository = jobStatusRepository;
    }

     @GetMapping(path = "/auth")
    public ResponseEntity<String> getAuth() {
         return redditClientService.getAuthUrl();
     }

     @GetMapping(path = "/oauth/callback")
    public ResponseEntity<Void>  oauthCallback(@RequestParam("code") String code, @RequestParam("state") String state,
                                               HttpServletResponse response) {
         String redditUsername;

         try {
             redditUsername =redditClientService.exchangeCodeForToken(code, state); // returns username
         } catch (Exception e) {
             // In case of error, redirect with an error message
             URI errorRedirect = URI.create("http://localhost:5173/?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
             return ResponseEntity.status(HttpStatus.FOUND).location(errorRedirect).build();
         }
         ResponseCookie cookie = ResponseCookie.from("authToken", redditUsername)
                 .httpOnly(true)
                 .secure(false) // set to true if using HTTPS
                 .sameSite("Lax")
                 .path("/")
                 .maxAge(Duration.ofHours(1))
                 .build();

         response.addHeader("Set-Cookie", cookie.toString());

         // Redirect with the username
         URI redirectUri = URI.create("http://localhost:5173/?username=" + URLEncoder.encode(redditUsername, StandardCharsets.UTF_8));
         return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();

     }

    @GetMapping("/info")
    public Map<String, Object> userInfo(OAuth2AuthenticationToken authentication) {
        // Return the user's attributes as a map
        return authentication.getPrincipal().getAttributes();
    }

    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @GetMapping("/me")
    public ResponseEntity<String> getUserInfo(@CookieValue(name = "authToken", required = false) String authToken) {
        System.out.println(authToken);
        if (authToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing auth token");
        }
        try {
            String userJson = redditClientService.getUserInfo(authToken);
            return ResponseEntity.ok(userJson);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @PostMapping("/all-saved")
    public ResponseEntity<Map<String, String>> fetchAllUserSaved(@RequestBody User user) throws Exception {
        String jobId = UUID.randomUUID().toString();

        JobStatus job = new JobStatus();
        job.setJobId(jobId);
        job.setUsername(user.getUsername());
        job.setStatus("PENDING");
        jobStatusRepository.save(job);

        // async call
        redditClientService.startFetchJob(jobId, user);

        return ResponseEntity.ok(Map.of("jobId", jobId));
    }

    @GetMapping("/status/{jobId}")
    public ResponseEntity<JobStatus> getStatus(@PathVariable String jobId) {
        return jobStatusRepository.findById(jobId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @PostMapping("/saved")
    public ResponseEntity<List<Optional<RedditPost>>> getUserSaved(@RequestBody SavedRequest request)throws Exception {
        String username = request.getUsername();
        String after = request.getAfter();
        List<Optional<RedditPost>> posts = redditClientService.getUserSaved(username, after);
        return ResponseEntity.ok(posts);

    }
    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @PostMapping("/status")
    public ResponseEntity<SavedPageResponse> fetchUserSaved(@RequestBody SavedRequest request)throws Exception {
        String username = request.getUsername();
        String after = request.getAfter();
        SavedPageResponse page = redditClientService.fetchUserSaved(username, after);
        return ResponseEntity.ok(page);

    }
    @PostMapping("/download")
    public void downloadRedditMedia(@RequestBody List<DownloadRequest> requests) throws IOException {
        CompletableFuture.allOf(
                requests.stream()
                        .map(request -> CompletableFuture.runAsync(() -> {
                            try {
                                redditClientService.download(request.getUrl(), request.getFilename());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }))
                        .toArray(CompletableFuture[]::new)
        ).join();
    }

    @PostMapping("/scrape")
    public String scrapeAndDownload(@RequestHeader("Authorization") String bearerToken,@RequestBody List<Post> posts) throws FileNotFoundException {
        List<DownloadRequest> allMediaItems = new ArrayList<>();

        for (Post post : posts) {
            List<DownloadRequest> mediaItems = redditClientService.scrapeMediaFromPost(bearerToken, post.getUrl());
            allMediaItems.addAll(mediaItems);
        }

        CompletableFuture.allOf(
                allMediaItems.stream()
                        .map(item -> CompletableFuture.runAsync(() -> {
                            try {
                                redditClientService.download(item.getUrl(), item.getFilename().split("\\?")[0]);
                            } catch (FileNotFoundException e) {
                                System.err.println("Skipped (not found): " + item.getUrl());
                            }  catch (IOException e) {
                                e.printStackTrace();
                            }
                        }))
                        .toArray(CompletableFuture[]::new)
        ).join();

        return "Scraped and downloaded " + allMediaItems.size() + " items.";
    }
}
