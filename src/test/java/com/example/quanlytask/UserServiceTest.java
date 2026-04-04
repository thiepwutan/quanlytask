package com.example.quanlytask;

import com.example.quanlytask.entity.User;
import com.example.quanlytask.exception.*;
import com.example.quanlytask.repository.UserRepository;
import com.example.quanlytask.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Tạo user thành công")
    void create_success() {
        User user = new User();
        user.setId("u1");
        user.setName("Thiep");
        user.setEmail("thiep@gmail.com");
        user.setPassword("hashed");

        when(userRepository.findByEmail("thiep@gmail.com")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.create(user);

        assertNotNull(result);
        assertEquals("Thiep", result.getName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Tạo user thất bại khi email trùng")
    void create_emailDuplicate() {
        User existing = new User();
        existing.setEmail("thiep@gmail.com");

        User newUser = new User();
        newUser.setEmail("thiep@gmail.com");

        when(userRepository.findByEmail("thiep@gmail.com")).thenReturn(existing);

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> userService.create(newUser)
        );

        assertEquals("Email đã tồn tại: thiep@gmail.com", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Tìm user theo id thành công")
    void getById_found() {
        User user = new User();
        user.setId("u1");
        user.setName("thiep");

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));

        User result = userService.getById("u1");

        assertNotNull(result);
        assertEquals("u1", result.getId());
    }

    @Test
    @DisplayName("Tìm user theo id không thấy => NotFoundException")
    void getById_notFound() {
        when(userRepository.findById("u99")).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> userService.getById("u99")
        );

        assertEquals("Không tìm thấy user: u99", ex.getMessage());
    }
}