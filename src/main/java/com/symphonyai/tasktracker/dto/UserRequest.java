package com.symphonyai.tasktracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 150)
        String name,
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 254)
        String email,
        @NotBlank(message = "Role is required")
        @Size(max = 50)
        String role
) {
}
