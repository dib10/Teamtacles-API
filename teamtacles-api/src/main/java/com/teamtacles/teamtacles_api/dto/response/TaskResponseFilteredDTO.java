package com.teamtacles.teamtacles_api.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.teamtacles.teamtacles_api.model.User;
import com.teamtacles.teamtacles_api.model.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponseFilteredDTO{
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Status status;
    private UserResponseDTO owner;
    private List<UserResponseDTO> usersResponsability;
    private ProjectResponseFilteredDTO project;
}  