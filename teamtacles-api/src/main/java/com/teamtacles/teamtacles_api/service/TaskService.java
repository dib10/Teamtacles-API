package com.teamtacles.teamtacles_api.service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.teamtacles.teamtacles_api.dto.page.PagedResponse;
import com.teamtacles.teamtacles_api.dto.request.TaskRequestDTO;
import com.teamtacles.teamtacles_api.dto.response.TaskResponseDTO;
import com.teamtacles.teamtacles_api.mapper.PagedResponseMapper;
import com.teamtacles.teamtacles_api.model.Task;
import com.teamtacles.teamtacles_api.model.User;
import com.teamtacles.teamtacles_api.model.enums.Status;
import com.teamtacles.teamtacles_api.repository.TaskRepository;
import com.teamtacles.teamtacles_api.repository.UserRepository;

import java.util.Comparator;

import org.modelmapper.ModelMapper;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PagedResponseMapper pagedResponseMapper;


    public TaskService(TaskRepository taskRepository, UserRepository userRepository, ModelMapper modelMapper, PagedResponseMapper pagedResponseMapper){
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.pagedResponseMapper = pagedResponseMapper;
    }

    // post
    public TaskResponseDTO createTask(TaskRequestDTO taskRequestDTO) {
        User creatorUser = findUsers(1L);

		Task taskCreated = taskRepository.save(modelMapper.map(taskRequestDTO, Task.class));
        taskCreated.setOwner(creatorUser);

        return modelMapper.map(taskCreated, TaskResponseDTO.class);
	}

    // get all
    public TaskResponseDTO getAllTasks(Pageable pageable){
        Page<Task> tasks = taskRepository.findAll(pageable);
        return modelMapper.map(tasks, TaskResponseDTO.class);
    }

    // get task by id
    public TaskResponseDTO getTasksById(Long id){
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException());
        
        return modelMapper.map(task, TaskResponseDTO.class);
    }

    public PagedResponse<TaskResponseDTO> getAllTasksByStatus(Pageable pageable, String status){
        Status statusEnum = Status.valueOf(status);
        Page<Task> tasks = taskRepository.findByStatus(statusEnum, pageable);
        return pagedResponseMapper.toPagedResponse(tasks, TaskResponseDTO.class);
    }

    // put
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO taskRequestDTO){
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException());

        modelMapper.map(taskRequestDTO, task);
        task.setId(id);
        task.setProject(task.getProject());
        task.setOwner(task.getOwner());
        task.setUsersResponsability(task.getUsersResponsability());

        Task updated = taskRepository.save(task);
        return modelMapper.map(updated, TaskResponseDTO.class);
    }

    // delete
    public void deleteTask(Long id){
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException());

        taskRepository.delete(task);
    }
    
    private User findUsers(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException());
        return user;
    }

}
