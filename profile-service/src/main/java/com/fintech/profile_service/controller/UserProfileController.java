package com.fintech.profile_service.controller;

import com.fintech.profile_service.dto.ProfileRequest;
import com.fintech.profile_service.entity.UserProfile;
import com.fintech.profile_service.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileRepository userProfileRepository;

    @PostMapping
    public ResponseEntity<UserProfile> saveProfile(@RequestBody ProfileRequest request){
        //kiem tra  email da ton tai
        Optional<UserProfile> existingProfile  = userProfileRepository.findByEmail(request.getEmail());
        UserProfile profile;
        if (existingProfile.isPresent()){
            //neu co roi thi cap nhat
            profile = existingProfile.get();
        }else {
            //tao mmoi
            profile = new UserProfile();
            profile.setEmail(request.getEmail());
        }

        //cap nhat thong tin
        profile.setFullName(request.getFullName());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setAddress(request.getAddress());

        UserProfile saveProfile = userProfileRepository.save(profile);
        return ResponseEntity.ok(saveProfile);
    }


    @GetMapping("/{email}")
    public ResponseEntity<?> getProfileByEmail(@PathVariable String email){
        Optional<UserProfile> profile = userProfileRepository.findByEmail(email);

        if (profile.isPresent()){
            return ResponseEntity.ok(profile.get());
        }else {
            return ResponseEntity.status(404).body("Khong tim thay ho so cho email:");
        }
    }
}
