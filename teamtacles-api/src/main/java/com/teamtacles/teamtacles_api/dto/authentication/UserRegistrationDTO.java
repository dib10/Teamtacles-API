package com.teamtacles.teamtacles_api.dto.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDTO {
    @Size(min = 3, max = 50, message = "The username must be between 3 and 50 characters long")
    @NotBlank(message = "Username must not be blank")
    private String userName;

    @Email(message = "Email must be valid")
    @Size(min = 8, max = 50, message = "Email must not exceed 50 characters")
    private String email;

    @Size(min = 5, max = 100, message = "Password must be between 5 and 100 characters long") 
    @NotBlank(message = "Password must not be blank")
    private String password;

    @NotBlank(message = "Password confirmation must not be blank")
    private String passwordConfirm;
}
