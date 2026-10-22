package com.symphonyai.tasktracker.service;

import com.symphonyai.tasktracker.dto.ProjectRequest;
import com.symphonyai.tasktracker.dto.ProjectResponse;
import com.symphonyai.tasktracker.exception.ConflictException;
import com.symphonyai.tasktracker.model.Project;
import com.symphonyai.tasktracker.repository.ProjectRepository;
import com.symphonyai.tasktracker.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock ProjectRepository projectRepository;
    @Mock TaskRepository taskRepository;
    @InjectMocks ProjectService projectService;

    @Test
    void createSavesProjectWhenNameIsUnique() {
        when(projectRepository.existsByNameIgnoreCase("Payments")).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectResponse response = projectService.create(new ProjectRequest("Payments", "API"));

        assertEquals("Payments", response.name());
        assertEquals("API", response.description());
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void createRejectsDuplicateName() {
        when(projectRepository.existsByNameIgnoreCase("Payments")).thenReturn(true);
        assertThrows(ConflictException.class,
                () -> projectService.create(new ProjectRequest("Payments", null)));
        verify(projectRepository, never()).save(any());
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> projectService.findById(99L));
    }
}
