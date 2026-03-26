package com.example.quanlytask.controller;

import com.example.quanlytask.dto.*;
import com.example.quanlytask.entity.*;
import com.example.quanlytask.exception.*;
import com.example.quanlytask.repository.*;
import com.example.quanlytask.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "1. Auth", description = "Đăng ký và đăng nhập")
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    @Operation(summary = "Đăng ký tài khoản",
            description = "Role hợp lệ: USER hoặc MANAGER")
    // POST /api/auth/register
    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody RegisterRequest request) {

        // Kiểm tra email trùng
        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new BadRequestException("Email đã tồn tại");
        }

        // Tìm role
        Role role = roleRepository.findByName(request.getRole());
        if (role == null) {
            throw new BadRequestException("Role không hợp lệ, chỉ dùng USER hoặc MANAGER");
        }

        // Tạo user mới
        User user = new User();
        user.setId("u" + System.currentTimeMillis() % 100000);
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // hash BCrypt
        user.setRoles(Set.of(role));

        userRepository.save(user);
        return ApiResponse.success("Đăng ký thành công");
    }

    @Operation(summary = "Đăng nhập",
            description = "Trả về JWT token — copy token để dùng nút Authorize")
    // POST /api/auth/login
    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody LoginRequest request) {

        // Tìm user theo email
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new BadRequestException("Email không tồn tại");
        }

        // Kiểm tra password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Sai mật khẩu");
        }

        // Tạo JWT token
        String token = jwtUtil.generateToken(user.getEmail());
        return ApiResponse.success(token);
    }
}