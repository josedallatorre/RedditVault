package com.example.redditvault.client;

import org.springframework.stereotype.Service;

@Service
public class RedditClientService {
    private final RedditProperties redditProperties;

    public RedditClientService(RedditProperties redditProperties) {
        this.redditProperties = redditProperties;
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
    public String getAuthUrl(){
        String state = "prova";
        String url = String.format(
                redditProperties.getUserAuthUrl(state)
                /*"https://ssl.reddit.com/api/v1/authorize?client_id=%s&response_type=code&state=randomstate&redirect_uri=%s&duration=temporary&scope=read",
                redditProperties.getClientId(),
                redditProperties.getRedirectUri()*/
        );
        return url;
    }
}
