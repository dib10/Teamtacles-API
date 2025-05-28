package com.teamtacles.teamtacles_api.service;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.teamtacles.teamtacles_api.dto.page.PagedResponse;
import com.teamtacles.teamtacles_api.dto.request.ProjectRequestDTO;
import com.teamtacles.teamtacles_api.dto.request.ProjectRequestPatchDTO;
import com.teamtacles.teamtacles_api.dto.response.ProjectResponseDTO;
import com.teamtacles.teamtacles_api.exception.InvalidTaskStateException;
import com.teamtacles.teamtacles_api.exception.ResourceNotFoundException;
import com.teamtacles.teamtacles_api.mapper.PagedResponseMapper;
import com.teamtacles.teamtacles_api.model.Project;
import com.teamtacles.teamtacles_api.model.Task;
import com.teamtacles.teamtacles_api.model.User;
import com.teamtacles.teamtacles_api.model.UserAuthenticated;
import com.teamtacles.teamtacles_api.model.enums.ERole;
import com.teamtacles.teamtacles_api.repository.ProjectRepository;
import com.teamtacles.teamtacles_api.repository.UserRepository;
import java.util.List;
import java.util.ArrayList;

@Service
public class ProjectService {
    
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PagedResponseMapper pagedResponseMapper;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository, ModelMapper modelMapper, PagedResponseMapper pagedResponseMapper){
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.pagedResponseMapper = pagedResponseMapper;
    }

    public PagedResponse<ProjectResponseDTO> getAllProjects(Pageable pageable, User userFromToken){
        Page<Project> projectsPage;

        // se for ADM -> Listar tudo
        if(isADM(userFromToken)){
            projectsPage = projectRepository.findAll(pageable);
        }
        // Lista projetos em que eu participo
        else{
            projectsPage = projectRepository.findByTeam(userFromToken, pageable);
        }

        return pagedResponseMapper.toPagedResponse(projectsPage, ProjectResponseDTO.class);

    }

    public ProjectResponseDTO getProjectById(@PathVariable Long id, User userFromToken){
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("project Not found."));
            // Chama o método que verifica se o usuário é dono da tarefa ou se é um administrador
        ensureUserCanModifyTask(project, userFromToken);

        return modelMapper.map(project, ProjectResponseDTO.class);
    }

    // post
    public ProjectResponseDTO createProject(ProjectRequestDTO projectRequestDTO, User userFromToken){
        // Busca o usuário criador do projeto
        User creatorUser = findUsers(userFromToken.getUserId());
        List<User> team = new ArrayList<>();
        team.add(userFromToken); //associa o usuário criador do projeto ao time

        // Busca todos os usuários do time iterativamente
        for (Long userId : projectRequestDTO.getTeam()) {
            team.add(findUsers(userId));
        }
        
        Project convertedProject = modelMapper.map(projectRequestDTO, Project.class);

        convertedProject.setCreator(creatorUser);
        convertedProject.setTeam(team);

        Project projectCreated = projectRepository.save(convertedProject);
        return modelMapper.map(projectCreated, ProjectResponseDTO.class);
    }

    // put
    public ProjectResponseDTO updateProject(Long id, ProjectRequestDTO projectRequestDTO, User userFromToken){
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("project Not found."));
            // Chama o método que verifica se o usuário é dono da tarefa ou se é um administrador
        ensureUserCanModifyTask(project, userFromToken);

        modelMapper.map(projectRequestDTO, project);
        project.setId(id);
        project.setTeam(project.getTeam()); // preserva o time
        project.setCreator(project.getCreator()); // preserva o creator

        Project updatedProject = projectRepository.save(project);
        return modelMapper.map(updatedProject, ProjectResponseDTO.class);
    }

    // patch
    public ProjectResponseDTO partialUpdateProject(Long id, ProjectRequestPatchDTO projectRequestPatchDTO, User userFromToken){
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("project Not found."));
            // Chama o método que verifica se o usuário é dono da tarefa ou se é um administrador
        ensureUserCanModifyTask(project, userFromToken);

        modelMapper.map(projectRequestPatchDTO, project);
        project.setId(id);
        project.setTeam(project.getTeam()); // preserva o time
        project.setCreator(project.getCreator()); // preserva o creator

        Project updatedProject = projectRepository.save(project);
        return modelMapper.map(updatedProject, ProjectResponseDTO.class);
    }

    // delete
    public void deleteProject(Long id, User userFromToken){
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("project Not found."));
            // Chama o método que verifica se o usuário é dono da tarefa ou se é um administrador
        ensureUserCanModifyTask(project, userFromToken);
        
        projectRepository.delete(project);
    }

    private User findUsers(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("user Not found."));
        return user;
    }

    // Métodos para auxiliar

    // Verificando se o usuário é admin
    private boolean isADM(User user) {
        return user.getRoles().stream().anyMatch(role -> role.getRoleName().equals(ERole.ADMIN));
    }

    // Validando se o usuário é dono do projeto, se ele não for adm, ele não consegue criar, editar ou deletar tarefas de outros usuários
    private void ensureUserCanModifyTask(Project project, User user) {
        if(!isADM(user) && !project.getId().equals(user.getUserId())) {
            throw new InvalidTaskStateException (" You do not have permission to modify this project."); 
        }
    }
}
