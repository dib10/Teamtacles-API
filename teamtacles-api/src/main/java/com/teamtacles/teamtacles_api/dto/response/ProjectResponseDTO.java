package com.teamtacles.teamtacles_api.dto.response;

import java.util.List;

import com.teamtacles.teamtacles_api.model.Task;
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
    private Long id;
    private List<Task> tasks;
    private User creator;
    private List<User> team;
    private String title;
    private String description;
}
