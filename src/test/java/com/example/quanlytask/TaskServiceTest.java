package com.example.quanlytask;

import com.example.quanlytask.entity.*;
import com.example.quanlytask.exception.BadRequestException;
import com.example.quanlytask.exception.NotFoundException;
import com.example.quanlytask.repository.ProjectRepository;
import com.example.quanlytask.repository.TaskRepository;
import com.example.quanlytask.repository.UserRepository;
import com.example.quanlytask.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock TaskRepository taskRepository;
    @Mock ProjectRepository projectRepository;
    @Mock UserRepository userRepository;

    @InjectMocks
    TaskService taskService;

    Project testProject;
    User assigneeUser;
    Task testTask;

    @BeforeEach
    void setUp() {
        testProject = createProject("p1", "Website Redesign");
        assigneeUser = createUser("u1", "Thiep", "thiep@gmail.com");
        testTask = createTask("t1", "Thiết kế homepage", TaskStatus.TODO, testProject, assigneeUser);
    }

    // ---------- Helper methods ----------
    private Project createProject(String id, String name) {
        Project p = new Project();
        p.setId(id);
        p.setName(name);
        return p;
    }

    private User createUser(String id, String name, String email) {
        User u = new User();
        u.setId(id);
        u.setName(name);
        u.setEmail(email);
        return u;
    }

    private Task createTask(String id, String title, TaskStatus status, Project project, User assignee) {
        Task t = new Task();
        t.setId(id);
        t.setTitle(title);
        t.setStatus(status);
        t.setProject(project);
        t.setAssignee(assignee);
        return t;
    }

    // ---------- CREATE ----------
    @Test
    void create_shouldReturnTask_whenValidInput() {
        when(projectRepository.findById("p1")).thenReturn(Optional.of(testProject));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task result = taskService.create("p1", "Thiết kế homepage");

        assertThat(result.getTitle()).isEqualTo("Thiết kế homepage");
        assertThat(result.getStatus()).isEqualTo(TaskStatus.TODO);
        verify(taskRepository).save(argThat(t -> t.getTitle().equals("Thiết kế homepage")));
    }

    @Test
    void create_shouldThrowNotFound_whenProjectNotExist() {
        when(projectRepository.findById("p999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.create("p999", "abc"))
                .isInstanceOf(NotFoundException.class);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowBadRequest_whenTitleNullOrEmpty() {

        assertThatThrownBy(() -> taskService.create("p1", null))
                .isInstanceOf(BadRequestException.class);

        assertThatThrownBy(() -> taskService.create("p1", ""))
                .isInstanceOf(BadRequestException.class);

        verify(taskRepository, never()).save(any());
    }

    // ---------- ASSIGN ----------
    @Test
    void assign_shouldUpdateAssignee_whenValid() {
        when(taskRepository.findById("t1")).thenReturn(Optional.of(testTask));
        when(userRepository.findById("u1")).thenReturn(Optional.of(assigneeUser));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task result = taskService.assign("t1", "u1");

        assertThat(result.getAssignee().getId()).isEqualTo("u1");
        verify(taskRepository).save(argThat(t -> t.getAssignee().getId().equals("u1")));
    }

    @Test
    void assign_shouldThrowNotFound_whenTaskNotExist() {
        when(taskRepository.findById("t999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.assign("t999", "u1"))
                .isInstanceOf(NotFoundException.class);
        verify(userRepository, never()).findById(any());
    }

    @Test
    void assign_shouldThrowNotFound_whenUserNotExist() {
        when(taskRepository.findById("t1")).thenReturn(Optional.of(testTask));
        when(userRepository.findById("u999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.assign("t1", "u999"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void assign_shouldOverwriteExistingAssignee() {
        User newUser = createUser("u2", "Minh", "minh@gmail.com");
        when(taskRepository.findById("t1")).thenReturn(Optional.of(testTask));
        when(userRepository.findById("u2")).thenReturn(Optional.of(newUser));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task result = taskService.assign("t1", "u2");

        assertThat(result.getAssignee().getId()).isEqualTo("u2");
        verify(taskRepository).save(argThat(t -> t.getAssignee().getId().equals("u2")));
    }

    // ---------- UPDATE STATUS ----------
    @Test
    void updateStatus_shouldUpdateStatus_whenValid() {
        testTask.setStatus(TaskStatus.TODO);
        when(taskRepository.findById("t1")).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task result = taskService.updateStatus("t1", TaskStatus.IN_PROGRESS);

        assertThat(result.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    void updateStatus_shouldThrowBadRequest_whenAlreadyDone() {
        testTask.setStatus(TaskStatus.DONE);
        when(taskRepository.findById("t1")).thenReturn(Optional.of(testTask));

        assertThatThrownBy(() -> taskService.updateStatus("t1", TaskStatus.TODO))
                .isInstanceOf(BadRequestException.class);

        verify(taskRepository, never()).save(any());
    }

    @Test
    void updateStatus_shouldThrowNotFound_whenTaskNotExist() {
        when(taskRepository.findById("t999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateStatus("t999", TaskStatus.IN_PROGRESS))
                .isInstanceOf(NotFoundException.class);
    }

    // ---------- GET ----------
    @Test
    void getByProject_shouldReturnTaskList() {
        when(taskRepository.findByProjectId("p1")).thenReturn(List.of(testTask));

        List<Task> result = taskService.getByProject("p1");

        assertThat(result).hasSize(1);
        verify(taskRepository).findByProjectId("p1");
    }

    @Test
    void getByUser_shouldReturnTaskList() {
        when(taskRepository.findByAssigneeId("u1")).thenReturn(List.of(testTask));

        List<Task> result = taskService.getByUser("u1");

        assertThat(result).hasSize(1);
        verify(taskRepository).findByAssigneeId("u1");
    }

    // ---------- DELETE ----------
    @Test
    void delete_shouldCallDeleteById() {
        doNothing().when(taskRepository).deleteById("t1");

        taskService.delete("t1");

        verify(taskRepository).deleteById("t1");
    }
}