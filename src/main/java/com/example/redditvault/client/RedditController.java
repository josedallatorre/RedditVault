package com.example.redditvault.client;

import com.example.redditvault.JwtService;
import com.example.redditvault.client.dto.DownloadRequest;
import com.example.redditvault.client.dto.SavedPageResponse;
import com.example.redditvault.client.dto.SavedRequest;
import com.example.redditvault.redditAccount.RedditAccount;
import com.example.redditvault.redditPost.RedditPost;
import com.example.redditvault.utils.DownloadUtils;
import com.example.redditvault.web.UserTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(path = "api/v1/redditclient")
public class RedditController {
    private final RedditClientService redditClientService;
    private final JobStatusRepository jobStatusRepository;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;


    @Autowired
    public RedditController(RedditClientService redditClientService, JobStatusRepository jobStatusRepository,
                            JwtService jwtService, UserDetailsService userDetailsService) {
        this.redditClientService = redditClientService;
        this.jobStatusRepository = jobStatusRepository;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }


    @GetMapping("/info")
    public Map<String, Object> userInfo(OAuth2AuthenticationToken authentication) {
        // Return the user's attributes as a map
        return authentication.getPrincipal().getAttributes();
    }

    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @GetMapping("/me/{redditUsername}")
    public ResponseEntity<String> getUserInfo(@RequestHeader("Authorization") String authHeader, @PathVariable String redditUsername) {
        // TODO: extract email from jwt then pass it to service to get Reddit username and than
        // we can get other infos
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing auth token");
        }
        String jwt = authHeader.substring(7);
        try {
            String userJson = redditClientService.getUserInfo(jwt, redditUsername);
            return ResponseEntity.ok(userJson);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @GetMapping("/me")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String authHeader) {
        // TODO: extract email from jwt then pass it to service to get Reddit username and than
        // we can get other infos
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing auth token");
        }
        String jwt = authHeader.substring(7);
        final String userEmail = jwtService.extractUsername(jwt);
        UserTest user = (UserTest) this.userDetailsService.loadUserByUsername(userEmail);
        List<User> redditUsers = new ArrayList<User>();
        Set<RedditAccount> userRedditAccounts = user.getRedditAccounts();
        for(RedditAccount account: userRedditAccounts){
            User redditUser = new User(account.getRedditUsername(), account.getId());
            redditUsers.add(redditUser);
        }
        return ResponseEntity.ok(redditUsers);
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

    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @PostMapping("/all-media")
    public ResponseEntity<Map<String, String>> fetchAllUserSavedMedia(@RequestBody User user) throws Exception {
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
                                DownloadUtils.download(request.getUrl(), request.getFilename());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }))
                        .toArray(CompletableFuture[]::new)
        ).join();
    }

    /*
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
     */
}
