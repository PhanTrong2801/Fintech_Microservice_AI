package com.repurpose.identity_service.controller;

import com.repurpose.identity_service.entity.SubscriptionPlan;
import com.repurpose.identity_service.entity.User;
import com.repurpose.identity_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final AuthService authService;

    /**
     * Lấy thông tin quota của user hiện tại
     */
    @GetMapping("/me/quota")
    public ResponseEntity<Map<String, Object>> getQuota(@RequestParam String email) {
        User user = authService.getUserByEmail(email);
        return ResponseEntity.ok(Map.of(
                "email", user.getEmail(),
                "plan", user.getSubscriptionPlan().name(),
                "monthlyQuota", user.getMonthlyQuota(),
                "usedQuota", user.getUsedQuota(),
                "remainingQuota", user.getMonthlyQuota() - user.getUsedQuota(),
                "quotaResetDate", user.getQuotaResetDate() != null ? user.getQuotaResetDate().toString() : "N/A"
        ));
    }

    /**
     * Nâng cấp subscription plan
     */
    @PostMapping("/me/upgrade")
    public ResponseEntity<User> upgradePlan(
            @RequestParam String email,
            @RequestParam SubscriptionPlan plan) {
        return ResponseEntity.ok(authService.upgradePlan(email, plan));
    }
    /**
     * Tiêu thụ 1 lượt quota
     */
    @PostMapping("/me/consume-quota")
    public ResponseEntity<Boolean> consumeQuota(@RequestParam String email) {
        return ResponseEntity.ok(authService.consumeQuota(email));
    }
}
