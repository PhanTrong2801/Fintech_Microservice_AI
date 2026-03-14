package com.fintech.identity_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/my-profile")
    public ResponseEntity<String> getMyProfile(){
        // Lấy thông tin email từ SecurityContext (được set bởi JwtAuthFilter)
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok("Xin chào! Đây là thông tin mật của tài khoản" +currentEmail);
    }
}
