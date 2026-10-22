package com.symphonyai.tasktracker.service;

import com.symphonyai.tasktracker.dto.UserRequest;
import com.symphonyai.tasktracker.dto.UserResponse;
import com.symphonyai.tasktracker.exception.ConflictException;
import com.symphonyai.tasktracker.exception.ResourceNotFoundException;
import com.symphonyai.tasktracker.model.TeamUser;
import com.symphonyai.tasktracker.repository.TaskRepository;
import com.symphonyai.tasktracker.repository.TeamUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class UserService {
    private final TeamUserRepository userRepository;
    private final TaskRepository taskRepository;

    public UserService(TeamUserRepository userRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    public UserResponse create(UserRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) throw new ConflictException("A user with this email already exists");
        return toResponse(userRepository.save(new TeamUser(request.name().trim(), email, request.role().trim())));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() { return userRepository.findAll().stream().map(this::toResponse).toList(); }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) { return toResponse(getUser(id)); }

    public UserResponse update(Long id, UserRequest request) {
        TeamUser user = getUser(id);
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email) && !user.getEmail().equalsIgnoreCase(email)) {
            throw new ConflictException("A user with this email already exists");
        }
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setRole(request.role().trim());
        return toResponse(userRepository.save(user));
    }

    public void delete(Long id) {
        TeamUser user = getUser(id);
        if (taskRepository.existsByAssignee_Id(id)) {
            throw new ConflictException("Cannot delete a user who is assigned to tasks. Reassign or unassign tasks first.");
        }
        userRepository.delete(user);
    }

    public TeamUser getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    private UserResponse toResponse(TeamUser u) { return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRole()); }
}
