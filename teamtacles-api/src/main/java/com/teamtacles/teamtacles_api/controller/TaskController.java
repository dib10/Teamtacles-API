package com.teamtacles.teamtacles_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamtacles.teamtacles_api.dto.page.PagedResponse;
import com.teamtacles.teamtacles_api.dto.request.TaskRequestDTO;
import com.teamtacles.teamtacles_api.dto.request.TaskRequestPatchDTO;
import com.teamtacles.teamtacles_api.dto.response.TaskResponseFilteredDTO;
import com.teamtacles.teamtacles_api.dto.response.ProjectResponseDTO;
import com.teamtacles.teamtacles_api.dto.response.TaskResponseDTO;
import com.teamtacles.teamtacles_api.model.UserAuthenticated;
import com.teamtacles.teamtacles_api.service.TaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/project")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }

    @Operation(summary = "Create a new task for a project", description = "Creates a new task associated with a specific project. Only users with appropriate permissions for the project (project creator, team member, or administrator) can create tasks.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Task created successfully, returns the new task details."),
        @ApiResponse(responseCode = "400", description = "Bad Request: Invalid task data provided (missing fields, invalid format)."),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Authentication required or invalid token."),
        @ApiResponse(responseCode = "403", description = "Forbidden: User does not have permission to create tasks in this project."),
        @ApiResponse(responseCode = "404", description = "Not Found: Project with the specified ID was not found."),
        @ApiResponse(responseCode = "500", description = "Internal Server Error: An unexpected error occurred.")
    })
    @PostMapping("/{id_project}/task")
    public ResponseEntity<TaskResponseDTO> createTask(@PathVariable("id_project") @Parameter(description = "Project ID") Long id_project, 
        @Valid @RequestBody @Parameter(description = "Details of the task to be created (title, description, due date).") TaskRequestDTO taskRequestDTO, 
        @Parameter(hidden = true) @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ){
        TaskResponseDTO taskResponseDTO = taskService.createTask(id_project, taskRequestDTO, authenticatedUser.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(taskResponseDTO);
    }

    @Operation(summary = "Get a task by ID", description = "Retrieves a specific task associated with a project by its ID. Users can only view tasks within projects they have access to.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the task details."),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Authentication required or invalid token."),
        @ApiResponse(responseCode = "403", description = "Forbidden: User does not have permission to view tasks in this project."),
        @ApiResponse(responseCode = "404", description = "Not Found: Project or Task with the specified IDs was not found."),
        @ApiResponse(responseCode = "500", description = "Internal Server Error: An unexpected error occurred.")
    })
    @GetMapping("/{id_project}/task/{id_task}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable("id_project") @Parameter(description = "Project ID") Long id_project, 
        @PathVariable("id_task") @Parameter(description = "Project Task") Long id_task,
        @Parameter(hidden = true) @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ){
        return ResponseEntity.ok(taskService.getTasksById(id_project, id_task, authenticatedUser.getUser()));
    }   

    @Operation(summary = "Get tasks assigned to a specific user within a project", description = "Retrieves a paginated list of tasks assigned to a specific user within a given project. Access is typically restricted to administrators.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the paginated list of tasks assigned to the user."),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Authentication required or invalid token."),
        @ApiResponse(responseCode = "403", description = "Forbidden: User does not have permission to view tasks in this project or tasks assigned to this specific user."),
        @ApiResponse(responseCode = "404", description = "Not Found: Project or User with the specified IDs was not found."),
        @ApiResponse(responseCode = "500", description = "Internal Server Error: An unexpected error occurred.")
    })
    @GetMapping("/{projectId}/tasks/user/{userId}")
    public ResponseEntity<PagedResponse<TaskResponseDTO>> getTasksByUserInProject(@PathVariable @Parameter(description = "Project ID") Long projectId, 
        @PathVariable @Parameter(description = "User ID") Long userId, 
        @Parameter(description = "Pagination parameters (page, size, sort).") Pageable pageable,
        @Parameter(hidden = true) @AuthenticationPrincipal UserAuthenticated userFromToken
    ){
        PagedResponse<TaskResponseDTO> response = taskService.getAllTasksFromUserInProject(pageable, projectId, userId, userFromToken.getUser());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Search and filter tasks", description = "Retrieves a paginated and filtered list of tasks based on provided criteria (status, due date, project ID). Users can only search within projects they have access to, or administrators")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the filtered list of tasks."),
        @ApiResponse(responseCode = "400", description = "Bad Request: Invalid query parameters provided (malformed date, invalid status)."),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Authentication required or invalid token."),
        @ApiResponse(responseCode = "403", description = "Forbidden: User does not have permission to view tasks in the specified project(s)."),
        @ApiResponse(responseCode = "500", description = "Internal Server Error: An unexpected error occurred.")
    })
    @GetMapping("/task/search")
    public ResponseEntity<PagedResponse<TaskResponseFilteredDTO>> getAllTasksFiltered(@RequestParam(value = "status", required = false) @Parameter(description = "Filter tasks by status") String status,
        @RequestParam(value = "dueDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Parameter(description = "Filter tasks by dueDate") LocalDateTime dueDate,
        @RequestParam(value = "projectId", required = false) @Parameter(description = "Filter tasks by Project ID") Long projectId,
        @Parameter(description = "Pagination parameters (page, size, sort).") Pageable pageable, 
        @Parameter(hidden = true) @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ){
        PagedResponse tasksPage = taskService.getAllTasksFiltered(status, dueDate, projectId, pageable, authenticatedUser.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(tasksPage);
    }  

    @Operation(summary = "Update task status", description = "Updates the status of a specific task within a project. Only users with appropriate permissions (e.g., project members, task assignees, or administrators) can change a task's status.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task status updated successfully, returns the updated task details."),
        @ApiResponse(responseCode = "400", description = "Bad Request: Invalid status provided or other invalid data for update."),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Authentication required or invalid token."),
        @ApiResponse(responseCode = "403", description = "Forbidden: User does not have permission to update the status of this task or in this project."),
        @ApiResponse(responseCode = "404", description = "Not Found: Project or Task with the specified IDs was not found."),
        @ApiResponse(responseCode = "500", description = "Internal Server Error: An unexpected error occurred.")
    })
    @PatchMapping("/{id_project}/task/{id_task}/updateStatus")
    public ResponseEntity<TaskResponseDTO> updateStatus(@PathVariable("id_project") @Parameter(description = "Project ID") Long id_project, 
        @PathVariable("id_task") @Parameter(description = "Task ID")Long id_task, 
        @Valid @RequestBody @Parameter(description = "Request body containing the new status for the task or other informations.") TaskRequestPatchDTO taskRequestPatchDTO, 
        @Parameter(hidden = true) @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ){
        TaskResponseDTO taskResponseDTO = taskService.updateStatus(id_project, id_task, taskRequestPatchDTO, authenticatedUser.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(taskResponseDTO);
    }

    @Operation(summary = "Update an existing task", description = "Updates all modifiable details of a specific task within a project, identified by its project ID and task ID. Only users with appropriate permissions (project members, task assignees, or administrators) can update a task.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task updated successfully, returns the updated task details."),
        @ApiResponse(responseCode = "400", description = "Bad Request: Invalid task data provided (missing required fields, invalid format)."),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Authentication required or invalid token."),
        @ApiResponse(responseCode = "403", description = "Forbidden: User does not have permission to update this task or in this project."),
        @ApiResponse(responseCode = "404", description = "Not Found: Project or Task with the specified IDs was not found."),
        @ApiResponse(responseCode = "500", description = "Internal Server Error: An unexpected error occurred.")
    })
    @PutMapping("/{id_project}/task/{id_task}")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable("id_project") @Parameter(description = "Project ID") Long id_project, 
        @PathVariable("id_task") @Parameter(description = "Task ID") Long id_task, 
        @Valid @RequestBody @Parameter(description = "Complete updated details for the task.") TaskRequestDTO taskRequestDTO, 
        @Parameter(hidden = true) @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ){
        TaskResponseDTO taskResponseDTO = taskService.updateTask(id_project, id_task, taskRequestDTO, authenticatedUser.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(taskResponseDTO);
    }
    
    @Operation(summary = "Delete a task", description = "Deletes a specific task identified by its project ID and task ID. Only users with appropriate permissions (project members, task assignees, or administrators) can delete a task.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Task deleted successfully (No Content)."),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Authentication required or invalid token."),
        @ApiResponse(responseCode = "403", description = "Forbidden: User does not have permission to delete this task or in this project."),
        @ApiResponse(responseCode = "404", description = "Not Found: Project or Task with the specified IDs was not found."),
        @ApiResponse(responseCode = "500", description = "Internal Server Error: An unexpected error occurred.")
    })
    @DeleteMapping("/{id_project}/task/{id_task}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id_project") @Parameter(description = "Project ID") Long id_project, 
        @PathVariable("id_task") @Parameter(description = "Task ID") Long id_task, 
        @Parameter(hidden = true) @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ){
        taskService.deleteTask(id_project, id_task, authenticatedUser.getUser());
        return ResponseEntity.noContent().build();
    }
}

