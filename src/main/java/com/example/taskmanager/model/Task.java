package com.example.taskmanager.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "tasks")
public class Task {
    
    @Id
    private String id;
    private String name;
    private String owner;
    private String command;
    private List<TaskExecution> taskExecutions = new ArrayList<>();

    // --- Manually Added Getters and Setters (Replaces @Data) ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }

    // CRITICAL: Needed for TaskService.save and TaskService.simulateK8sExecution
    public String getCommand() { return command; } 
    public void setCommand(String command) { this.command = command; }

    // CRITICAL: Needed for TaskService.executeTask to add new execution
    public List<TaskExecution> getTaskExecutions() { return taskExecutions; }
    public void setTaskExecutions(List<TaskExecution> taskExecutions) { this.taskExecutions = taskExecutions; }

    // You may also need a default constructor for Spring/MongoDB
    public Task() {}
}
