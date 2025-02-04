package com.project.MplTournament.service;

import com.project.MplTournament.ExcpetionHandler.ExpiredTokenException;
import com.project.MplTournament.ExcpetionHandler.InvalidTokenException;
import com.project.MplTournament.ExcpetionHandler.UsernameMismatchException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {


    private String secretkey = "";

    public JwtService() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGen.generateKey();
            secretkey = Base64.getEncoder().encodeToString(sk.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateToken(String userName, String userRole, int userId) {
        //Map<String, Objects> claims = new HashMap<>();
        return Jwts.builder()
                .claim("role", userRole)
                .claim("userId",userId)
                .subject(userName)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretkey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token) {
        if (isTokenExpired(token)) {
            throw new ExpiredTokenException("JWT token has expired");
        }
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);

        if (userName == null || userName.isEmpty()) {
            throw new InvalidTokenException("Token is invalid or expired");
        }

        if (!userName.equals(userDetails.getUsername())) {
            throw new UsernameMismatchException("Username in token doesn't match with user details");
        }

        if (isTokenExpired(token)) {
            throw new ExpiredTokenException("JWT token has expired");
        }

        return true;
    }


    private boolean isTokenExpired(String token) {
        Date expirationDate = extractExpiration(token);
        if (expirationDate.before(new Date())) {
            throw new ExpiredTokenException("JWT token has expired");
        }
        return false;  // Token is not expired
    }

    private Date extractExpiration(String token) {
        try {
            return extractClaim(token, Claims::getExpiration);
        } catch (Exception e) {
            throw new ExpiredTokenException("JWT token has expired");
        }
    }
}
