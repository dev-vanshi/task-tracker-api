package com.symphonyai.tasktracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectRequest(
        @NotBlank(message = "Project name is required")
        @Size(max = 150, message = "Project name must be at most 150 characters")
        String name,
        @Size(max = 2000, message = "Description must be at most 2000 characters")
        String description
) {
}
