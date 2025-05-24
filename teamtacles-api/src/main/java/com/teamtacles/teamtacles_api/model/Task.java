package com.teamtacles.teamtacles_api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.teamtacles.teamtacles_api.model.enums.Status;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 50)
	@NotBlank(message="O título não pode estar em branco!")
    private String title; 

    @Size(max = 250)
    private String description;

    @NotNull
    @Future(message="A data de entrega não pode ser no passado!") 
	@JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    // owner das tasks 
    @NotNull
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User owner;

    // projetos que a task está associada
    @NotNull
    @ManyToOne(optional = false) // composição - temq pertencer a algum projeto
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}
