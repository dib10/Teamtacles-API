package com.teamtacles.teamtacles_api.dto.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRegistrationDTO {
    @Size(min = 3, max = 50, message = "O Username deve conter entre 3 e 50 caracteres")
    @NotBlank(message="O nome não pode estar em branco!")
    private String userName;

    @Email(message="O email deve ser válido!")
    @Size(min = 8, max = 50, message = "O email não pode ultrapassar 50 caracteres")
    private String email;

    @Size(min = 5, max = 100, message = "A senha deve conter entre 5 e 100 caracteres") 
    @NotBlank(message="A senha não pode estar em branco!")
    private String password;

    @NotBlank(message="A senha de confirmação não pode estar em branco!")
    private String passwordConfirm;
}
