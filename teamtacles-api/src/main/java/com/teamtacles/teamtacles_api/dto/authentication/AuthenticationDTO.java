package com.teamtacles.teamtacles_api.dto.authentication;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationDTO {
    @NotBlank(message = "Enter your Username")
    private String userName;
    @NotBlank(message = "Enter your password")
    private String password;
}
