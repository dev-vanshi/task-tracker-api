package com.symphonyai.tasktracker.controller;

import com.symphonyai.tasktracker.dto.ProjectRequest;
import com.symphonyai.tasktracker.dto.ProjectResponse;
import com.symphonyai.tasktracker.dto.TaskResponse;
import com.symphonyai.tasktracker.model.TaskStatus;
import com.symphonyai.tasktracker.service.ProjectService;
import com.symphonyai.tasktracker.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final TaskService taskService;

    public ProjectController(ProjectService projectService, TaskService taskService) {
        this.projectService = projectService;
        this.taskService = taskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse create(@Valid @RequestBody ProjectRequest request) { return projectService.create(request); }

    @GetMapping
    public List<ProjectResponse> findAll() { return projectService.findAll(); }

    @GetMapping("/{id}")
    public ProjectResponse findById(@PathVariable Long id) { return projectService.findById(id); }

    @PutMapping("/{id}")
    public ProjectResponse update(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
        return projectService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { projectService.delete(id); }

    @GetMapping("/{id}/tasks")
    public Page<TaskResponse> tasks(@PathVariable Long id,
                                    @RequestParam(required = false) Integer page,
                                    @RequestParam(required = false) Integer size) {
        return taskService.findByProject(id, page, size);
    }
}
