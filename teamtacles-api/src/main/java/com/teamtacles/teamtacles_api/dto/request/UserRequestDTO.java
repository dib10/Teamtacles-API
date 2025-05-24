package com.teamtacles.teamtacles_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {
    @Size(max = 50)
    @NotBlank(message="O nome não pode estar em branco!")
    private String userName;

    @Size(max = 250)
    @Email(message="O email deve ser válido!")
    private String email;

    @Size(min = 5, max = 100) 
    @NotBlank(message="A senha não pode estar em branco!")
    private String password;

    @Size(min = 5, max = 100)
    @NotBlank(message="A senha de confirmação não pode estar em branco!")
    private String passwordConfirm;
}
