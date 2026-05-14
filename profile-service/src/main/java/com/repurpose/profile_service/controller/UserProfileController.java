package com.repurpose.profile_service.controller;

import com.repurpose.profile_service.dto.ProfileRequest;
import com.repurpose.profile_service.entity.UserProfile;
import com.repurpose.profile_service.repository.UserProfileRepository;
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
        // Kiểm tra email đã tồn tại
        Optional<UserProfile> existingProfile = userProfileRepository.findByEmail(request.getEmail());
        UserProfile profile;
        if (existingProfile.isPresent()){
            // Nếu có rồi thì cập nhật
            profile = existingProfile.get();
        } else {
            // Tạo mới
            profile = new UserProfile();
            profile.setEmail(request.getEmail());
        }

        // Cập nhật thông tin
        profile.setFullName(request.getFullName());
        profile.setBio(request.getBio());
        profile.setWebsite(request.getWebsite());
        profile.setPreferredLanguage(request.getPreferredLanguage());
        profile.setTimezone(request.getTimezone());

        UserProfile savedProfile = userProfileRepository.save(profile);
        return ResponseEntity.ok(savedProfile);
    }

    @GetMapping("/{email}")
    public ResponseEntity<?> getProfileByEmail(@PathVariable String email){
        Optional<UserProfile> profile = userProfileRepository.findByEmail(email);

        if (profile.isPresent()){
            return ResponseEntity.ok(profile.get());
        } else {
            return ResponseEntity.status(404).body("Không tìm thấy hồ sơ cho email: " + email);
        }
    }
}
