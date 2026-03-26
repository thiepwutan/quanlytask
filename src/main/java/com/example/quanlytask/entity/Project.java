package com.example.quanlytask.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "projects")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Project {

    @Id
    @Column(name = "id", length = 10)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    // 1 Project có nhiều Task
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    @JsonIgnore   // tránh lỗi vòng lặp json
    private List<Task> tasks;
}