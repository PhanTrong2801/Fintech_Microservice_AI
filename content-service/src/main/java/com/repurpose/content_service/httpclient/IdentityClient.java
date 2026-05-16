package com.repurpose.content_service.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "identity-service")
public interface IdentityClient {

    @PostMapping("/users/me/consume-quota")
    ResponseEntity<Boolean> consumeQuota(
            @RequestParam("email") String email,
            @RequestHeader("Authorization") String token);
}
