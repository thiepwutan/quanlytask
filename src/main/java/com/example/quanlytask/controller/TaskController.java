package com.example.quanlytask.controller;

import com.example.quanlytask.dto.AppResponse;
import com.example.quanlytask.entity.*;
import com.example.quanlytask.exception.BadRequestException;
import com.example.quanlytask.repository.UserRepository;
import com.example.quanlytask.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@SecurityRequirement(name = "BearerAuth")
public class TaskController {

    private final TaskService taskService;
    UserRepository userRepository;

    @Operation(
            summary = "Lấy tất cả task",
            description = "Trả về danh sách tất cả task trong hệ thống. Yêu cầu đăng nhập."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lấy danh sách thành công",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "code": 200,
                      "message": "Thành công",
                      "data": [
                        { "id": "t1", "title": "Thiết kế homepage", "status": "IN_PROGRESS" },
                        { "id": "t2", "title": "Viết API login", "status": "DONE" }
                      ]
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Chưa đăng nhập hoặc token không hợp lệ",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "code": 403,
                      "message": "Access Denied",
                      "data": null
                    }
                    """))
            )
    })
    // GET /api/tasks
    @GetMapping
    public AppResponse<List<Task>> getAll() {
        return AppResponse.success(taskService.getAll());  // wrap vào ApiResponse
    }

    // POST /api/tasks?projectId=p1&title=Làm homepage
    @Operation(
            summary = "Tạo task mới",
            description = """
            Tạo một task mới gắn với project.
            - **projectId**: ID project phải tồn tại trong hệ thống
            - **title**: Tiêu đề task, không được để trống
            - Task mới sẽ có status mặc định là `TODO`
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tạo task thành công",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "code": 200,
                      "message": "Thành công",
                      "data": {
                        "id": "t12345",
                        "title": "Thiết kế homepage",
                        "status": "TODO",
                        "project": { "id": "p1", "name": "Website Redesign" },
                        "assignee": null
                      }
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dữ liệu đầu vào không hợp lệ",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "projectId trống", value = """
                        {
                          "code": 400,
                          "message": "projectId không được để trống",
                          "data": null
                        }
                        """),
                                    @ExampleObject(name = "title trống", value = """
                        {
                          "code": 400,
                          "message": "Tiêu đề không được để trống",
                          "data": null
                        }
                        """)
                            })
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy project",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "code": 404,
                      "message": "Không tìm thấy project: p999",
                      "data": null
                    }
                    """))
            )
    })
    @PostMapping
    public AppResponse<Task> create(
            @Parameter(description = "ID của project (ví dụ: p1, p2)", example = "p1")
            @RequestParam @NotBlank(message = "projectId không được để trống") String projectId,
            @Parameter(description = "Tiêu đề của task", example = "Thiết kế homepage")
            @RequestParam @NotBlank(message = "Tiêu đề không được để trống") String title) {
        return AppResponse.success(taskService.create(projectId, title));
    }

    // PUT /api/tasks/t1/assign?userId=u1
    @Operation(
            summary = "Assign task cho user",
            description = """
            Giao task cho một user cụ thể.
            - **taskId**: ID task phải tồn tại
            - **userId**: ID user phải tồn tại
            - Một task chỉ có một người được assign tại một thời điểm (assign lại sẽ ghi đè)
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Assign thành công",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "code": 200,
                      "message": "Thành công",
                      "data": {
                        "id": "t1",
                        "title": "Thiết kế homepage",
                        "status": "TODO",
                        "assignee": { "id": "u1", "name": "Thiệp" }
                      }
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy task hoặc user",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Task không tồn tại", value = """
                        {
                          "code": 404,
                          "message": "Không tìm thấy task: t999",
                          "data": null
                        }
                        """),
                                    @ExampleObject(name = "User không tồn tại", value = """
                        {
                          "code": 404,
                          "message": "Không tìm thấy user: u999",
                          "data": null
                        }
                        """)
                            })
            )
    })
    @PutMapping("/{taskId}/assign")
    public AppResponse<Task> assign(@PathVariable String taskId,
                                    @RequestParam String userId) {
        return AppResponse.success(taskService.assign(taskId, userId));
    }

    // PUT /api/tasks/t1/status?newStatus=IN_PROGRESS
    @Operation(
            summary = "Cập nhật trạng thái",
            description = """
            Thay đổi trạng thái của task.
            
            **Luồng trạng thái hợp lệ:**
            - `TODO` → `IN_PROGRESS`
            - `IN_PROGRESS` → `DONE`
            - `TODO` → `DONE`
            
            ️ **Lưu ý:** Task đã ở trạng thái `DONE` không thể cập nhật thêm.
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cập nhật thành công",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "code": 200,
                      "message": "Thành công",
                      "data": {
                        "id": "t1",
                        "title": "Thiết kế homepage",
                        "status": "IN_PROGRESS"
                      }
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Task đã DONE, không thể cập nhật",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "code": 400,
                      "message": "Không thể cập nhật task đã DONE",
                      "data": null
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy task",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "code": 404,
                      "message": "Không tìm thấy task: t999",
                      "data": null
                    }
                    """))
            )
    })
    @PutMapping("/{taskId}/status")
    public AppResponse<Task> updateStatus(@PathVariable String taskId,
                                          @RequestParam TaskStatus newStatus) {
        return AppResponse.success(taskService.updateStatus(taskId, newStatus));
    }

    // GET /api/tasks/project/p1
    @Operation(
            summary = "Lấy task theo project",
            description = "Trả về tất cả task thuộc một project. Trả về danh sách rỗng nếu project không có task."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lấy danh sách thành công",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "code": 200,
                      "message": "Thành công",
                      "data": [
                        { "id": "t1", "title": "Thiết kế homepage", "status": "IN_PROGRESS" },
                        { "id": "t5", "title": "Fix bug header", "status": "TODO" }
                      ]
                    }
                    """))
            )
    })
    @GetMapping("/project/{projectId}")
    public AppResponse<List<Task>> getByProject(@PathVariable String projectId) {
        return AppResponse.success(taskService.getByProject(projectId));
    }

    // GET /api/tasks/user/u1
    @Operation(
            summary = "Lấy task theo user",
            description = """
            Lấy danh sách task được giao cho một user.
            
            **Phân quyền:**
            - `USER`: Chỉ xem được task của chính mình (userId phải trùng với ID của mình)
            - `MANAGER`: Xem được task của tất cả mọi người
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lấy danh sách thành công",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "code": 200,
                      "message": "Thành công",
                      "data": [
                        { "id": "t1", "title": "Thiết kế homepage", "status": "IN_PROGRESS" }
                      ]
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "USER cố xem task của người khác",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "code": 400,
                      "message": "Bạn chỉ có thể xem task của chính mình",
                      "data": null
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Chưa đăng nhập hoặc token hết hạn",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "code": 403,
                      "message": "Access Denied",
                      "data": null
                    }
                    """))
            )
    })
    @GetMapping("/user/{userId}")
    public AppResponse<List<Task>> getByUser(@PathVariable String userId,
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

        return AppResponse.success(taskService.getByUser(userId));
    }

    // DELETE /api/tasks/t1
    @Operation(
            summary = "Xóa task",
            description = "Xóa vĩnh viễn một task khỏi hệ thống theo ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Xóa thành công",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "code": 200,
                      "message": "Thành công",
                      "data": "Đã xóa task t1"
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Chưa đăng nhập",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "code": 403,
                      "message": "Access Denied",
                      "data": null
                    }
                    """))
            )
    })
    @DeleteMapping("/{id}")
    public AppResponse<String> delete(@PathVariable String id) {
        taskService.delete(id);
        return AppResponse.success("Đã xóa task " + id);  // wrap
    }
}