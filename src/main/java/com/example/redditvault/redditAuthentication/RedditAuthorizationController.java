package com.example.redditvault.redditAuthentication;

import com.example.redditvault.client.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@RestController
@RequestMapping(path = "api/v1/reddit")
public class RedditAuthorizationController {


    private final RedditAuthorizationService redditAuthorizationService;

    public RedditAuthorizationController(RedditAuthorizationService redditAuthorizationService) {
        this.redditAuthorizationService = redditAuthorizationService;
    }

    @GetMapping(path = "/auth")
    public ResponseEntity<String> getAuth(@RequestHeader("Authorization") String authHeader) {
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing auth token");
        }
        String jwt = authHeader.substring(7);
        return redditAuthorizationService.getAuthUrl(jwt);
    }

    @GetMapping(path = "/oauth/callback")
    //TODO: remove auth header, here we receive a callback from reddit
    public ResponseEntity<String>  oauthCallback(@RequestParam("code") String code, @RequestParam("state") String state,
                                                 HttpServletResponse response) {
        String redditUsername;
        try {
            //TODO: better logic, too many things in one function
            redditUsername =redditAuthorizationService.exchangeCodeForToken(code, state); // returns username
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

    @PostMapping(path = "/refresh-token")
    public ResponseEntity<String> refreshRedditToken(@RequestHeader("Authorization") String authHeader,
                                                     @RequestBody User user){
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing auth token");
        }
        String jwt = authHeader.substring(7);
        if(user.getUsername() == null || user.getUsername().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid reddit token");
        }
        try {
            String userJson = redditAuthorizationService.refreshRedditToken(user, jwt);
            return ResponseEntity.ok(userJson);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}
