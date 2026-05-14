package com.repurpose.profile_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileRequest {
    private String email;
    private String fullName;
    private String bio;
    private String website;
    private String preferredLanguage;
    private String timezone;
}
