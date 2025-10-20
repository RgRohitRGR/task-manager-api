
## Project Title
Task Manager API: Spring Boot Microservice with Simulated Command Execution

## Simple overview of use/purpose
This project implements a Task Management REST API in Java Spring Boot that manages "Task" objects stored in MongoDB. The core purpose is to provide standard CRUD operations, implement essential security validation, and simulate the execution of shell commands

## Description 
The Task Manager API is a core backend application providing full **CRUD** (Create, Read, Update, Delete) functionality for scheduling executable commands. It utilizes a three-tier architecture:

1. Controller: Exposes the RESTful endpoints.

2. Service: Implements security validation to prevent dangerous inputs and handles the logic for execution.

3. Repository: Persists Task objects and their execution logs to a MongoDB database using Spring Data.

The key endpoint **(PUT /api/tasks/{id}/execute)** fulfills the requirement to execute a command by using a service method **(simulateK8sExecution)** that pauses execution via **Thread.sleep** and generates mock output, acting as a reliable stand-in for a cloud execution process.

## Getting Started
**Dependencies**

Operating System: Windows 10/11 

Java: Java Development Kit (JDK) 17 or higher.

Build Tool: Maven (using the mvnw wrapper).

Database: MongoDB Server(must be running on the default port, localhost:27017).

API Client: Postman or cURL (for testing).

**Installing / Deploying**

The project should be cloned and run from your local machine.

**Clone the Repository**:


```bash
git clone [https://github.com/RgRohitRGR/task-manager-api](https://github.com/RgRohitRGR/task-manager-api) 
cd task-manager-api
```

**Building the application**: Build the executable JAR using the Maven wrapper.
```bash
.\mvnw.cmd clean install
```

**File Modifications:** No modifications are required, as the necessary MongoDB connection string **(mongodb://localhost:27017/taskdb)** is ready in **src/main/resources/application.properties**.


**Executing Program:**

Ensure your MongoDB service is running before attempting to start the application.

**How to run the program:** Execute the Spring Boot application using the Maven wrapper

```bash
.\mvnw.cmd spring-boot:run
```

**Step-by-step API Test (Verification):** Use cURL (or Postman) to verify the core functionality. The server runs on **port 8080**.

**CREATE Task (Test Security and Persistence):**
```bash
curl -X PUT http://localhost:8080/api/tasks -H "Content-Type: application/json" -d "{ \"id\": \"T001\", \"name\": \"Simulated Cleanup\", \"owner\": \"User\", \"command\": \"echo cleaning up logs\" }"
```

**EXECUTE Task (Test Simulation and Logging):**
```bash
curl -X PUT http://localhost:8080/api/tasks/T001/execute
```

**VERIFY Output (Test Persistence):** (The response will show the taskExecutions list populated with startTime, endTime, and mock output)
```bash
curl http://localhost:8080/api/tasks?id=T001
```

## Help

Any advise for common problems or issues:

java.net.ConnectException (MongoDB Error): The MongoDB server is not running. Check the Windows Services Manager and ensure the MongoDB Service is started.

Port 8080 was already in use: Another program (or a previously failed attempt) is holding the port. Terminate the application process via the Windows Task Manager (look for java.exe or javaw.exe).

Compilation Errors: If errors occur despite having the JDK, perform a deep clean in your IDE (Project > Clean...) as the Lombok setup may have left stale binaries.

## Authors

R G ROHIT
Contact: +91 9019533859, rgrrohit16@gmail.com

 https://github.com/RgRohitRGR

## Version History

**v1.0.0 -** Initial implementation of Java REST API with MongoDB integration and simulated K8s execution.