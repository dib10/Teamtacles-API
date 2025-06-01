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

    @PostMapping("/{id_project}/task")
    public ResponseEntity<TaskResponseDTO> createTask(@PathVariable("id_project") Long id_project, @Valid @RequestBody TaskRequestDTO taskRequestDTO, @AuthenticationPrincipal UserAuthenticated authenticatedUser){
        TaskResponseDTO taskResponseDTO = taskService.createTask(id_project, taskRequestDTO, authenticatedUser.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(taskResponseDTO);
    }

    @GetMapping("/{id_project}/task/{id_task}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable("id_project") Long id_project, @PathVariable("id_task") Long id_task, @AuthenticationPrincipal UserAuthenticated authenticatedUser) {
        return ResponseEntity.ok(taskService.getTasksById(id_project, id_task, authenticatedUser.getUser()));
    }   

    @GetMapping("/{projectId}/tasks/user/{userId}")
    public ResponseEntity<PagedResponse<TaskResponseDTO>> getTasksByUserInProject(@PathVariable Long projectId, @PathVariable Long userId, @AuthenticationPrincipal UserAuthenticated userFromToken, Pageable pageable) {
        PagedResponse<TaskResponseDTO> response = taskService.getAllTasksFromUserInProject(pageable, projectId, userId, userFromToken.getUser());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/task/search")
    public ResponseEntity<PagedResponse<TaskResponseFilteredDTO>> getAllTasksFiltered(@RequestParam(value = "status", required = false) String status,
        @RequestParam(value = "dueDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDate,
        @RequestParam(value = "projectId", required = false) Long projectId,
        Pageable pageable, 
        @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ){
        PagedResponse tasksPage = taskService.getAllTasksFiltered(status, dueDate, projectId, pageable, authenticatedUser.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(tasksPage);
    }  

    @PatchMapping("/{id_project}/task/{id_task}/updateStatus")
    public ResponseEntity<TaskResponseDTO> updateStatus(@PathVariable("id_project") Long id_project, @PathVariable("id_task") Long id_task, @Valid @RequestBody TaskRequestPatchDTO taskRequestPatchDTO, @AuthenticationPrincipal UserAuthenticated authenticatedUser){
        TaskResponseDTO taskResponseDTO = taskService.updateStatus(id_project, id_task, taskRequestPatchDTO, authenticatedUser.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(taskResponseDTO);
    }

    @PutMapping("/{id_project}/task/{id_task}")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable("id_project") Long id_project, @PathVariable("id_task") Long id_task, @Valid @RequestBody TaskRequestDTO taskRequestDTO, @AuthenticationPrincipal UserAuthenticated authenticatedUser){
        TaskResponseDTO taskResponseDTO = taskService.updateTask(id_project, id_task, taskRequestDTO, authenticatedUser.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(taskResponseDTO);
    }
    
    @DeleteMapping("/{id_project}/task/{id_task}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id_project") Long id_project, @PathVariable("id_task") Long id_task, @AuthenticationPrincipal UserAuthenticated authenticatedUser){
        taskService.deleteTask(id_project, id_task, authenticatedUser.getUser());
        return ResponseEntity.noContent().build();
    }
}

