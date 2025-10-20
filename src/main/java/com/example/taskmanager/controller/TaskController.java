package com.example.taskmanager.controller;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod; // Import required for explicit methods

import java.util.List;

// -----------------------------------------------------------
// 1. CORS FIX: Explicitly allow GET, PUT, and DELETE methods.
// -----------------------------------------------------------
@CrossOrigin(origins = "http://localhost:5173", 
            methods = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE}) 
@RestController
@RequestMapping("/api/tasks") // All endpoints start with /api/tasks
public class TaskController {

    private final TaskService taskService;

    // Spring injects the TaskService here
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // 1. GET tasks (all or by ID)
    // URL: GET http://localhost:8080/api/tasks?id={id} OR GET http://localhost:8080/api/tasks
    @GetMapping
    public ResponseEntity<?> getTasks(@RequestParam(required = false) String id) {
        if (id != null) {
            // Find single task by ID
            return taskService.findById(id)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task ID not found.")); // 404
        }
        // Return all tasks
        return ResponseEntity.ok(taskService.findAll()); // 200 OK
    }

    // 2. PUT a task (Create/Update)
    // URL: PUT http://localhost:8080/api/tasks (JSON body required)
    @PutMapping
    public ResponseEntity<?> saveTask(@RequestBody Task task) {
        try {
            Task savedTask = taskService.save(task);
            return new ResponseEntity<>(savedTask, HttpStatus.CREATED); // 201 Created status
        } catch (IllegalArgumentException e) {
            // Handle the security failure thrown by the Service layer
            return ResponseEntity.badRequest().body(e.getMessage()); // 400 Bad Request
        }
    }

    // 3. DELETE a task
    // URL: DELETE http://localhost:8080/api/tasks/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        taskService.deleteById(id);
        return ResponseEntity.noContent().build(); // 204 No Content (Successful delete)
    }

    // 4. GET (find) tasks by name
    // URL: GET http://localhost:8080/api/tasks/search?name={searchString}
    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchTasksByName(@RequestParam String name) {
        List<Task> tasks = taskService.findByNamePart(name);
        if (tasks.isEmpty()) {
            return ResponseEntity.notFound().build(); // 404 if no results found
        }
        return ResponseEntity.ok(tasks); // 200 OK
    }

    // 5. PUT a TaskExecution (Execute Command)
    // URL: PUT http://localhost:8080/api/tasks/{id}/execute
    @PutMapping("/{id}/execute")
    public ResponseEntity<?> executeTask(@PathVariable String id) {
        return taskService.executeTask(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task ID not found for execution.")); // 404
    }
}