package com.teamtacles.teamtacles_api.dto.request;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestDTO {

    @Size(max = 50)
	@NotBlank(message="O título não pode estar em branco!")
    private String title; 

    @Size(max = 250)        
    private String description;

    @NotBlank
    @Future(message="A data de entrega não pode ser no passado!")
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dueDate;

}