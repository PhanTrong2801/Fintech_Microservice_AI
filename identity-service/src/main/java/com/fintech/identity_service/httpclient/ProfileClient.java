package com.fintech.identity_service.httpclient;

import com.fintech.identity_service.dto.ProfileCreationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

//Service client để giao tiếp với profile-service
@FeignClient(name = "profile-service", url = "http://profile-service:8082")
public interface ProfileClient {

    // Mô phỏng lại chính xác API tạo profile bên Profile Service
    @PostMapping("/profiles")
    ResponseEntity<Object> createProfile(@RequestBody ProfileCreationRequest request);
}
