package com.teamtacles.teamtacles_api.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.teamtacles.teamtacles_api.dto.page.PagedResponse;
import com.teamtacles.teamtacles_api.dto.request.ProjectRequestDTO;
import com.teamtacles.teamtacles_api.dto.request.ProjectRequestPatchDTO;
import com.teamtacles.teamtacles_api.dto.response.ProjectResponseDTO;
import com.teamtacles.teamtacles_api.model.UserAuthenticated;
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
    public ResponseEntity<ProjectResponseDTO> createProject (@RequestBody @Valid ProjectRequestDTO projectRequestDTO, @AuthenticationPrincipal UserAuthenticated authenticatedUser){
        ProjectResponseDTO projectResponseDTO = projectService.createProject(projectRequestDTO, authenticatedUser.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(projectResponseDTO);
    }

    @GetMapping("/all")
    public ResponseEntity<PagedResponse<ProjectResponseDTO>> getAllProjects(Pageable pageable, @AuthenticationPrincipal UserAuthenticated authenticatedUser){
        PagedResponse projectsPage = projectService.getAllProjects(pageable, authenticatedUser.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(projectsPage);
    }  

    @PutMapping("/{id}") 
    public ResponseEntity<ProjectResponseDTO> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectRequestDTO projectRequestDTO, @AuthenticationPrincipal UserAuthenticated authenticatedUser){
        ProjectResponseDTO responseDTO = projectService.updateProject(id, projectRequestDTO, authenticatedUser.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> partialUpdateProject(@PathVariable Long id, @Valid @RequestBody ProjectRequestPatchDTO projectRequestPatchDTO, @AuthenticationPrincipal UserAuthenticated authenticatedUser){
        ProjectResponseDTO responseDTO = projectService.partialUpdateProject(id, projectRequestPatchDTO, authenticatedUser.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, @AuthenticationPrincipal UserAuthenticated authenticatedUser){
        projectService.deleteProject(id, authenticatedUser.getUser());
        return ResponseEntity.noContent().build();
    }
}