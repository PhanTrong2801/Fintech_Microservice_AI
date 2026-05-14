package com.repurpose.profile_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Liên kết với identity-service
    @Column(unique = true, nullable = false)
    private String email;

    private String fullName;
    private String avatarUrl;

    // ===== NEW: Content Creator fields =====
    private String bio;
    private String website;

    @Column(length = 10)
    @Builder.Default
    private String preferredLanguage = "en"; // en, vi, etc.

    @Column(length = 50)
    private String timezone;
}
