package com.symphonyai.tasktracker.config;

import com.symphonyai.tasktracker.model.TaskPriority;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "task")
public record TaskProperties(TaskPriority defaultPriority) {
}
