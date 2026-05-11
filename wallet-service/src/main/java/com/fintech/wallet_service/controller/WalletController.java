package com.fintech.wallet_service.controller;

import com.fintech.wallet_service.dto.TransferRequest;
import com.fintech.wallet_service.entity.Transaction;
import com.fintech.wallet_service.entity.Wallet;
import com.fintech.wallet_service.repository.TransactionRepository;
import com.fintech.wallet_service.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final TransactionRepository transactionRepository;

    @PostMapping("/create")
    public ResponseEntity<Wallet> createWallet(@RequestParam String email){
        return ResponseEntity.ok(walletService.createWallet(email));
    }

    @GetMapping("/{email}")
    public ResponseEntity<Wallet> getWalletInfo(@PathVariable String email){
        return ResponseEntity.ok(walletService.getWalletInfo(email));
    }

    @PostMapping("/deposit")
    public ResponseEntity<Wallet> deposit(@RequestParam String email, @RequestParam BigDecimal amount, @RequestParam(required = false, defaultValue = "Nạp tiền vào ví") String description) {
        return ResponseEntity.ok(walletService.deposit(email, amount, description));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Wallet> withdraw(@RequestParam String email, @RequestParam BigDecimal amount, @RequestParam(required = false, defaultValue = "Rút tiền từ ví") String description) {
        return ResponseEntity.ok(walletService.withdraw(email, amount, description));
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequest request) {
        walletService.transfer(request.getFromEmail(), request.getToEmail(), request.getAmount(), request.getDescription());
        return ResponseEntity.ok("Chuyển tiền thành công");
    }

    @GetMapping("/{email}/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable String email) {
        return ResponseEntity.ok(transactionRepository.findByWalletEmailOrderByTimestampDesc(email));
    }
}


