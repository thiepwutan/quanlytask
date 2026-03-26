package com.example.quanlytask.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String id;
    private String name;
    private String email;
    private String password;
    private String role;  // "USER" hoặc "MANAGER"
}