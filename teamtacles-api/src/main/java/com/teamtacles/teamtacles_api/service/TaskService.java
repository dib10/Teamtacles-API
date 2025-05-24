package com.teamtacles.teamtacles_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.teamtacles.teamtacles_api.dto.request.TaskRequestDTO;
import com.teamtacles.teamtacles_api.dto.response.TaskResponseDTO;
import com.teamtacles.teamtacles_api.model.Task;
import com.teamtacles.teamtacles_api.repository.TaskRepository;

import org.modelmapper.ModelMapper;

@Service
public class TaskService {

    private TaskRepository taskRepository;
    private ModelMapper modelMapper;

    public TaskService(TaskRepository taskRepository, ModelMapper modelMapper){
        this.taskRepository = taskRepository;
        this.modelMapper = modelMapper;
    }

    // post
    public TaskResponseDTO createTask(TaskRequestDTO taskRequestDTO) {
		Task taskCreated = taskRepository.save(modelMapper.map(taskRequestDTO, Task.class));
        return modelMapper.map(taskCreated, TaskResponseDTO.class);
	}

    // get all
    public TaskResponseDTO getAllTasks(Pageable pageable){
        Page<Task> tasks = taskRepository.findAll(pageable);
        return modelMapper.map(tasks, TaskResponseDTO.class);
    }
    
}
