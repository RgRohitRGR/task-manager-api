/*package com.example.taskmanager.repository;

public interface TaskRepository {

}*/
//Rohit
package com.example.taskmanager.repository;

import com.example.taskmanager.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

/**
 * By extending MongoRepository<Task, String>, Spring Data automatically provides:
 * - save(), findById(), findAll(), deleteById(), etc.
 * The parameters are: <The model class to manage, The data type of the ID field>.
 */
public interface TaskRepository extends MongoRepository<Task, String> {
    
    /**
     * Custom search method required: GET (find) tasks by name.
     * Spring Data interprets this method name to build the necessary MongoDB query 
     * to perform a case-insensitive search for names containing the provided string.
     */
    List<Task> findByNameContainingIgnoreCase(String name);
}

