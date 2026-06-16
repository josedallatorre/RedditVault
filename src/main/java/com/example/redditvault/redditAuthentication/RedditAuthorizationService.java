package com.example.redditvault.redditAuthentication;

import com.example.redditvault.JwtService;
import com.example.redditvault.client.User;
import com.example.redditvault.client.dto.RedditProperties;
import com.example.redditvault.redditAccount.RedditAccount;
import com.example.redditvault.redditAccount.RedditAccountRepository;
import com.example.redditvault.redditAccount.RedditAccountService;
import com.example.redditvault.web.UserTest;
import com.example.redditvault.web.UserTestRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class RedditAuthorizationService {
    private final RedditStateCodeRepository redditStateCodeRepository;
    private final JwtService jwtService;
    private final RedditProperties redditProperties;
    private final RedditAccountRepository redditAccountRepository;
    private final RedditAccountService redditAccountService;
    private final RedditTokenRepository redditTokenRepository;
    private final HttpClient client = HttpClient.newHttpClient();
    private final UserDetailsService userDetailsService;
    private final UserTestRepository userTestRepository;

    public RedditAuthorizationService(RedditStateCodeRepository redditStateCodeRepository, JwtService jwtService,
                                      RedditProperties redditProperties, RedditAccountRepository redditAccountRepository,
                                      RedditAccountService redditAccountService, RedditTokenRepository redditTokenRepository,
                                      UserDetailsService userDetailsService, UserTestRepository userTestRepository) {
        this.redditStateCodeRepository = redditStateCodeRepository;
        this.jwtService = jwtService;
        this.redditProperties = redditProperties;
        this.redditAccountRepository = redditAccountRepository;
        this.redditAccountService = redditAccountService;
        this.redditTokenRepository = redditTokenRepository;
        this.userDetailsService = userDetailsService;
        this.userTestRepository = userTestRepository;
    }

    public ResponseEntity<String> getAuthUrl(String jwt) {
        //TODO: generate a random state and then check if a request of auth is valid
        String state = UUID.randomUUID().toString();
        final String userEmail = jwtService.extractUsername(jwt);
        UserTest user = (UserTest) this.userDetailsService.loadUserByUsername(userEmail);
        RedditStateCode redditStateCode = new RedditStateCode(state, user.getId());
        redditStateCodeRepository.save(redditStateCode);
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
            Integer userId = redditStateCodeRepository.findById(state)
                    .map(RedditStateCode::getUserId)
                    .orElseThrow(() -> new RuntimeException("Invalid state"));
            UserTest user = userTestRepository.findById(userId)
                    .orElseThrow(()->new RuntimeException("Invalid User"));
            RedditToken token = new RedditToken();
            Optional<RedditAccount> optionalRedditAccount = redditAccountRepository.findByRedditUsername(redditUsername);
            if (!optionalRedditAccount.isPresent()){
                RedditAccount redditAccount = new RedditAccount(redditUsername, user);
                redditAccountService.addNewRedditAccount(redditAccount);
                token.setRedditAccount(redditAccount);
            }else {
                token.setRedditAccount(optionalRedditAccount.get());
            }
            token.setAccessToken(accessToken);
            token.setRefreshToken(refreshToken);
            token.setExpiresAt(Instant.now().plusSeconds(expiresIn));
            token.setRedditUsername(redditUsername);
            redditTokenRepository.save(token);

            return redditUsername;
        } catch (Exception e) {
            throw new RuntimeException("OAuth exchange failed", e);
        }
    }

    public String refreshRedditToken(User user, String jwt) {
        String refreshToken = getRefreshToken(user.getUsername());
        try {
            String credentials = redditProperties.getClientId() + ":" + redditProperties.getClientSecret();
            String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

            String formData = "grant_type=refresh_token" +
                    "&refresh_token=" + refreshToken;

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
            String newRefreshToken = jsonNode.has("refresh_token") ? jsonNode.get("refresh_token").asText() : null;
            int expiresIn = jsonNode.get("expires_in").asInt();


            // final String userEmail = jwtService.extractUsername(jwt);
            //UserTest userTest = (UserTest) this.userDetailsService.loadUserByUsername(userEmail);
            //String username = user.getUsername();

            // Optional: fetch username with access token
            String redditUsername = fetchUsername(accessToken);
            Optional<RedditAccount> redditAccount = redditAccountRepository.findByRedditUsername(redditUsername);

            Optional<RedditToken> oldToken = redditTokenRepository.findByRedditUsername(redditUsername);
            oldToken.ifPresent(redditToken -> redditTokenRepository.deleteById(redditToken.getId()));
            RedditToken token = new RedditToken();
            token.setAccessToken(accessToken);
            token.setRefreshToken(newRefreshToken);
            token.setExpiresAt(Instant.now().plusSeconds(expiresIn));
            token.setRedditUsername(redditUsername);
            redditAccount.ifPresent(token::setRedditAccount);
            redditTokenRepository.save(token);

            return redditUsername;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to exchange code for token: " + e.getMessage();
        }
    }
    public String getRefreshToken(String redditUsername) {
        return redditTokenRepository.findByRedditUsername(redditUsername)
                .map(RedditToken::getRefreshToken)
                .orElseThrow(() -> new RuntimeException("User not authorized"));
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

}
