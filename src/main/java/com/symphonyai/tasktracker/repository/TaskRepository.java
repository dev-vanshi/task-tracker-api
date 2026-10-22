package com.symphonyai.tasktracker.repository;

import com.symphonyai.tasktracker.model.Task;
import com.symphonyai.tasktracker.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByStatus(TaskStatus status, Pageable pageable);
    Page<Task> findByStatusAndProject_Id(TaskStatus status, Long projectId, Pageable pageable);
    Page<Task> findByProject_Id(Long projectId, Pageable pageable);
    boolean existsByProject_Id(Long projectId);
    boolean existsByAssignee_Id(Long userId);
}
