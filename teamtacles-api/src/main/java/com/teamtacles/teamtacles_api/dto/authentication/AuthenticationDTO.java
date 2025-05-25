package com.teamtacles.teamtacles_api.dto.authentication;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthenticationDTO {
    @NotBlank(message = "Por favor, insira o username.")
    private String userName;
    @NotBlank(message = "Por favor, insira sua senha.")
    private String passoword;
}
