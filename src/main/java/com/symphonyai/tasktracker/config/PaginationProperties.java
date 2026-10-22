package com.symphonyai.tasktracker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pagination")
public record PaginationProperties(int defaultPageSize) {
}
