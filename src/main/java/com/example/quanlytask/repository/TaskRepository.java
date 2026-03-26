package com.example.quanlytask.repository;

import com.example.quanlytask.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, String> {
    // Tìm task theo user
    List<Task> findByAssigneeId(String assigneeId);

    // Tìm task theo project
    List<Task> findByProjectId(String projectId);
}
