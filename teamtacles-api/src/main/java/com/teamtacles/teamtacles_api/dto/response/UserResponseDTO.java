package com.teamtacles.teamtacles_api.dto.response;

import java.util.List;
import java.util.Set;

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
    private String userName;
    private String email;
    private Set<ERoleResponseDTO> role;
}