package com.fintech.wallet_service.controller;

import com.fintech.wallet_service.entity.Wallet;
import com.fintech.wallet_service.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/create")
    public ResponseEntity<Wallet> createWallet(@RequestParam String email){
        return ResponseEntity.ok(walletService.createWallet(email));
    }

    @GetMapping("/{email}")
    public ResponseEntity<Wallet> getWalletInfo(@PathVariable String email){
        return ResponseEntity.ok(walletService.getWalletInfo(email));
    }
}


