package com.teamtacles.teamtacles_api.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.teamtacles.teamtacles_api.dto.response.ProjectResponseDTO;
import com.teamtacles.teamtacles_api.dto.response.UserResponseDTO;
import com.teamtacles.teamtacles_api.model.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequestPatchDTO {
    private Optional<String> title = Optional.empty();
    private Optional<String> description = Optional.empty();
    private Optional<LocalDateTime> dueDate = Optional.empty();
    private Optional<Status> status = Optional.empty();
    private Optional<UserResponseDTO> owner = Optional.empty();
    private Optional<ProjectResponseDTO> project = Optional.empty();
    private Optional<List<Long>> userId = Optional.empty();
}