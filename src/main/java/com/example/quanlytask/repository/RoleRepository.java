package com.example.quanlytask.repository;

import com.example.quanlytask.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {
    Role findByName(String name);
}