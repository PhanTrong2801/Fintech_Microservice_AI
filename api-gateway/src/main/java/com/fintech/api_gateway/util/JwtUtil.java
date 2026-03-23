package com.fintech.api_gateway.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {
    public static final String SECRECT = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    public void validateToken(final  String token){
        Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token);
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRECT);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
