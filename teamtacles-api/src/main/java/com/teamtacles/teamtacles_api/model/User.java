package com.teamtacles.teamtacles_api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;

    @Size(max = 250)
    @Email
    private String email;

    private Role role;
}