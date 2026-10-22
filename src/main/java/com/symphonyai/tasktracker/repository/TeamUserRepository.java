package com.symphonyai.tasktracker.repository;

import com.symphonyai.tasktracker.model.TeamUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {
    boolean existsByEmailIgnoreCase(String email);
}
