package com.teamtacles.teamtacles_api.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "users")
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Size(min = 3, max = 50)
    @NotBlank(message="O nome não pode estar em branco!")
    private String userName;

    @Email(message="O email deve ser válido!")
    @Size(min = 8, max = 50)
    private String email;

    @NotBlank(message="A senha não pode estar em branco!")
    @Size(min = 5, max = 100) 
    private String password;

    // tasks que pertencem a ele
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "user-task")
    private List<Task> task = new ArrayList<>();

    // projetos que ele criou
    @OneToMany(mappedBy = "creator")
    @JsonManagedReference(value = "user-project")
    private List<Project> createdProjects = new ArrayList<>();

    // projetos que ele participa
    @ManyToMany(mappedBy = "team")
    private List<Project> projects = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
}