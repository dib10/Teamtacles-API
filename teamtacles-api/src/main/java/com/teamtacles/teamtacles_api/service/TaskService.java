package com.teamtacles.teamtacles_api.service;

import org.springframework.stereotype.Service;

import com.teamtacles.teamtacles_api.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class TaskService {

    private TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }
    
}
