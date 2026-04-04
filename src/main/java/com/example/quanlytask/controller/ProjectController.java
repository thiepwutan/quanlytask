package com.example.quanlytask.controller;

import com.example.quanlytask.dto.AppResponse;
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
    public AppResponse<List<Project>> getAll() {
        return AppResponse.success(projectService.getAll());
    }

    // GET /api/projects/p1
    @Operation(summary = "Lấy project theo ID")
    @GetMapping("/{id}")
    public AppResponse<Project> getById(@PathVariable String id) {
        return AppResponse.success(projectService.getById(id));
    }

    // POST /api/projects
    @Operation(summary = "Tạo project mới")
    @PostMapping
    public AppResponse<Project> create(@RequestBody Project project) {
        return AppResponse.success(projectService.create(project));
    }

    // DELETE /api/projects/p1
    @Operation(summary = "Xóa project")
    @DeleteMapping("/{id}")
    public AppResponse<String> delete(@PathVariable String id) {
        projectService.delete(id);
        return AppResponse.success("Đã xóa project " + id);
    }
}
