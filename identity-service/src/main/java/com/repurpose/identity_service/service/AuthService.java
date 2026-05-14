package com.repurpose.identity_service.service;

import com.repurpose.identity_service.dto.AuthRequest;
import com.repurpose.identity_service.dto.AuthResponse;
import com.repurpose.identity_service.dto.ProfileCreationRequest;
import com.repurpose.identity_service.entity.SubscriptionPlan;
import com.repurpose.identity_service.entity.User;
import com.repurpose.identity_service.httpclient.ContentClient;
import com.repurpose.identity_service.httpclient.ProfileClient;
import com.repurpose.identity_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ProfileClient profileClient;
    private final ContentClient contentClient; // Thay WalletClient

    // Đăng ký
    public AuthResponse register(AuthRequest request){
        if (userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email này đã được sử dụng!");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .isActive(true)
                .subscriptionPlan(SubscriptionPlan.FREE) // Mặc định Free tier
                .monthlyQuota(5) // 5 repurposes/tháng cho Free
                .usedQuota(0)
                .quotaResetDate(LocalDate.now().plusMonths(1))
                .build();

        userRepository.save(user);

        // GIAO TIẾP NỘI BỘ: Tự động tạo Profile + Content Workspace qua OpenFeign
        ProfileCreationRequest profileCreationRequest = new ProfileCreationRequest();
        profileCreationRequest.setEmail(user.getEmail());
        profileCreationRequest.setFullName(user.getEmail().split("@")[0]);

        try {
            profileClient.createProfile(profileCreationRequest);
            System.out.println("✅ Giao tiếp nội bộ: Đã tạo Profile cho " + user.getEmail());

            // Tạo Content Workspace (thay vì Wallet)
            contentClient.createWorkspace(user.getEmail());
            System.out.println("✅ Giao tiếp nội bộ: Đã tạo Content Workspace cho " + user.getEmail());

        } catch (Exception e) {
            System.err.println("⚠️ Lỗi giao tiếp nội bộ: Chưa thể tạo profile hoặc workspace");
            e.printStackTrace();
        }

        // Sinh Token và trả về
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token, "Đăng ký thành công! Chào mừng đến với AI Content Repurposer!");
    }

    // Đăng nhập
    public AuthResponse login(AuthRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("Sai mật khẩu");
        }

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token, "Đăng nhập thành công!");
    }

    // Lấy thông tin quota
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user: " + email));
    }

    // Nâng cấp plan
    public User upgradePlan(String email, SubscriptionPlan newPlan) {
        User user = getUserByEmail(email);
        user.setSubscriptionPlan(newPlan);

        // Set quota theo plan
        switch (newPlan) {
            case STARTER -> user.setMonthlyQuota(50);
            case PRO -> user.setMonthlyQuota(999999); // Unlimited
            case ENTERPRISE -> user.setMonthlyQuota(999999);
            default -> user.setMonthlyQuota(5);
        }

        user.setUsedQuota(0); // Reset quota khi upgrade
        user.setQuotaResetDate(LocalDate.now().plusMonths(1));
        return userRepository.save(user);
    }
}
