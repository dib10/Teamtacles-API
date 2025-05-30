package com.teamtacles.teamtacles_api.dto.response;

import com.teamtacles.teamtacles_api.model.enums.ERole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class RoleResponseDTO {
    private Long id;
    private ERole roleName;
    
}
