package com.teamtacles.teamtacles_api.service;

import org.springframework.stereotype.Service;

import com.teamtacles.teamtacles_api.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;  
    
}
