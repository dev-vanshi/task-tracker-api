package com.symphonyai.tasktracker.controller;

import com.symphonyai.tasktracker.config.AppProperties;
import com.symphonyai.tasktracker.dto.InfoResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {
    private final AppProperties appProperties;
    public InfoController(AppProperties appProperties) { this.appProperties = appProperties; }

    @GetMapping("/api/info")
    public InfoResponse info() { return new InfoResponse(appProperties.name(), appProperties.version()); }
}
