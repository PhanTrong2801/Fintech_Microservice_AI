package com.fintech.identity_service.service;

import com.fintech.identity_service.dto.AuthRequest;
import com.fintech.identity_service.dto.AuthResponse;
import com.fintech.identity_service.entity.User;
import com.fintech.identity_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    //login Đăng kí
    public AuthResponse register(AuthRequest request){
        if (userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email này đã được sử dụng!");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .isActive(true)
                .build();

        userRepository.save(user);
// 4. Sinh Token và trả về
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token,"Đăng kí thành công!");
    }
    // Logic Đăng nhập
    public AuthResponse login (AuthRequest request){
        // 1. Tìm user theo email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new RuntimeException("Không tìm thấy tài khoản!" ));

        // 2. Kiểm tra mật khẩu (so sánh chuỗi text với mã băm trong DB)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("Sai mật khẩu");
        }

        // 3. Sinh Token và trả về
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token,"Đăng nhập thành công!");
    }

}
