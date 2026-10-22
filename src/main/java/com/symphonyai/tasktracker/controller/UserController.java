package com.symphonyai.tasktracker.controller;

import com.symphonyai.tasktracker.dto.UserRequest;
import com.symphonyai.tasktracker.dto.UserResponse;
import com.symphonyai.tasktracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) { this.userService = userService; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody UserRequest request) { return userService.create(request); }

    @GetMapping
    public List<UserResponse> findAll() { return userService.findAll(); }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable Long id) { return userService.findById(id); }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { userService.delete(id); }
}
