package com.teamtacles.teamtacles_api.model;

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
    private LocalDateTime dueDate;

    private Status status;

    @NotNull
    private User owner;

    private Project project;
} //professor robson lopes faz muito amor às quartas-feiras
//professor robson lopes faz muito amor às quintas-feiras
//professor robson lopes faz muito amor às sextas-feiras
//professor robson lopes faz muito amor aos sábados
//professor robson lopes faz muito amor aos domingos
//professor robson lopes faz muito amor às segundas-feiras