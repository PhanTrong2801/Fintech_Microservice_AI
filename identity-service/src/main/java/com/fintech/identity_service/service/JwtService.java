package com.fintech.identity_service.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoder;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    // Lấy chuỗi bí mật từ file application.yml
    @Value("${jwt.secret}")
    private String secretKey;

    // Lấy thời gian sống của token từ application.yml
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Hàm này dùng để tạo ra chuỗi JWT dựa vào Email của người dùng
     */
    public String generateToken(String email){
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }
    /**
     * Hàm phụ trợ để chuyển chuỗi secret thành SecretKey chuẩn của thư viện JJWT
     */
    private SecretKey getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Hàm trích xuất Email (subject) từ chuỗi Token
     */
    public String extractEmail(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Hàm kiểm tra Token có hợp lệ và chưa hết hạn không
     */
    public boolean isTokenValid(String token){
        try{
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
