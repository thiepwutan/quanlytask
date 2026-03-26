package com.example.quanlytask.controller;

import com.example.quanlytask.dto.ApiResponse;
import com.example.quanlytask.entity.*;
import com.example.quanlytask.exception.BadRequestException;
import com.example.quanlytask.repository.UserRepository;
import com.example.quanlytask.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Validated
@Tag(name = "2. Task", description = "Quản lý task")
public class TaskController {

    private final TaskService taskService;
                  UserRepository userRepository;

    @Operation(summary = "Lấy tất cả task")
    // GET /api/tasks
    @GetMapping
    public ApiResponse<List<Task>> getAll() {
        return ApiResponse.success(taskService.getAll());  // wrap vào ApiResponse
    }

    // POST /api/tasks?projectId=p1&title=Làm homepage
    @Operation(summary = "Tạo task mới")
    @PostMapping
    public ApiResponse<Task> create(
            @RequestParam @NotBlank(message = "projectId không được để trống") String projectId,
            @RequestParam @NotBlank(message = "Tiêu đề không được để trống") String title) {
        return ApiResponse.success(taskService.create(projectId, title));
    }

    // PUT /api/tasks/t1/assign?userId=u1
    @Operation(summary = "Assign task cho user")
    @PutMapping("/{taskId}/assign")
    public ApiResponse<Task> assign(@PathVariable String taskId,
                                    @RequestParam String userId) {
        return ApiResponse.success(taskService.assign(taskId, userId));
    }

    // PUT /api/tasks/t1/status?newStatus=IN_PROGRESS
    @Operation(summary = "Cập nhật trạng thái",
            description = "Không thể cập nhật task đã DONE. Các giá trị: TODO, IN_PROGRESS, DONE")
    @PutMapping("/{taskId}/status")
    public ApiResponse<Task> updateStatus(@PathVariable String taskId,
                                          @RequestParam TaskStatus newStatus) {
        return ApiResponse.success(taskService.updateStatus(taskId, newStatus));
    }

    // GET /api/tasks/project/p1
    @Operation(summary = "Lấy task theo project")
    @GetMapping("/project/{projectId}")
    public ApiResponse<List<Task>> getByProject(@PathVariable String projectId) {
        return ApiResponse.success(taskService.getByProject(projectId));
    }

    // GET /api/tasks/user/u1
    @Operation(summary = "Lấy task theo user",
            description = "USER chỉ xem được task của chính mình. MANAGER xem được tất cả")
    @GetMapping("/user/{userId}")
    public ApiResponse<List<Task>> getByUser(@PathVariable String userId,
                                             Authentication authentication) {
        // Lấy email từ token
        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email);

        // Nếu là USER thì chỉ xem task của mình
        boolean isManager = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName().equals("MANAGER"));

        if (!isManager && !currentUser.getId().equals(userId)) {
            throw new BadRequestException("Bạn chỉ có thể xem task của chính mình");
        }

        return ApiResponse.success(taskService.getByUser(userId));
    }

    // DELETE /api/tasks/t1
    @Operation(summary = "Xóa task")
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable String id) {
        taskService.delete(id);
        return ApiResponse.success("Đã xóa task " + id);  // wrap
    }
}