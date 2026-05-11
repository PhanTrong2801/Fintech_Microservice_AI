package com.fintech.identity_service.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "wallet-service")
public interface WalletClient {

    @PostMapping("/wallets/create")
    ResponseEntity<Object> createWallet(@RequestParam("email") String email);
}
