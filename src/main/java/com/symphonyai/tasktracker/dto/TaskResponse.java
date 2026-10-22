package com.symphonyai.tasktracker.dto;

import com.symphonyai.tasktracker.model.TaskPriority;
import com.symphonyai.tasktracker.model.TaskStatus;
import java.time.LocalDate;

public record TaskResponse(
        Long id, String title, TaskStatus status, TaskPriority priority,
        Long projectId, String projectName, Long assigneeId, String assigneeName, LocalDate dueDate
) {
}
