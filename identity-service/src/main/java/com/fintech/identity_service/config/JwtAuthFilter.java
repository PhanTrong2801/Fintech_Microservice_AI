package com.fintech.identity_service.config;

import com.fintech.identity_service.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. Lấy chuỗi Token từ Header của HTTP Request
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. Kiểm tra xem Header có chứa Token chuẩn (bắt đầu bằng "Bearer ") không
        if (authHeader ==null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);// Không có thì cho đi tiếp (đến SecurityConfig chặn sau)
            return;
        }

        // 3. Cắt bỏ chữ "Bearer " (7 ký tự) để lấy đúng chuỗi Token
        jwt = authHeader.substring(7);

        try {

            // 4. Kiểm tra Token có hợp lệ không
            if (jwtService.isTokenValid(jwt)){
                // Lấy email từ token
                userEmail = jwtService.extractEmail(jwt);

                // 5. Nếu hợp lệ, cấp quyền cho người dùng này trong hệ thống Spring Security
                if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userEmail, null,new ArrayList<>()
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }catch (Exception e){
            System.out.println("Token không hợp lệ hoặc đã hết hạn!");
        }

        filterChain.doFilter(request,response);
    }
}
