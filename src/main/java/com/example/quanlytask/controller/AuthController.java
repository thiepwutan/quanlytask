package com.example.quanlytask.controller;

import com.example.quanlytask.dto.*;
import com.example.quanlytask.entity.*;
import com.example.quanlytask.exception.*;
import com.example.quanlytask.repository.*;
import com.example.quanlytask.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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


    @Operation(
            summary = "Đăng ký tài khoản",
            description = """
            Tạo tài khoản mới với role USER hoặc MANAGER.
            - **name**: Tên hiển thị (bắt buộc)
            - **email**: Phải là email hợp lệ và chưa tồn tại trong hệ thống
            - **password**: Mật khẩu (sẽ được mã hóa BCrypt)
            - **role**: Chỉ chấp nhận `USER` hoặc `MANAGER`
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Đăng ký thành công",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "code": 200,
                      "message": "Thành công",
                      "data": "Đăng ký thành công"
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Lỗi dữ liệu đầu vào",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Email đã tồn tại", value = """
                        {
                          "code": 400,
                          "message": "Email đã tồn tại",
                          "data": null
                        }
                        """),
                                    @ExampleObject(name = "Role không hợp lệ", value = """
                        {
                          "code": 400,
                          "message": "Role không hợp lệ, chỉ dùng USER hoặc MANAGER",
                          "data": null
                        }
                        """)
                            })
            )
    })
    // POST /api/auth/register
    @PostMapping("/register")
    public AppResponse<String> register(@RequestBody RegisterRequest request) {

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
        return AppResponse.success("Đăng ký thành công");
    }

    @Operation(
            summary = "Đăng nhập",
            description = """
            Đăng nhập bằng email và password. Trả về JWT token.
            
            **Cách dùng token:**
            1. Copy chuỗi token từ trường `data`
            2. Click nút **Authorize** (🔒) ở góc trên phải
            3. Paste token vào ô và click Authorize
            4. Tất cả API sau đó sẽ tự động gửi kèm token
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Đăng nhập thành công, trả về JWT token",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "code": 200,
                      "message": "Thành công",
                      "data": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGllcEBnbWFpbC5jb20iLCJpYXQiOjE3MDA..."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Sai thông tin đăng nhập",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Email không tồn tại", value = """
                        {
                          "code": 400,
                          "message": "Email không tồn tại",
                          "data": null
                        }
                        """),
                                    @ExampleObject(name = "Sai mật khẩu", value = """
                        {
                          "code": 400,
                          "message": "Sai mật khẩu",
                          "data": null
                        }
                        """)
                            })
            )
    })
    // POST /api/auth/login
    @PostMapping("/login")
    public AppResponse<String> login(@RequestBody LoginRequest request) {

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
        return AppResponse.success(token);
    }
}