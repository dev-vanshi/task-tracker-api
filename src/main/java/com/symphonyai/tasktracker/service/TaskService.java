package com.symphonyai.tasktracker.service;

import com.symphonyai.tasktracker.config.PaginationProperties;
import com.symphonyai.tasktracker.config.TaskProperties;
import com.symphonyai.tasktracker.dto.TaskRequest;
import com.symphonyai.tasktracker.dto.TaskResponse;
import com.symphonyai.tasktracker.exception.ResourceNotFoundException;
import com.symphonyai.tasktracker.model.*;
import com.symphonyai.tasktracker.repository.TaskRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectService projectService;
    private final UserService userService;
    private final TaskProperties taskProperties;
    private final PaginationProperties paginationProperties;

    public TaskService(TaskRepository taskRepository, ProjectService projectService, UserService userService,
                       TaskProperties taskProperties, PaginationProperties paginationProperties) {
        this.taskRepository = taskRepository;
        this.projectService = projectService;
        this.userService = userService;
        this.taskProperties = taskProperties;
        this.paginationProperties = paginationProperties;
    }

    public TaskResponse create(TaskRequest request) {
        Project project = projectService.getProject(request.projectId());
        TeamUser assignee = request.assigneeId() == null ? null : userService.getUser(request.assigneeId());
        Task task = new Task(request.title().trim(),
                request.status() == null ? TaskStatus.TODO : request.status(),
                request.priority() == null ? taskProperties.defaultPriority() : request.priority(),
                project, assignee, request.dueDate());
        return toResponse(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> findAll(TaskStatus status, Long projectId, Integer page, Integer size, String sortBy, String direction) {
        int pageNumber = page == null ? 0 : page;
        int pageSize = size == null ? paginationProperties.defaultPageSize() : size;
        if (pageNumber < 0) throw new IllegalArgumentException("Page must be zero or greater");
        if (pageSize < 1 || pageSize > 100) throw new IllegalArgumentException("Page size must be between 1 and 100");
        String safeSort = List.of("id", "title", "status", "priority", "dueDate").contains(sortBy) ? sortBy : "id";
        Sort sort = "desc".equalsIgnoreCase(direction) ? Sort.by(safeSort).descending() : Sort.by(safeSort).ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        if (projectId != null) projectService.getProject(projectId);
        if (status != null && projectId != null) return taskRepository.findByStatusAndProject_Id(status, projectId, pageable).map(this::toResponse);
        if (status != null) return taskRepository.findByStatus(status, pageable).map(this::toResponse);
        if (projectId != null) return taskRepository.findByProject_Id(projectId, pageable).map(this::toResponse);
        return taskRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public TaskResponse findById(Long id) { return toResponse(getTask(id)); }

    public TaskResponse update(Long id, TaskRequest request) {
        Task task = getTask(id);
        Project project = projectService.getProject(request.projectId());
        TeamUser assignee = request.assigneeId() == null ? null : userService.getUser(request.assigneeId());
        task.setTitle(request.title().trim());
        task.setStatus(request.status() == null ? task.getStatus() : request.status());
        task.setPriority(request.priority() == null ? task.getPriority() : request.priority());
        task.setProject(project);
        task.setAssignee(assignee);
        task.setDueDate(request.dueDate());
        return toResponse(taskRepository.save(task));
    }

    public void delete(Long id) { taskRepository.delete(getTask(id)); }

    @Transactional(readOnly = true)
    public Page<TaskResponse> findByProject(Long projectId, Integer page, Integer size) {
        projectService.getProject(projectId);
        int pageSize = size == null ? paginationProperties.defaultPageSize() : size;
        int pageNumber = page == null ? 0 : page;
        if (pageNumber < 0 || pageSize < 1 || pageSize > 100) throw new IllegalArgumentException("Invalid page or size");
        return taskRepository.findByProject_Id(projectId, PageRequest.of(pageNumber, pageSize, Sort.by("id").ascending()))
                .map(this::toResponse);
    }

    private Task getTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + id));
    }

    private TaskResponse toResponse(Task t) {
        return new TaskResponse(t.getId(), t.getTitle(), t.getStatus(), t.getPriority(),
                t.getProject().getId(), t.getProject().getName(),
                t.getAssignee() == null ? null : t.getAssignee().getId(),
                t.getAssignee() == null ? null : t.getAssignee().getName(), t.getDueDate());
    }
}
