package com.teamtacles.teamtacles_api.service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import com.teamtacles.teamtacles_api.dto.page.PagedResponse;
import com.teamtacles.teamtacles_api.dto.request.TaskRequestDTO;
import com.teamtacles.teamtacles_api.dto.request.TaskRequestPatchDTO;
import com.teamtacles.teamtacles_api.dto.response.ProjectResponseDTO;
import com.teamtacles.teamtacles_api.dto.response.TaskResponseDTO;
import com.teamtacles.teamtacles_api.dto.response.TaskResponseFilteredDTO;
import com.teamtacles.teamtacles_api.exception.ResourceNotFoundException;
import com.teamtacles.teamtacles_api.mapper.PagedResponseMapper;
import com.teamtacles.teamtacles_api.model.Project;
import com.teamtacles.teamtacles_api.model.Task;
import com.teamtacles.teamtacles_api.model.User;
import com.teamtacles.teamtacles_api.model.enums.ERole;
import com.teamtacles.teamtacles_api.model.enums.Status;
import com.teamtacles.teamtacles_api.repository.ProjectRepository;
import com.teamtacles.teamtacles_api.repository.TaskRepository;
import com.teamtacles.teamtacles_api.repository.UserRepository;
import com.teamtacles.teamtacles_api.exception.InvalidTaskStateException;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.modelmapper.ModelMapper;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;
    private final PagedResponseMapper pagedResponseMapper;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, ProjectRepository projectRepository, ModelMapper modelMapper, PagedResponseMapper pagedResponseMapper){
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.modelMapper = modelMapper;
        this.pagedResponseMapper = pagedResponseMapper;
    }

    // post
    public TaskResponseDTO createTask(Long id_project, TaskRequestDTO taskRequestDTO, User userFromToken) {        
        Project project = findprojects(id_project);
        User creatorUser = findUsers(userFromToken.getUserId());
        List<User> usersResponsability = new ArrayList<>();
        
        for (Long userId : taskRequestDTO.getUsersResponsability()) {
            usersResponsability.add(findUsers(userId));
        }
        
        Task convertedTask = modelMapper.map(taskRequestDTO, Task.class);
        convertedTask.setProject(project);
        convertedTask.setOwner(creatorUser);
        convertedTask.setStatus(Status.TODO);
        convertedTask.setUsersResponsability(usersResponsability);

        Task createdTask = taskRepository.save(convertedTask);
        return modelMapper.map(createdTask, TaskResponseDTO.class);
	}

    // get task by id
    public TaskResponseDTO getTasksById(Long id_project, Long id_task, User userFromToken){
        Task task = taskRepository.findById(id_task)
                .orElseThrow(() -> new ResourceNotFoundException("Task Not Found."));

        ensureProjectMatchesTask(task, id_project);

        // Chama o método que verifica se o usuário é dono da tarefa ou se é um administrador
        ensureUserCanModifyTask(task, userFromToken);
        
        return modelMapper.map(task, TaskResponseDTO.class);
    }
    
    public PagedResponse<TaskResponseDTO> getAllTasksFromUserInProject(Pageable pageable, Long projectId, Long userId, User userFromToken) {
        if (!isADM(userFromToken)) {
            throw new AccessDeniedException("Access Forbidden");
        }
        // Verifica se o projeto existe
        projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found."));

        // Verifica se o usuário existe
        userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        Page<Task> tasksPage = taskRepository
            .findByProjectIdAndUsersResponsabilityId(projectId, userId, pageable);

        return pagedResponseMapper.toPagedResponse(tasksPage, TaskResponseDTO.class);
    }

    public PagedResponse<TaskResponseFilteredDTO> getAllTasksFiltered(String status, LocalDateTime dueDate, Long projectId, Pageable pageable, User userFromToken){        
        Status statusEnum = transformStatusToEnum(status);

        if(projectId != null){
            projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found."));
        }

        Page<Task> tasksList;
        if(isADM(userFromToken)){
            tasksList = taskRepository.findTasksFiltered(statusEnum, dueDate, projectId, pageable);
        }
        else{
            tasksList = taskRepository.findTasksFilteredByUser(statusEnum, dueDate, projectId, userFromToken.getUserId(), pageable);
        }
        return pagedResponseMapper.toPagedResponse(tasksList, TaskResponseFilteredDTO.class);
    }
    
    // put
    public TaskResponseDTO updateTask(Long id_project, Long id_task, TaskRequestDTO taskRequestDTO, User userFromToken) {
        Task task = taskRepository.findById(id_task)
            .orElseThrow(() -> new ResourceNotFoundException("Task Not Found."));
        // Chama o método que verifica se o usuário é dono da tarefa ou se é um administrador
        ensureProjectMatchesTask(task, id_project);
        ensureUserCanModifyTask(task, userFromToken);

        List<User> usersResponsability = new ArrayList<>();
        
        for (Long userId : taskRequestDTO.getUsersResponsability()) {
            usersResponsability.add(findUsers(userId));
        }

        modelMapper.map(taskRequestDTO, task);
        task.setId(id_task);
        task.setProject(task.getProject());
        task.setOwner(task.getOwner());
        task.setUsersResponsability(usersResponsability);
        Task updated = taskRepository.save(task);
        
        return modelMapper.map(updated, TaskResponseDTO.class);
    }

    // patch 
    public TaskResponseDTO updateStatus(Long id_project, Long id_task, TaskRequestPatchDTO taskRequestPatchDTO, User userFromToken){
        Task task = taskRepository.findById(id_task)
            .orElseThrow(() -> new ResourceNotFoundException("Task Not Found."));
        
        ensureProjectMatchesTask(task, id_project);
        ensureUserCanModifyTask(task, userFromToken);

        taskRequestPatchDTO.getStatus().ifPresent(task::setStatus);
        task.setId(id_task);
        task.setOwner(task.getOwner());
        
        Task taskUpdated = taskRepository.save(task);
        return modelMapper.map(taskUpdated, TaskResponseDTO.class);
    }

    // delete
    public void deleteTask(Long id_project, Long id_task, User userFromToken) {
        Task task = taskRepository.findById(id_task)
            .orElseThrow(() -> new ResourceNotFoundException("Task Not Found."));
            // Chama o método que verifica se o usuário é dono da tarefa ou se é um administrador
        ensureProjectMatchesTask(task, id_project);
        ensureUserCanModifyTask(task, userFromToken);

        taskRepository.delete(task);
    }
    
    private User findUsers(Long id_task){
        User user = userRepository.findById(id_task).orElseThrow(() -> new ResourceNotFoundException("User Not Found."));
        return user;
    }

    private Project findprojects(Long id_project){
        Project project = projectRepository.findById(id_project).orElseThrow(() -> new ResourceNotFoundException("Project Not Found."));        
        return project;
    }


    // Métodos para auxiliar
    // Verificando se o usuário é admin
    private boolean isADM(User user) {
        return user.getRoles().stream().anyMatch(role -> role.getRoleName().equals(ERole.ADMIN));
    }

    // Validando se o usuário é dono do projeto, se ele não for adm/responsavel, ele não consegue criar, editar ou deletar tarefas de outros usuários
    private void ensureUserCanModifyTask(Task task, User user) {
        boolean isResposible = task.getUsersResponsability().stream() 
            .anyMatch(resposible -> resposible.getUserId().equals(user.getUserId())); //verifica se o usuário é responsável pela tarefa

        if(!isADM(user) && !task.getOwner().getUserId().equals(user.getUserId()) && !isResposible) {
            throw new InvalidTaskStateException ("You do not have permission to modify this task."); 
        }
    }
    
    // Verifica se o projeto da tarefa corresponde ao projeto fornecido
    private void ensureProjectMatchesTask(Task task, Long id_project){
        if(!(task.getProject().getId() == id_project)){
            throw new ResourceNotFoundException("Task does not belong to the specified project.");
        }
    }

    private Status transformStatusToEnum(String status){
        if(status != null && !status.isEmpty()){
            try{
                return Status.valueOf(status);
            } catch(IllegalArgumentException ex){
                throw new IllegalArgumentException("Invalid status value: " + status);
            }
        } 
        return null;
    }
}
