package com.example.quanlytask.service;

import com.example.quanlytask.entity.Project;
import com.example.quanlytask.exception.NotFoundException;
import com.example.quanlytask.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public List<Project> getAll() {
        return projectRepository.findAll();
    }

    public Project getById(String id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy project: " + id));
    }

    public Project create(Project project) {
        return projectRepository.save(project);
    }

    public void delete(String id) {
        projectRepository.deleteById(id);
    }
}