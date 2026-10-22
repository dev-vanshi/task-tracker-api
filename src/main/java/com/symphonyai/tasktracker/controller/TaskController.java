package com.symphonyai.tasktracker.controller;

import com.symphonyai.tasktracker.dto.TaskRequest;
import com.symphonyai.tasktracker.dto.TaskResponse;
import com.symphonyai.tasktracker.model.TaskStatus;
import com.symphonyai.tasktracker.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;
    public TaskController(TaskService taskService) { this.taskService = taskService; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@Valid @RequestBody TaskRequest request) { return taskService.create(request); }

    @GetMapping
    public Page<TaskResponse> findAll(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return taskService.findAll(status, projectId, page, size, sortBy, direction);
    }

    @GetMapping("/{id}")
    public TaskResponse findById(@PathVariable Long id) { return taskService.findById(id); }

    @PutMapping("/{id}")
    public TaskResponse update(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        return taskService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { taskService.delete(id); }
}
