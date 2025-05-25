package com.teamtacles.teamtacles_api.dto.request;

import java.util.List;
import java.util.Optional;

import com.teamtacles.teamtacles_api.model.enums.ERole;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ERoleRequestDTO {

    @Pattern(regexp = "^(Admin|User|Leader)$", message = "Role must be ADMIN, USER, or LEADER.")
    private ERole role;
}