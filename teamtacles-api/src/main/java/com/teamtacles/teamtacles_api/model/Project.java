package com.teamtacles.teamtacles_api.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class Project{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private List<Task> tasks;

    @NotBlank(message="O Projeto deve ter um dono!")
    private User creator;

    private List<User> team;

    @NotBlank(message="O título não pode estar em branco!")
    @Size(max = 50)
    private String title;

    @Size(max = 50)
    private String description;
}