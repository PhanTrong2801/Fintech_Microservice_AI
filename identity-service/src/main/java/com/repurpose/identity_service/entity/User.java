package com.repurpose.identity_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(name = "is_active")
    private boolean isActive;

    // ===== NEW: Subscription & Quota fields =====

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private SubscriptionPlan subscriptionPlan = SubscriptionPlan.FREE;

    @Column(nullable = false)
    @Builder.Default
    private Integer monthlyQuota = 5; // Free tier = 5 repurposes/tháng

    @Column(nullable = false)
    @Builder.Default
    private Integer usedQuota = 0;

    @Column
    private LocalDate quotaResetDate;
}
