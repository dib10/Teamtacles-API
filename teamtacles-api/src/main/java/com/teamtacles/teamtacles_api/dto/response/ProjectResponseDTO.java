package com.teamtacles.teamtacles_api.dto.response;

import java.util.List;

import com.teamtacles.teamtacles_api.model.Task;
import com.teamtacles.teamtacles_api.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponseDTO {
    private Long id;
    private String title;
    private String description;
    private List<Task> tasks;
    private User creator;
    private List<User> team;
}
