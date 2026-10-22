package com.symphonyai.tasktracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TaskPriority priority;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false, foreignKey = @ForeignKey(name = "fk_task_project"))
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id", foreignKey = @ForeignKey(name = "fk_task_assignee"))
    private TeamUser assignee;

    private LocalDate dueDate;

    protected Task() {
    }

    public Task(String title, TaskStatus status, TaskPriority priority, Project project, TeamUser assignee, LocalDate dueDate) {
        this.title = title;
        this.status = status;
        this.priority = priority;
        this.project = project;
        this.assignee = assignee;
        this.dueDate = dueDate;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    public TeamUser getAssignee() { return assignee; }
    public void setAssignee(TeamUser assignee) { this.assignee = assignee; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
}
