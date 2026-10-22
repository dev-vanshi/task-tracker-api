package com.symphonyai.tasktracker.service;

import com.symphonyai.tasktracker.dto.ProjectRequest;
import com.symphonyai.tasktracker.dto.ProjectResponse;
import com.symphonyai.tasktracker.exception.ConflictException;
import com.symphonyai.tasktracker.exception.ResourceNotFoundException;
import com.symphonyai.tasktracker.model.Project;
import com.symphonyai.tasktracker.repository.ProjectRepository;
import com.symphonyai.tasktracker.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public ProjectService(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    public ProjectResponse create(ProjectRequest request) {
        if (projectRepository.existsByNameIgnoreCase(request.name().trim())) {
            throw new ConflictException("A project with this name already exists");
        }
        Project project = new Project(request.name().trim(), request.description());
        return toResponse(projectRepository.save(project));
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> findAll() {
        return projectRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse findById(Long id) { return toResponse(getProject(id)); }

    public ProjectResponse update(Long id, ProjectRequest request) {
        Project project = getProject(id);
        if (projectRepository.existsByNameIgnoreCase(request.name().trim())
                && !project.getName().equalsIgnoreCase(request.name().trim())) {
            throw new ConflictException("A project with this name already exists");
        }
        project.setName(request.name().trim());
        project.setDescription(request.description());
        return toResponse(projectRepository.save(project));
    }

    public void delete(Long id) {
        Project project = getProject(id);
        if (taskRepository.existsByProject_Id(id)) {
            throw new ConflictException("Cannot delete a project that still has tasks. Delete its tasks first.");
        }
        projectRepository.delete(project);
    }

    public Project getProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + id));
    }

    private ProjectResponse toResponse(Project p) {
        return new ProjectResponse(p.getId(), p.getName(), p.getDescription(), p.getCreatedAt());
    }
}
