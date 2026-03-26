package com.example.quanlytask.repository;

import com.example.quanlytask.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, String> {
}
