package com.symphonyai.tasktracker.service;

import com.symphonyai.tasktracker.config.PaginationProperties;
import com.symphonyai.tasktracker.config.TaskProperties;
import com.symphonyai.tasktracker.dto.TaskRequest;
import com.symphonyai.tasktracker.exception.ResourceNotFoundException;
import com.symphonyai.tasktracker.model.*;
import com.symphonyai.tasktracker.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock TaskRepository taskRepository;
    @Mock ProjectService projectService;
    @Mock UserService userService;
    @Mock TaskProperties taskProperties;
    @Mock PaginationProperties paginationProperties;
    @InjectMocks TaskService taskService;

    @Test
    void createRequiresExistingProject() {
        when(projectService.getProject(7L)).thenThrow(new ResourceNotFoundException("Project not found with id 7"));
        assertThrows(ResourceNotFoundException.class, () -> taskService.create(
                new TaskRequest("Write tests", null, null, 7L, null, null)));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void findByIdThrowsWhenTaskMissing() {
        when(taskRepository.findById(123L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> taskService.findById(123L));
    }
}
