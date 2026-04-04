package com.fintech.profile_service.dto;


import lombok.Data;

@Data
public class ProfileRequest {

    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
}
