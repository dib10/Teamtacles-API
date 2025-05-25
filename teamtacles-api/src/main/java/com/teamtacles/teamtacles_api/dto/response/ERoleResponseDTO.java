package com.teamtacles.teamtacles_api.dto.response;

import com.teamtacles.teamtacles_api.model.enums.ERole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ERoleResponseDTO {
    private ERole role;
}
