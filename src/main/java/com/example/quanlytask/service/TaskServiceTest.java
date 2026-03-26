package com.example.quanlytask.service;

import com.example.quanlytask.entity.Project;
import com.example.quanlytask.entity.Task;
import com.example.quanlytask.entity.TaskStatus;
import com.example.quanlytask.entity.User;
import com.example.quanlytask.exception.BadRequestException;
import com.example.quanlytask.exception.NotFoundException;
import com.example.quanlytask.repository.ProjectRepository;
import com.example.quanlytask.repository.TaskRepository;
import com.example.quanlytask.repository.UserRepository;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private Project project;
    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId("p1");
        project.setName("Website Redesign");

        user = new User();
        user.setId("u1");
        user.setName("Thiep");
        user.setEmail("thiep@gmail.com");

        task = new Task();
        task.setId("t1");
        task.setTitle("Thiết kế homepage");
        task.setStatus(TaskStatus.TODO);
        task.setProject(project);
        task.setAssignee(user);
    }

    // ---------- create ----------

    @Test
    void create_success() {
        when(projectRepository.findById("p1")).thenReturn(Optional.of(project));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task result = taskService.create("p1", "Thiết kế homepage");

        assertThat(result.getTitle()).isEqualTo("Thiết kế homepage");
        assertThat(result.getStatus()).isEqualTo(TaskStatus.TODO);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void create_projectNotFound_throwsNotFound() {
        when(projectRepository.findById("p999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.create("p999", "abc"))
                .isInstanceOf(NotFoundException.class);

        verify(taskRepository, never()).save(any());
    }

    // ---------- assign ----------

    @Test
    void assign_success() {
        when(taskRepository.findById("t1")).thenReturn(Optional.of(task));
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task result = taskService.assign("t1", "u1");

        assertThat(result.getAssignee().getId()).isEqualTo("u1");
        verify(taskRepository).save(task);
    }

    @Test
    void assign_taskNotFound_throwsNotFound() {
        when(taskRepository.findById("t999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.assign("t999", "u1"))
                .isInstanceOf(NotFoundException.class);

        verify(userRepository, never()).findById(any());
    }

    @Test
    void assign_userNotFound_throwsNotFound() {
        when(taskRepository.findById("t1")).thenReturn(Optional.of(task));
        when(userRepository.findById("u999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.assign("t1", "u999"))
                .isInstanceOf(NotFoundException.class);
    }

    // ---------- updateStatus ----------

    @Test
    void updateStatus_success() {
        task.setStatus(TaskStatus.TODO);
        when(taskRepository.findById("t1")).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task result = taskService.updateStatus("t1", TaskStatus.IN_PROGRESS);

        assertThat(result.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    void updateStatus_alreadyDone_throwsBadRequest() {
        task.setStatus(TaskStatus.DONE);
        when(taskRepository.findById("t1")).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.updateStatus("t1", TaskStatus.TODO))
                .isInstanceOf(BadRequestException.class);

        verify(taskRepository, never()).save(any());
    }

    @Test
    void updateStatus_taskNotFound_throwsNotFound() {
        when(taskRepository.findById("t999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateStatus("t999", TaskStatus.IN_PROGRESS))
                .isInstanceOf(NotFoundException.class);
    }

    // ---------- getByProject ----------

    @Test
    void getByProject_returnsList() {
        when(taskRepository.findByProjectId("p1")).thenReturn(List.of(task));

        List<Task> result = taskService.getByProject("p1");

        assertThat(result).hasSize(1);
        verify(taskRepository).findByProjectId("p1");
    }

    // ---------- getByUser ----------

    @Test
    void getByUser_returnsList() {
        when(taskRepository.findByAssigneeId("u1")).thenReturn(List.of(task));

        List<Task> result = taskService.getByUser("u1");

        assertThat(result).hasSize(1);
        verify(taskRepository).findByAssigneeId("u1");
    }

    // ---------- delete ----------

    @Test
    void delete_callsDeleteById() {
        doNothing().when(taskRepository).deleteById("t1");

        taskService.delete("t1");

        verify(taskRepository).deleteById("t1");
    }
}