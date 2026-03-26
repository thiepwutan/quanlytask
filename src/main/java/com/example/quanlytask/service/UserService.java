package com.example.quanlytask.service;

import com.example.quanlytask.entity.User;
import com.example.quanlytask.exception.BadRequestException;
import com.example.quanlytask.exception.NotFoundException;
import com.example.quanlytask.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // Lấy tất cả user
    public List<User> getAll() {
        return userRepository.findAll();
    }

    // Tìm theo id
    public User getById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy user: " + id));
    }

    // Thêm mới
    public User create(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new BadRequestException("Email đã tồn tại: " + user.getEmail());
        }
        return userRepository.save(user);
    }

    // Xóa
    public void delete(String id) {
        userRepository.deleteById(id);
    }
}