package com.symphonyai.tasktracker.service;

import com.symphonyai.tasktracker.dto.UserRequest;
import com.symphonyai.tasktracker.exception.ConflictException;
import com.symphonyai.tasktracker.model.TeamUser;
import com.symphonyai.tasktracker.repository.TaskRepository;
import com.symphonyai.tasktracker.repository.TeamUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock TeamUserRepository userRepository;
    @Mock TaskRepository taskRepository;
    @InjectMocks UserService userService;

    @Test
    void createRejectsDuplicateEmail() {
        when(userRepository.existsByEmailIgnoreCase("person@example.com")).thenReturn(true);
        assertThrows(ConflictException.class, () -> userService.create(
                new UserRequest("Person", "person@example.com", "DEVELOPER")));
        verify(userRepository, never()).save(any(TeamUser.class));
    }
}
