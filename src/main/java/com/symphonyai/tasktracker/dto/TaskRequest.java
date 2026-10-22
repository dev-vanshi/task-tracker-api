package com.symphonyai.tasktracker.dto;

import com.symphonyai.tasktracker.model.TaskPriority;
import com.symphonyai.tasktracker.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record TaskRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 200)
        String title,
        TaskStatus status,
        TaskPriority priority,
        @NotNull(message = "Project ID is required")
        Long projectId,
        Long assigneeId,
        LocalDate dueDate
) {
}
