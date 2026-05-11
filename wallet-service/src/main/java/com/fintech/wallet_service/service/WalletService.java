package com.fintech.wallet_service.service;

import com.fintech.wallet_service.entity.Transaction;
import com.fintech.wallet_service.entity.TransactionType;
import com.fintech.wallet_service.entity.Wallet;
import com.fintech.wallet_service.repository.TransactionRepository;
import com.fintech.wallet_service.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

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

    @Transactional
    public Wallet deposit(String email, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền nạp phải lớn hơn 0");
        }

        Wallet wallet = getWalletInfo(email);
        wallet.setBalance(wallet.getBalance().add(amount));
        Wallet updatedWallet = walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .amount(amount)
                .type(TransactionType.DEPOSIT)
                .description(description)
                .build();
        transactionRepository.save(transaction);

        return updatedWallet;
    }

    @Transactional
    public Wallet withdraw(String email, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền rút phải lớn hơn 0");
        }

        Wallet wallet = getWalletInfo(email);
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Số dư không đủ để thực hiện giao dịch");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        Wallet updatedWallet = walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .amount(amount)
                .type(TransactionType.WITHDRAW)
                .description(description)
                .build();
        transactionRepository.save(transaction);

        return updatedWallet;
    }

    @Transactional
    public void transfer(String fromEmail, String toEmail, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền chuyển phải lớn hơn 0");
        }
        if (fromEmail.equals(toEmail)) {
            throw new IllegalArgumentException("Không thể chuyển tiền cho chính mình");
        }

        // Rút tiền từ ví người gửi
        withdraw(fromEmail, amount, "Chuyển tiền cho " + toEmail + ": " + description);
        
        // Nạp tiền vào ví người nhận
        deposit(toEmail, amount, "Nhận tiền từ " + fromEmail + ": " + description);
    }
}
