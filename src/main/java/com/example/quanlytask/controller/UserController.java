package com.example.quanlytask.controller;

import com.example.quanlytask.dto.ApiResponse;
import com.example.quanlytask.entity.User;
import com.example.quanlytask.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "4. User", description = "Quản lý user")
public class UserController {

    private final UserService userService;

    // GET /api/users
    @Operation(summary = "Lấy tất cả user")
    @GetMapping
    public ApiResponse<List<User>> getAll() {
        return ApiResponse.success(userService.getAll());
    }

    // GET /api/users/u1
    @Operation(summary = "Lấy user theo ID")
    @GetMapping("/{id}")
    public ApiResponse<User> getById(@PathVariable String id) {
        return ApiResponse.success(userService.getById(id));
    }

    // POST /api/users
    @Operation(summary = "Tạo user mới",
            description = "Email phải unique. Dùng endpoint register thay thế nếu cần hash password")
    @PostMapping
    public ApiResponse<User> create(@Valid @RequestBody User user) {  //@Valid để trigger validation
        return ApiResponse.success(userService.create(user));
    }

    // DELETE /api/users/u1
    @Operation(summary = "Xóa user")
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable String id) {
        userService.delete(id);
        return ApiResponse.success("Đã xóa user " + id);
    }


}