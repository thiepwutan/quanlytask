package com.example.quanlytask.service;

import com.example.quanlytask.entity.*;
import com.example.quanlytask.exception.BadRequestException;
import com.example.quanlytask.exception.NotFoundException;
import com.example.quanlytask.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    // Tạo task mới — validate projectId tồn tại
    public Task create(String projectId, String title) {

        // Kiểm tra project có tồn tại không
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy project: " + projectId));


        Task task = new Task();
        task.setId("t" + (System.currentTimeMillis() % 100000)); // tự tạo id
        task.setTitle(title);
        task.setStatus(TaskStatus.TODO);
        task.setProject(project);

        return taskRepository.save(task);
    }

    // Assign task cho user
    public Task assign(String taskId, String userId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy task: " + taskId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy user: " + userId));


        task.setAssignee(user);
        return taskRepository.save(task);
    }

    // Update status — chặn nếu đã DONE
    public Task updateStatus(String taskId, TaskStatus newStatus) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy task: " + taskId));

        if (task.getStatus() == TaskStatus.DONE) {
            throw new BadRequestException("Không thể cập nhật task đã DONE");
        }

        task.setStatus(newStatus);
        return taskRepository.save(task);
    }

    // List task theo project
    public List<Task> getByProject(String projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    // List task theo user
    public List<Task> getByUser(String userId) {
        return taskRepository.findByAssigneeId(userId);
    }

    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    public void delete(String id) {
        taskRepository.deleteById(id);
    }
}