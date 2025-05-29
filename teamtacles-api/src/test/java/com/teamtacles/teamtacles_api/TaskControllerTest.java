package com.teamtacles.teamtacles_api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.teamtacles.teamtacles_api.model.Project;
import com.teamtacles.teamtacles_api.model.Task;
import com.teamtacles.teamtacles_api.model.User;
import com.teamtacles.teamtacles_api.model.UserAuthenticated;
import com.teamtacles.teamtacles_api.repository.ProjectRepository;
import com.teamtacles.teamtacles_api.repository.TaskRepository;
import com.teamtacles.teamtacles_api.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private User testUser;
    private Project testProject;

    @BeforeEach
    void setup() {
        taskRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUserName("Caio da Silva");
        user.setEmail("caio@example.com");
        user.setPassword("senhaSegura123");
        testUser = userRepository.save(user); 

        Project project = new Project();
        project.setTitle("Projeto Exemplo");
        project.setDescription("Projeto de exemplo para integração com frontend");
        project.setTeam(List.of(testUser));
        project.setCreator(user);
        testProject = projectRepository.save(project);

        authenticate(testUser);
    }

    @Test
    @DisplayName("Should create a task and return 201 Created")
    void testCreateTask_ShouldReturnCreated() throws Exception {
        String json = String.format("""
        {
            "title": "Revisar 2 documentação do projeto",
            "description": "Conferir se a documentação da API está atualizada",
            "dueDate": "30/06/2025 18:00",
            "ownerId": %d,
            "usersResponsability": [%d]
        }
        """, testUser.getUserId(), testUser.getUserId());

        mockMvc.perform(post("/api/project/{project_id}/task", testProject.getId())
            .contentType("application/json")
            .content(json))
            .andDo(print())
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should return task by ID with status 200")
    void testGetTaskById_ShouldReturnOK() throws Exception {
        Task task = new Task();
        task.setTitle("Revisar 2 documentação do projeto");
        task.setDescription("Conferir se a documentação da API está atualizada");
        task.setDueDate(LocalDateTime.of(2025, 6, 30, 18, 0));
        task.setOwner(testUser);
        task.setProject(testProject);
        task.setUsersResponsability(List.of(testUser));

        task = taskRepository.save(task);

        mockMvc.perform(get("/api/project/" + testProject.getId() + "/task/" + 1))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Revisar 2 documentação do projeto"))
            .andExpect(jsonPath("$.description").value("Conferir se a documentação da API está atualizada"));
    }

    // Simula o usuario autenticado 
    private void authenticate(User user) {
        UserAuthenticated userAuthenticated = new UserAuthenticated(user);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userAuthenticated, null, userAuthenticated.getAuthorities()));
    }

    

}
