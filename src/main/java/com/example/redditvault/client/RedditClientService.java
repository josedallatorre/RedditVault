package com.example.redditvault.client;

import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@Service
public class RedditClientService {

    public ResponseEntity<String> getMe(){
        String token = "eyJhbGciOiJSUzI1NiIsImtpZCI6IlNIQTI1NjpzS3dsMnlsV0VtMjVmcXhwTU40cWY4MXE2OWFFdWFyMnpLMUdhVGxjdWNZIiwidHlwIjoiSldUIn0.eyJzdWIiOiJ1c2VyIiwiZXhwIjoxNzQ0ODkxNzk1LjkwMTA5MSwiaWF0IjoxNzQ0ODA1Mzk1LjkwMTA5LCJqdGkiOiJUYW9IQ2NfUDZOZDlZNnYtQjhGTlN2NGs1a1lzakEiLCJjaWQiOiJ1MGFSZnlzZkROa0J0QlA3TFVpZWdRIiwibGlkIjoidDJfY3djNTFlOGgiLCJhaWQiOiJ0Ml9jd2M1MWU4aCIsImxjYSI6MTYyNDQ3NzI1ODAwMCwic2NwIjoiZUp4RWpVRU9nMEFJUmVfeTE5eW82V0pHMEpJNllnQnR2SDB6YXRNZGZCN19QZkRSdHdwcmdoQmxGOUNadUJRR29SbjNEWVFiMlMwNzBvN1lxZ3V6WnZUSHJjYmdXdnRwZGQxTFNwT0lNa2xjSllNdG8wNGdfSHRubTY1aHRjZ2JHLWVpZmxLcmVkZjlndGhxT18zS3NxVG1BY0pMSTgwUFBMOEJBQURfXzhIRlF4YyIsInJjaWQiOiJOcU0zYU82dS1aSWg2WnctdUtUdVpsdVI0V09OelBTSUxzeXlCTWlhVmU4IiwiZmxvIjo4fQ.FZntnySTL9nr5D5bRXM6NU34P6YlJo_mm83fanHbyZ9KvkmeGOh19gPdEvgnxzZg3XY2mNCymoXUfN01skPNd9wleOmpHpgsp_WhIW9OoDzlg9Swxqis9Rj1gMPx-8YBXfLw5hTSUKTTkAEh-ltCtun5VoZtaUtdMUeZy7RKt5kZStiLH4uC6NEJ50nOJ6M0Y4npnMzSgjSs3LveXXNlA6ilgDd0icJiUKp_7Hy9MFSmlsuDJiJVY4ntXk70Npc7vI5XFTtsJzt-2_sSGdmS6WKUyXf3te6zqYVUYts_X801xpHnHOeFuLRyvM0nT36qIK5EBmEctydATURhhsuUHw"; // Replace this with your real token

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
        RedditOAuthClient r = new RedditOAuthClient("1","ciao",true);
        return r.getCode("ciao");
    }
    public static String getAuth(){
        //RedditOAuthClient r = new RedditOAuthClient("1","ciao",true);
        return RedditOAuthClient.getUserAuthUrl("ciao");
    }
}
