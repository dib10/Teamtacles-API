package com.teamtacles.teamtacles_api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.teamtacles.teamtacles_api.model.enums.Status;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    private Status status;

    @NotNull
    private User owner;

    private Project project;
}
