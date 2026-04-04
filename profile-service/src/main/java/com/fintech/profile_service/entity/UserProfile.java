package com.fintech.profile_service.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //lien ket voi identity
    @Column(unique = true, nullable = false)
    private String email;

    private String fullName;
    private String phoneNumber;
    private String address;
    private String avatarUrl;
}
