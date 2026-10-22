package com.symphonyai.tasktracker.repository;

import com.symphonyai.tasktracker.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    boolean existsByNameIgnoreCase(String name);
}
