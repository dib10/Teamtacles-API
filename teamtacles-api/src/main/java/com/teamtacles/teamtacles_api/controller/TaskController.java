package com.teamtacles.teamtacles_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamtacles.teamtacles_api.dto.request.TaskRequestDTO;
import com.teamtacles.teamtacles_api.dto.response.TaskResponseDTO;
import com.teamtacles.teamtacles_api.service.TaskService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<TaskResponseDTO> createTask(@PathVariable("id_project") Long id_project, @Valid @RequestBody TaskRequestDTO taskRequestDTO){
        TaskResponseDTO taskResponseDTO = taskService.createTask(taskRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskResponseDTO);
    }

    @GetMapping("/{id_project}/task/{id_task}")
    public ResponseEntity<TaskResponseDTO> getTasksById(@PathVariable("id_project") Long id_project, @PathVariable("id_task") Long id_task) {
        return ResponseEntity.ok(taskService.getTasksById(id_project));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequestDTO taskRequestDTO){
        TaskResponseDTO taskResponseDTO = taskService.updateTask(id, taskRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(taskResponseDTO);
    }
    
    @DeleteMapping
    public ResponseEntity<Void> deleteTask(@PathVariable Long id){
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

}
