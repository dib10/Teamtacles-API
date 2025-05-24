package com.teamtacles.teamtacles_api.dto.response;

import java.util.List;

import com.teamtacles.teamtacles_api.model.Project;
import com.teamtacles.teamtacles_api.model.Role;
import com.teamtacles.teamtacles_api.model.Task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserResponseDTO{
    private Long userId;
    private String userName;
    private String email;
    private String password;
    private Role role;
    private List<Task> task;
    private List<Project> createdProjects;
    private List<Project> projects;
}