package com.teamtacles.teamtacles_api.service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.teamtacles.teamtacles_api.dto.page.PagedResponse;
import com.teamtacles.teamtacles_api.dto.request.TaskRequestDTO;
import com.teamtacles.teamtacles_api.dto.response.TaskResponseDTO;
import com.teamtacles.teamtacles_api.exception.ResourceNotFoundException;
import com.teamtacles.teamtacles_api.mapper.PagedResponseMapper;
import com.teamtacles.teamtacles_api.model.Project;
import com.teamtacles.teamtacles_api.model.Task;
import com.teamtacles.teamtacles_api.model.User;
import com.teamtacles.teamtacles_api.model.enums.Status;
import com.teamtacles.teamtacles_api.repository.ProjectRepository;
import com.teamtacles.teamtacles_api.repository.TaskRepository;
import com.teamtacles.teamtacles_api.repository.UserRepository;

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
    public TaskResponseDTO createTask(Long id_project, TaskRequestDTO taskRequestDTO) {
        Project project = projectRepository.findById(id_project)
            .orElseThrow(() -> new ResourceNotFoundException("Project Not Found."));
        
        User creatorUser = findUsers(1L);
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

    // get all
    public TaskResponseDTO getAllTasks(Pageable pageable){
        Page<Task> tasks = taskRepository.findAll(pageable);
        return modelMapper.map(tasks, TaskResponseDTO.class);
    }

    // get task by id
    public TaskResponseDTO getTasksById(Long id){
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task Not Found."));
        
        return modelMapper.map(task, TaskResponseDTO.class);
    }

    /*public PagedResponse<TaskResponseDTO> getAllTasksByStatus(Pageable pageable, String status){
        Status statusEnum = Status.valueOf(status);
        Page<Task> tasks = taskRepository.findByStatus(statusEnum, pageable);
        return pagedResponseMapper.toPagedResponse(tasks, TaskResponseDTO.class);
    }*/

    // put
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO taskRequestDTO){
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task Not Found."));

        List<User> usersResponsability = new ArrayList<>();
        
        for (Long userId : taskRequestDTO.getUsersResponsability()) {
            usersResponsability.add(findUsers(userId));
        }

        modelMapper.map(taskRequestDTO, task);
        task.setId(id);
        task.setProject(task.getProject());
        task.setOwner(task.getOwner());
        task.setUsersResponsability(usersResponsability);

        Task updated = taskRepository.save(task);
        return modelMapper.map(updated, TaskResponseDTO.class);
    }

    // delete
    public void deleteTask(Long id){
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task Not Found."));

        taskRepository.delete(task);
    }
    
    private User findUsers(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User Not Found."));
        return user;
    }
}
