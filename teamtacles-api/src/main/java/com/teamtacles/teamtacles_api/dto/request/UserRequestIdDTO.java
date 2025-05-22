package com.teamtacles.teamtacles_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public class UserRequestIdDTO {
    
    @NotBlank(message = "Indique o id do usuário.")
    private Long id;
}
