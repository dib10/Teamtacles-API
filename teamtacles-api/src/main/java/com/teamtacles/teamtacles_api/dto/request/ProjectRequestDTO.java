package com.teamtacles.teamtacles_api.dto.request;

import java.util.ArrayList;
import java.util.List;

import com.teamtacles.teamtacles_api.model.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectRequestDTO {
    @NotBlank(message = "The title must not be blank")
    @Size(max = 50, message = "The title must not exceed 50 characters")
    private String title;

    @Size(max = 50, message = "The description must not exceed 50 characters")
    private String description;

    @NotEmpty(message = "The team must have at least one user")
    private List<Long> team = new ArrayList<>();
}