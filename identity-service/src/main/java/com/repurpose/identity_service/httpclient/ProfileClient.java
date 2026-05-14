package com.repurpose.identity_service.httpclient;

import com.repurpose.identity_service.dto.ProfileCreationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign Client để giao tiếp với profile-service (giữ nguyên pattern cũ)
 */
@FeignClient(name = "profile-service")
public interface ProfileClient {

    @PostMapping("/profiles")
    ResponseEntity<Object> createProfile(@RequestBody ProfileCreationRequest request);
}
