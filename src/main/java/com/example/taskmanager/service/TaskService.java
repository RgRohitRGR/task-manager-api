package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskExecution;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.KubernetesClientException; // Needed to catch K8s API errors

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service 
public class TaskService {
    
    // --- KUBERNETES CLIENT INITIALIZATION ---
    // The client automatically picks up the Service Account token inside the Pod
    private final KubernetesClient kubernetesClient = new KubernetesClientBuilder().build();
    private final String namespace = "default"; // Assuming deployment is in the default namespace

    private final TaskRepository taskRepository;

    // Spring injects the Repository instance automatically through the constructor
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // --- Core Data Operations (UNCHANGED) ---

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Optional<Task> findById(String id) {
        return taskRepository.findById(id);
    }

    public Task save(Task task) {
        if (!isCommandSafe(task.getCommand())) {
            throw new IllegalArgumentException("Command validation failed: command contains unsafe shell characters.");
        }
        return taskRepository.save(task);
    }

    public void deleteById(String id) {
        taskRepository.deleteById(id);
    }
    
    public List<Task> findByNamePart(String namePart) {
        return taskRepository.findByNameContainingIgnoreCase(namePart);
    }

    // --- Execution Logic (PUT /api/tasks/{id}/execute) ---
    
    /**
     * Executes the shell command by creating a Kubernetes Job/Pod.
     */
    public Optional<Task> executeTask(String id) {
        return taskRepository.findById(id).map(task -> {
            
            // 1. Initialize TaskExecution object
            TaskExecution execution = new TaskExecution();
            execution.setStartTime(Instant.now());

            try {
                // 2. Define the Job specification
                Job job = new JobBuilder()
                        .withNewMetadata()
                            // Create a unique, identifiable Job name
                            .withGenerateName("task-runner-" + task.getId().toLowerCase() + "-")
                        .endMetadata()
                        .withNewSpec()
                            // Clean up Job resources after 5 minutes
                            .withTtlSecondsAfterFinished(300) 
                            .withNewTemplate()
                                .withNewSpec()
                                    .addNewContainer()
                                        .withName("command-executor")
                                        .withImage("busybox") // Use busybox as requested
                                        // Execute command via shell
                                        .withCommand("/bin/sh", "-c")
                                        .withArgs(task.getCommand()) 
                                    .endContainer()
                                    // Ensure Pod terminates after command completion
                                    .withRestartPolicy("OnFailure") 
                                .endSpec()
                            .endTemplate()
                        .endSpec()
                        .build();

                // 3. Create the Job in Kubernetes (CORE TASK)
                Job createdJob = kubernetesClient.batch().v1().jobs().inNamespace(namespace).resource(job).create();

                // 4. Record the submission details (Success does not mean command is done)
                execution.setEndTime(Instant.now());
                execution.setOutput("K8s Job submitted successfully. Name: " + createdJob.getMetadata().getName());
                
                // **Crucial for debugging the final silent failure:**
                System.out.println("K8s Job submitted: " + createdJob.getMetadata().getName());

            } catch (KubernetesClientException e) {
                // Catches K8s-specific API errors (like 403 Forbidden)
                e.printStackTrace(); // Log the full stack trace to see the exact error
                
                execution.setEndTime(Instant.now());
                execution.setOutput("K8s API Failure (Check logs): " + e.getMessage());
            } catch (Exception e) {
                // Catches other internal exceptions
                e.printStackTrace(); 
                execution.setEndTime(Instant.now());
                execution.setOutput("Internal Error during Job Creation: " + e.getMessage());
            }
            
            // 5. Save the updated Task back to MongoDB
            task.getTaskExecutions().add(execution); 
            return taskRepository.save(task); 
        });
    }

    // --- Auxiliary/Private Methods (Simulation removed, Security remains) ---

    /**
     * SECURITY VALIDATION: Simple check to block dangerous shell characters/commands.
     */
    private boolean isCommandSafe(String command) {
        // Blocking command chaining (;, &&, ||) and destructive commands (sudo, rm -rf).
    	// FINAL CODE CHANGE VERIFICATION LINE
        return !(command.contains(";") ||
                 command.contains("&&") ||
                 command.contains("||") ||
                 command.contains("sudo") ||
                 command.contains("rm -rf"));
    }
}