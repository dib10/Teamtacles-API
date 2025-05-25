package com.teamtacles.teamtacles_api.dto.request;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestDTO {

    @Size(max = 50, message = "The title must not exceed 50 characters")
    @NotBlank(message = "The title must not be blank")
    private String title; 

    @Size(max = 250, message = "The description must not exceed 250 characters")
    private String description;

    @NotBlank(message = "The due date must not be blank")
    @Future(message = "The due date cannot be in the past")
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dueDate;

    private List<Long> usersResponsability = new ArrayList<>();
}