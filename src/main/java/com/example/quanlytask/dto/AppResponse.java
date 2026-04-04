package com.example.quanlytask.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppResponse<T> {
    private int code;
    private String message;
    private T data;

    // Thành công
    public static <T> AppResponse<T> success(T data) {
        return new AppResponse<>(200, "Thành công", data);
    }

    // Lỗi
    public static <T> AppResponse<T> error(int code, String message) {
        return new AppResponse<>(code, message, null);
    }
}