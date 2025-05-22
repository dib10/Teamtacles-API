package com.teamtacles.teamtacles_api.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.teamtacles.teamtacles_api.dto.request.ProjectRequestDTO;
import com.teamtacles.teamtacles_api.dto.response.ProjectResponseDTO;
import com.teamtacles.teamtacles_api.model.Project;
import com.teamtacles.teamtacles_api.repository.ProjectRepository;

@Service
public class ProjectService {
    
    private ProjectRepository projectRepository;

    private ModelMapper modelMapper;

    public ProjectService(ProjectRepository projectRepositor, ModelMapper modelMapper){
        this.projectRepository = projectRepository;
        this.modelMapper = modelMapper;
    }

    public ProjectResponseDTO postProject(ProjectRequestDTO projectRequestDTO){
        Project projectCreated = projectRepository.save(modelMapper.map(projectRequestDTO, Project.class));
        return modelMapper.map(projectCreated, ProjectResponseDTO.class);
    }
}
