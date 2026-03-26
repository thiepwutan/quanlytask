package com.example.quanlytask.controller;

import com.example.quanlytask.dto.ApiResponse;
import com.example.quanlytask.entity.Project;
import com.example.quanlytask.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "3. Project", description = "Quản lý project")
public class ProjectController {

    private final ProjectService projectService;

    // GET /api/projects
    @Operation(summary = "Lấy tất cả project")
    @GetMapping
    public ApiResponse<List<Project>> getAll() {
        return ApiResponse.success(projectService.getAll());
    }

    // GET /api/projects/p1
    @Operation(summary = "Lấy project theo ID")
    @GetMapping("/{id}")
    public ApiResponse<Project> getById(@PathVariable String id) {
        return ApiResponse.success(projectService.getById(id));
    }

    // POST /api/projects
    @Operation(summary = "Tạo project mới")
    @PostMapping
    public ApiResponse<Project> create(@RequestBody Project project) {
        return ApiResponse.success(projectService.create(project));
    }

    // DELETE /api/projects/p1
    @Operation(summary = "Xóa project")
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable String id) {
        projectService.delete(id);
        return ApiResponse.success("Đã xóa project " + id);
    }
}
