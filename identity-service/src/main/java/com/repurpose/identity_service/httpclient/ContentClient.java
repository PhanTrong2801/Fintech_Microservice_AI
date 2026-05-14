package com.repurpose.identity_service.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign Client để giao tiếp với content-service (thay thế WalletClient cũ)
 * Khi user đăng ký → tạo workspace rỗng trong Content Service
 */
@FeignClient(name = "content-service")
public interface ContentClient {

    @PostMapping("/content/workspace/create")
    ResponseEntity<Object> createWorkspace(@RequestParam("email") String email);
}
