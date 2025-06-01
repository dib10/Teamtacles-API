package com.teamtacles.teamtacles_api.project;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamtacles.teamtacles_api.dto.request.ProjectRequestDTO;
import com.teamtacles.teamtacles_api.dto.request.ProjectRequestPatchDTO;
import com.teamtacles.teamtacles_api.model.Project;
import com.teamtacles.teamtacles_api.model.Task;
import com.teamtacles.teamtacles_api.repository.ProjectRepository;
import com.teamtacles.teamtacles_api.repository.UserRepository;
import com.teamtacles.teamtacles_api.util.TestDataAux;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProjectControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TestDataAux testDataAux;

    @BeforeEach
    void setUpEnvironment() {
        projectRepository.deleteAll();
        userRepository.deleteAll();
        testDataAux.setUpTestUsers();
    }

    @Test
    @DisplayName("Should create a project and return 201 CREATED")
    void testCreateProject_whenUser_ShouldReturn201() throws Exception {
        ProjectRequestDTO dto = new ProjectRequestDTO();

        dto.setTitle("API Project");
        dto.setDescription("Team task management API");
        dto.setTeam(List.of(testDataAux.getNormalUser().getUserId()));

        mockMvc.perform(post("/api/project") 
                .header("Authorization", "Bearer " + testDataAux.getNormalUserToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("API Project"));
    }

    @Test
    @DisplayName("Should get all projects as ADMIN")
    void testGetAllProject_WhenAdmin_ShouldReturn200() throws Exception {

        Project savedProject = createUserOwnerProject();

        mockMvc.perform(get("/api/project/all") 
            .header("Authorization", "Bearer " + testDataAux.getAdminUserToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].title").value("API Project"));
    }

    @Test
    @DisplayName("Shouldn't return a project when user not in the team")
    void testGetAllProject_WhenUserNotInTeam_ShouldReturn200() throws Exception {
    
        Project savedProject = createAdminOwnerProject();

        mockMvc.perform(get("/api/project/all") 
            .header("Authorization", "Bearer " + testDataAux.getNormalUserToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    @DisplayName("Should return a project when user in the team")
    void testGetAllProject_WhenUser_ShouldReturn200() throws Exception {
    
        Project savedProject = createAdminOwnerProjectAndUserTeam();

        mockMvc.perform(get("/api/project/all") 
            .header("Authorization", "Bearer " + testDataAux.getNormalUserToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content").isNotEmpty());
    }

    @Test
    @DisplayName("Should partially update project as Admin with 200 OK")
    void testPartialUpdate_WhenAdmin_ShouldReturn200() throws Exception {

        Project savedProject = createUserOwnerProject();

        ProjectRequestPatchDTO dto = new ProjectRequestPatchDTO();
        dto.setTitle(Optional.of("Backend project"));

        mockMvc.perform(patch("/api/project/{project_id}", savedProject.getId())
            .header("Authorization", "Bearer " + testDataAux.getAdminUserToken())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Backend project"));
    }

    @Test
    @DisplayName("Should not partially update project with User in team with 403 FORBIDDEN")
    void testPartialUpdate_WhenUserInTeam_ShouldReturn403() throws Exception {
        
        Project savedProject = createAdminOwnerProjectAndUserTeam();

        ProjectRequestPatchDTO dto = new ProjectRequestPatchDTO();
        dto.setTitle(Optional.of("Backend project"));

        mockMvc.perform(patch("/api/project/{project_id}", savedProject.getId())
            .header("Authorization", "Bearer " + testDataAux.getNormalUserToken())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should not partially update project when User not in the team with 403 FORBIDDEN")
    void testPartialUpdate_WhenUser_ShouldReturn403() throws Exception {
        
        Project savedProject = createAdminOwnerProject();

        ProjectRequestPatchDTO dto = new ProjectRequestPatchDTO();
        dto.setTitle(Optional.of("Backend project"));

        mockMvc.perform(patch("/api/project/{project_id}", savedProject.getId())
            .header("Authorization", "Bearer " + testDataAux.getNormalUserToken())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should fully update project as Admin with 200 OK")
    void testFullUpdate_WhenAdmin_ShouldReturn200() throws Exception {

        Project savedProject = createUserOwnerProject();

        ProjectRequestDTO dto = new ProjectRequestDTO();

        dto.setTitle("Backend Project");
        dto.setDescription("With Java");
        dto.setTeam(List.of(testDataAux.getNormalUser().getUserId()));

        mockMvc.perform(put("/api/project/{project_id}", savedProject.getId())
            .header("Authorization", "Bearer " + testDataAux.getAdminUserToken())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Backend Project"))
            .andExpect(jsonPath("$.description").value("With Java"));
    }

    @Test
    @DisplayName("Should not fully update project when User not creator with 403 FORBIDDEN")
    void testFullUpdate_WhenUser_ShouldReturn403() throws Exception {
        Project savedProject = createAdminOwnerProjectAndUserTeam();

        ProjectRequestDTO dto = new ProjectRequestDTO();

        dto.setTitle("Backend Project");
        dto.setDescription("With Java");
        dto.setTeam(List.of(testDataAux.getNormalUser().getUserId()));

        mockMvc.perform(put("/api/project/{project_id}", savedProject.getId())
            .header("Authorization", "Bearer " + testDataAux.getNormalUserToken())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should delete project as Admin with 204 NO CONTENT")
    void testDeleteTask_WhenAdmin_ShouldReturn204() throws Exception {
        Project savedProject = createUserOwnerProject();

        mockMvc.perform(delete("/api/project/{project_id}", savedProject.getId())
                .header("Authorization", "Bearer " + testDataAux.getAdminUserToken()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should not delete project when not the creator with 403 FORBIDDEN")
    void testDeleteTask_WhenNotUserCreator_ShouldReturn403() throws Exception {
        Project savedProject = createAdminOwnerProjectAndUserTeam();

        mockMvc.perform(delete("/api/project/{project_id}", savedProject.getId())
                .header("Authorization", "Bearer " + testDataAux.getNormalUserToken()))
                .andExpect(status().isForbidden());
    }

    private Project createUserOwnerProject(){
        Project project = new Project();
        project.setTitle("API Project");
        project.setDescription("Team task management API");
        project.setTeam(List.of(testDataAux.getNormalUser())); 
        project.setCreator(testDataAux.getNormalUser());

        return projectRepository.save(project);
    }

    private Project createAdminOwnerProjectAndUserTeam() {
        Project project = new Project();
        project.setTitle("API Project");
        project.setDescription("Team task management API");
        project.setTeam(List.of(testDataAux.getNormalUser())); 
        project.setCreator(testDataAux.getAdminUser());

        return projectRepository.save(project);
    }


    private Project createAdminOwnerProject() {
        Project project = new Project();
        project.setTitle("API Project");
        project.setDescription("Team task management API");
        project.setTeam(List.of(testDataAux.getAdminUser())); 
        project.setCreator(testDataAux.getAdminUser());

        return projectRepository.save(project);
    }

}
