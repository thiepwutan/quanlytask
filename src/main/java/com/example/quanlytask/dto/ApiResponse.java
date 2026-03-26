package com.example.quanlytask.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    // Thành công
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Thành công", data);
    }

    // Lỗi
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}