package com.example.quanlytask.controller;

import com.example.quanlytask.dto.ApiResponse;
import com.example.quanlytask.exception.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 - không tìm thấy
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<?> handleNotFound(NotFoundException ex) {
        return ApiResponse.error(404, ex.getMessage());
    }

    // 400 - request sai
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleBadRequest(BadRequestException ex) {
        return ApiResponse.error(400, ex.getMessage());
    }

    // 400 - validate @NotBlank, @Size thất bại (@RequestBody)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Dữ liệu không hợp lệ");
        return ApiResponse.error(400, message);
    }

    // 400 - validate @RequestParam thất bại (@Validated trên class)
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getMessage())
                .findFirst()
                .orElse("Dữ liệu không hợp lệ");
        return ApiResponse.error(400, message);
    }

    // 500 - lỗi server - luôn để cuối cùng
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<?> handleGeneral(Exception ex) {
        return ApiResponse.error(500, "Lỗi server: " + ex.getMessage());
    }
}