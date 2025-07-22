package com.example.redditvault;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static String SECRET_KEY = "UVplVkcrcDhRRkQ4M2E4WGZnbXZuU25SUjRWak9pYzc0RHVOYkY4K3VDTWdXYnM3bjNRU3dDeGYvSVNJY2dnK25CNWVFSjFnNnkyT1Y4RWY4WHZISC9SUU5PQmYrOXpBc3R2bFpFaWQxTnZrSFlGeXFBNFhpT01qdktOYmtHd29WYlQ1Q3Izck1mbUdXZjBxeHptRFEvWnFwSnRBcW9XcW16Ym9NeU9CcWJ1bkVhUE9seUp1Nk1XUnRGNmFSUEE2dHdzUmM1aVkxRzBpcUpXNTJXTHlWbGRsVEJyaTBjU1FXdmFkWmhDd3lrYUtEdGM3ZWtoNVE3TEJ3Sm9IQUpYZTBpTHVzUEpGWDRkaTF2dDk3VkxBTW5KZVUvSWVUdUloWGZGQXNaV0RFV3ZBaTVLcjI4MS84bDFKVjkrZmZjNXBFeTFHWHd0YjBPb1BvZjhia2xHbmtpQjFlVkFjYlVqNkMvRHpuQ1pVVDFJPQ==";
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() +100 + 60*60*1000))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte keyBytes[] = Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
