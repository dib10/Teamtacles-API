package com.teamtacles.teamtacles_api.model;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructors;
import lombok.Data;
import lombok.NoArgsConstructors;

@Data
@AllArgsConstructors
@NoArgsConstructors

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    private String title; 


    private String description;


    private String status;


    private LocalDateTime dueDate;


    private 
    
    status;


    private User owner;


    private Project project;

}