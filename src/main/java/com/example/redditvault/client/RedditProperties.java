package com.example.redditvault.client;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@ConfigurationProperties(prefix = "reddit")
public class RedditProperties {
    //public static final boolean production = (SystemProperty.Environment.Value.Production == SystemProperty.environment.value());
    public static final boolean production = false;
    public static final String OAUTH_API_DOMAIN = "https://oauth.reddit.com";

    // Step 1. Send user to auth URL
    public static final String OAUTH_AUTH_URL = "https://ssl.reddit.com/api/v1/authorize";
    // https://ssl.reddit.com/api/v1/authorize?client_id=CLIENT_ID&response_type=TYPE&state=RANDOM_STRING&redirect_uri=URI&duration=DURATION&scope=SCOPE_STRING

    // Step 2. Reddit sends user to REDIRECT_URI
    private String redirectUri;

    // Step 3. Get token
    public static final String OAUTH_TOKEN_URL = "https://ssl.reddit.com/api/v1/access_token";

    public static final String ME_URL = "https://oauth.reddit.com/api/v1/me";

    // I think it is easier to create 2 reddit apps (one with 127.0.0.1 redirect URI)
    private String clientId;
    private String clientSecret;
    public static final String SCOPE_ID = "identity";

    // Field name in responses
    public static final String ACCESS_TOKEN_NAME = "access_token";
    public static final String REFRESH_TOKEN_NAME = "refresh_token";

    //public static final String SCOPES = "identity, edit, flair, history, modconfig, modflair, modlog, modposts, modwiki, mysubreddits, privatemessages, read, report, save, submit, subscribe, vote, wikiedit, wikiread";
    public static final String SCOPES = "identity,history";
    public static boolean permanentAccess = true;

    private String bearerToken;

    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();
    /*
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
     */


    public String getUserAuthUrl(String state) {
        String duration = permanentAccess ? "permanent" : "temporary";
        String url = OAUTH_AUTH_URL + "?client_id=" + clientId + "&response_type=code&state=" + state
                + "&redirect_uri=" + redirectUri + "&duration=" + duration + "&scope=" + SCOPES;

        // scopes: modposts, identity, edit, flair, history, modconfig, modflair, modlog, modposts, modwiki,
        // mysubreddits, privatemessages, read, report, save, submit, subscribe, vote, wikiedit, wikiread, etc.
        return url;
    }

    public ResponseEntity<String> getCode(String state) {
        String url = getUserAuthUrl(state);
        HttpHeaders headers = new HttpHeaders();
        //headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(response.getBody());
    }

    public String getBearerToken() {
        return bearerToken;
    }

    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
