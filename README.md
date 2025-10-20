
## Project Title
Task Manager API: Kubernetes Deployment with Dynamic Job Creation.

## Simple overview of use/purpose
This project implements a Task Management REST API in Java Spring Boot that manages "Task" objects stored in MongoDB. The primary purpose is to demonstrate cloud-native deployment and programmatic interaction with the Kubernetes API by dynamically creating a disposable Pod/Job to execute a shell command whenever a task is run.

## Description 
The Task Manager API is a core application built for technical assessment, providing full CRUD operations for scheduling shell commands. It has been containerized and deployed to a local Minikube cluster using the Docker driver and Helm for database management. The solution fulfills the stringent requirement of Task 2, where the execution endpoint (PUT /api/tasks/{id}/execute) utilizes the Fabric8 Kubernetes Client to generate a new busybox Job within the cluster, replacing the traditional local shell execution. This entire process validates skills in Java microservices, Docker, Kubernetes configuration, persistence, and RBAC setup.
## Getting Started
**Dependencies**

Operating System: Windows 10/11 (with WSL2 enabled).

Java: Java Development Kit (JDK) 17 or higher.

Build Tool: Maven (using the mvnw wrapper).

Containerization: Docker Desktop (must be running and stable).

Kubernetes: Minikube (configured with --driver=docker).

Package Manager: Helm (version 3+).

**Installing / Deploying**

The entire stack is deployed via commands executed from the project root directory.

**Clone the Repository**:


```bash
https://github.com/RgRohitRGR/task-manager-k8s-deployment
cd task-manager-k8s-deployment
```

**Start Kubernetes Cluster**:
```bash
minikube start --driver=docker
```


**Build Application Image:** 

The project uses a multi-stage Dockerfile to build the JAR and package the final image.
```bash
.\mvnw.cmd clean install
docker build -t task-manager-app:v1 .
minikube image load task-manager-app:v1
```

**Deploy MongoDB (with Persistence):**

This sets the necessary credentials and persistence.
```bash
export MONGO_PASSWORD="mysecretpassword"
helm install mongo-release bitnami/mongodb --set persistence.enabled=true --set auth.rootPassword=$MONGO_PASSWORD --wait
```

**Deploy Application:**
Apply all Kubernetes manifests (RBAC, Deployment, Service).
```bash
kubectl apply -f kubernetes/
```
**Executing Program / API Usage:**
To test the application, you must establish a local network tunnel and use PowerShell's **Invoke-RestMethod**.

**Start the Local Port-Forward:** (Keep this running in a separate terminal)
```bash
kubectl port-forward deployment/task-app-deployment 8080:8080
```
**Create a Task (Saves to MongoDB):**
```bash
PowerShellInvoke-RestMethod -Uri http://localhost:8080/api/tasks -Method PUT -Headers @{"Content-Type"="application/json"} -Body '{ "id": "TEST_JOB", "name": "Dynamic Job Test", "owner": "User", "command": "echo \"Hello from Kubernetes!\"" }'
```
**Execute the Task (Triggers K8s Job Creation):**
```bash
Invoke-RestMethod -Uri http://localhost:8080/api/tasks/TEST_JOB/execute -Method PUT
```
**Verify Kubernetes Job Creation (Proof of Task 2):**
```bash
kubectl get jobs
kubectl get pods -l job-name
```

**API Endpoints**
The base path for all Task operations is **/api/tasks**.

    | Method |       EndPoint        | Core Logic |
    | :----- |       :------:        | -------: |
    |   PUT  |       /api/tasks      | Create/UpdateTask(Requires JSON Body).  |
    |   GET  |       /api/tasks      | Retrieve all Tasks.  |
    |   PUT  |/api/tasks/{id}/execute| Programmatically creates a busybox Job in K8s. |
              

## Help

**Common Issues & Solutions**
Error: Unable to connect to the remote server **(API):** Ensure the kubectl port-forward command is running in a separate terminal and has not crashed.

**Error:** ImagePullBackOff: This is fixed by imagePullPolicy: Never in the deployment YAML.

**Error:** Authentication failed (MongoDB): This is fixed by setting authSource=admin in the deployment YAML, matching the password set during the Helm install.

**Error:** Invoke-RestMethod is not recognized: You are using the wrong shell. Switch to PowerShell.

## Authors

R G ROHIT
Contact: +91 9019533859, rgrrohit16@gmail.com

 https://github.com/RgRohitRGR

## Version History

**v1.0.1 -** Finalized Kubernetes Client configuration and persistent storage for deployment environment.

**v1.0.0 -** Initial implementation of Java REST API with MongoDB integration and simulated K8s execution.