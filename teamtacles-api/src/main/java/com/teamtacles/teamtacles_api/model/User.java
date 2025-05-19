package com.teamtacles.teamtacles_api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 50)
    @NotBlank(message="O nome não pode estar em branco!")
    private String userName;

    @Size(max = 250)
    @Email(message="O email deve ser válido!")
    private String email;

    @NotBlank(message="A senha não pode estar em branco!")
    @Size(min = 5, max = 100) 
    private String password;

    @OneToOne
    private Role role;
}