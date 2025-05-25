package com.teamtacles.teamtacles_api.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamtacles.teamtacles_api.dto.page.PagedResponse;
import com.teamtacles.teamtacles_api.dto.request.ProjectRequestDTO;
import com.teamtacles.teamtacles_api.dto.request.ProjectRequestPatchDTO;
import com.teamtacles.teamtacles_api.dto.response.ProjectResponseDTO;
import com.teamtacles.teamtacles_api.service.ProjectService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/project")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService){
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectResponseDTO> createProject (@RequestBody @Valid ProjectRequestDTO projectRequestDTO){
        ProjectResponseDTO projectResponseDTO = projectService.createProject(projectRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(projectResponseDTO);
    }

    @GetMapping 
    public ResponseEntity<PagedResponse<ProjectResponseDTO>> getAllProjects(Pageable pageable){
        PagedResponse projectsPage = projectService.getAllProjects(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(projectsPage);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectRequestDTO projectRequestDTO){
        ProjectResponseDTO responseDTO = projectService.updateProject(id, projectRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> partialUpdateProject(@PathVariable Long id, @Valid @RequestBody ProjectRequestPatchDTO projectRequestPatchDTO){
        ProjectResponseDTO responseDTO = projectService.partialUpdateProject(id, projectRequestPatchDTO);
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id){
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}