package com.fintech.wallet_service.service;

import com.fintech.wallet_service.entity.Wallet;
import com.fintech.wallet_service.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    public Wallet createWallet(String email){
        if (walletRepository.findByEmail(email).isPresent()){
            throw new RuntimeException("Ví  đã tồn tại cho email: " +email);
        }
        Wallet newWallet = Wallet.builder()
                .email(email)
                .balance(BigDecimal.ZERO)
                .build();
        return walletRepository.save(newWallet);
    }

    public Wallet getWalletInfo(String email){
        return walletRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("Không tìm thấy ví cho email: "+ email));
    }
}
