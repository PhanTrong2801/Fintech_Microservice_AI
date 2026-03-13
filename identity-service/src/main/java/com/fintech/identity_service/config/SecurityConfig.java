package com.fintech.identity_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // Công cụ dùng để mã hóa mật khẩu
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // Cấu hình phân quyền các API
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable) //tat CSRF vi dung JWT
                .authorizeHttpRequests(auth-> auth
                        .requestMatchers("auth/**").permitAll()// Cho phép tất cả mọi người truy cập các API bắt đầu bằng /auth
                        .anyRequest().authenticated()// Các API khác bắt buộc phải có Token mới được vào
                );
        return http.build();
    }
}
