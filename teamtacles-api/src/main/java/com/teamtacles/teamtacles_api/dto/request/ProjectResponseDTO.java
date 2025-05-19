package com.teamtacles.teamtacles_api.dto.request;

import java.util.List;

import com.teamtacles.teamtacles_api.dto.response.TaskResponseDTO;
import com.teamtacles.teamtacles_api.model.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponseDTO {

    private List<TaskResponseDTO> tasks;

    @NotBlank(message="O Projeto deve ter um dono!")
    private User creator;

    private List<User> team;

    @NotBlank(message="O título não pode estar em branco!")
    @Size(max = 50)
    private String title;

    @Size(max = 50)
    private String description;
}