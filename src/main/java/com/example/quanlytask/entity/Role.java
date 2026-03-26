package com.example.quanlytask.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Role {

    @Id
    @Column(name = "id", length = 10)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;  // USER hoặc MANAGER
}