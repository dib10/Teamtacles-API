package com.teamtacles.teamtacles_api;

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
import com.teamtacles.teamtacles_api.dto.request.TaskRequestDTO;
import com.teamtacles.teamtacles_api.dto.request.TaskRequestPatchDTO;
import com.teamtacles.teamtacles_api.model.Task;
import com.teamtacles.teamtacles_api.model.enums.Status;
import com.teamtacles.teamtacles_api.repository.ProjectRepository;
import com.teamtacles.teamtacles_api.repository.TaskRepository;
import com.teamtacles.teamtacles_api.repository.UserRepository;
import com.teamtacles.teamtacles_api.util.TestDataAux;
import com.teamtacles.teamtacles_api.util.TestDataProjectAux;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TestDataAux testDataAux;

    @Autowired
    private TestDataProjectAux testDataProjectAux;

    @BeforeEach
    void setUpEnvironment() {
        taskRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();
        testDataAux.setUpTestUsers();
        testDataProjectAux.setUpTestProject();
    }

    @Test
    @DisplayName("Should create a task and return 201 CREATED")
    void testCreateTask_ShouldReturn201() throws Exception {
        TaskRequestDTO dto = new TaskRequestDTO();
        dto.setTitle("Review project documentation");
        dto.setDescription("Check if the API documentation is updated");
        dto.setDueDate(LocalDateTime.now().plusDays(5));
        dto.setUsersResponsability(List.of(testDataAux.getNormalUser().getUserId()));

        mockMvc.perform(post("/api/project/{project_id}/task", testDataProjectAux.getProject().getId())
                .header("Authorization", "Bearer " + testDataAux.getNormalUserToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Review project documentation"));

        var tasks = taskRepository.findAll();
        assertFalse(tasks.isEmpty(), "Task list should not be empty after creation");
        assertEquals(testDataAux.getNormalUser().getUserId(), tasks.get(0).getOwner().getUserId(), "Task should be assigned to the correct user");   
    }

    @Test
    @DisplayName("Should return task by ID as Admin with 200 OK")
    void testGetTaskById_WhenAdmin_ShouldReturn200() throws Exception {
        Task savedTask = createUserTask();

        mockMvc.perform(get("/api/project/{id_project}/task/{id_task}", testDataProjectAux.getProject().getId(), savedTask.getId())
                .header("Authorization", "Bearer " + testDataAux.getAdminUserToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Review project documentation"));
    }

    @Test
    @DisplayName("Should forbid access to task by ID when not responsible with 403 FORBIDDEN")
    void testGetTaskById_WhenUserNotResponsible_ShouldReturn403() throws Exception {
        Task savedTask = createAdminTask();

        mockMvc.perform(get("/api/project/{id_project}/task/{id_task}", testDataProjectAux.getProject().getId(), savedTask.getId())
                .header("Authorization", "Bearer " + testDataAux.getNormalUserToken()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return tasks by user ID as Admin with 200 OK")
    void testGetTasksByUserId_WhenAdmin_ShouldReturn200() throws Exception {
        Task savedTask = createUserTask();

        mockMvc.perform(get("/api/project/{project_id}/tasks/user/{user_id}", testDataProjectAux.getProject().getId(), testDataAux.getNormalUser().getUserId())
                .header("Authorization", "Bearer " + testDataAux.getAdminUserToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Review project documentation"));
    }

    @Test
    @DisplayName("Should forbid user from getting their own tasks with 403 FORBIDDEN")
    void testGetTasksByUserId_WhenUser_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/project/{project_id}/tasks/user/{user_id}", testDataProjectAux.getProject().getId(), testDataAux.getNormalUser().getUserId())
                .header("Authorization", "Bearer " + testDataAux.getNormalUserToken()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should partially update task as Admin with 200 OK")
    void testPartialUpdate_WhenAdmin_ShouldReturn200() throws Exception {
        Task savedTask = createUserTask();

        TaskRequestPatchDTO dto = new TaskRequestPatchDTO();
        dto.setStatus(Optional.of(Status.INPROGRESS));

        mockMvc.perform(patch("/api/project/{id_project}/task/{id_task}/updateStatus", testDataProjectAux.getProject().getId(), savedTask.getId())
                .header("Authorization", "Bearer " + testDataAux.getAdminUserToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INPROGRESS"));
    }

    @Test
    @DisplayName("Should partially update task as responsible User with 200 OK")
    void testPartialUpdate_WhenUser_ShouldReturn200() throws Exception {
        Task savedTask = createUserTask();

        TaskRequestPatchDTO dto = new TaskRequestPatchDTO();
        dto.setStatus(Optional.of(Status.INPROGRESS));

        mockMvc.perform(patch("/api/project/{id_project}/task/{id_task}/updateStatus", testDataProjectAux.getProject().getId(), savedTask.getId())
                .header("Authorization", "Bearer " + testDataAux.getNormalUserToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INPROGRESS"));
    }

    @Test
    @DisplayName("Should forbid partial update of task by unauthorized User with 403 FORBIDDEN")
    void testPartialUpdate_WhenUserNotResponsible_ShouldReturn403() throws Exception {
        Task savedTask = createAdminTask();

        TaskRequestPatchDTO dto = new TaskRequestPatchDTO();
        dto.setStatus(Optional.of(Status.INPROGRESS));

        mockMvc.perform(patch("/api/project/{id_project}/task/{id_task}/updateStatus", testDataProjectAux.getProject().getId(), savedTask.getId())
                .header("Authorization", "Bearer " + testDataAux.getNormalUserToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should fully update task as Admin with 200 OK")
    void testFullUpdate_WhenAdmin_ShouldReturn200() throws Exception {
        Task savedTask = createUserTask();

        TaskRequestDTO dto = new TaskRequestDTO();
        dto.setTitle("Create README.md");
        dto.setDescription("Create the README.md file");
        dto.setDueDate(LocalDateTime.now().plusDays(5));
        dto.setUsersResponsability(List.of(testDataAux.getNormalUser().getUserId()));

        mockMvc.perform(put("/api/project/{id_project}/task/{id_task}", testDataProjectAux.getProject().getId(), savedTask.getId())
                .header("Authorization", "Bearer " + testDataAux.getAdminUserToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Create README.md"));
    }

    @Test
    @DisplayName("Should fully update task as owner User with 200 OK")
    void testFullUpdate_WhenUser_ShouldReturn200() throws Exception {
        Task savedTask = createUserTask();

        TaskRequestDTO dto = new TaskRequestDTO();
        dto.setTitle("Create README.md");
        dto.setDescription("Create the README.md file");
        dto.setDueDate(LocalDateTime.now().plusDays(5));
        dto.setUsersResponsability(List.of(testDataAux.getNormalUser().getUserId()));

        mockMvc.perform(put("/api/project/{id_project}/task/{id_task}", testDataProjectAux.getProject().getId(), savedTask.getId())
                .header("Authorization", "Bearer " + testDataAux.getNormalUserToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Create README.md"));
    }

    @Test
    @DisplayName("Should forbid full update of Admin's task by User with 403 FORBIDDEN")
    void testFullUpdate_WhenUserNotResponsible_ShouldReturn403() throws Exception {
        Task savedTask = createAdminTask();

        TaskRequestDTO dto = new TaskRequestDTO();
        dto.setTitle("Create README.md");
        dto.setDescription("Create the README.md file");
        dto.setDueDate(LocalDateTime.now().plusDays(5));
        dto.setUsersResponsability(List.of(testDataAux.getNormalUser().getUserId()));

        mockMvc.perform(put("/api/project/{id_project}/task/{id_task}", testDataProjectAux.getProject().getId(), savedTask.getId())
                .header("Authorization", "Bearer " + testDataAux.getNormalUserToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should delete task as Admin with 204 NO CONTENT")
    void testDeleteTask_WhenAdmin_ShouldReturn204() throws Exception {
        Task savedTask = createUserTask();

        mockMvc.perform(delete("/api/project/{id_project}/task/{id_task}", testDataProjectAux.getProject().getId(), savedTask.getId())
                .header("Authorization", "Bearer " + testDataAux.getAdminUserToken()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should delete task as owner User with 204 NO CONTENT")
    void testDeleteTask_WhenUser_ShouldReturn204() throws Exception {
        Task savedTask = createUserTask();

        mockMvc.perform(delete("/api/project/{id_project}/task/{id_task}", testDataProjectAux.getProject().getId(), savedTask.getId())
                .header("Authorization", "Bearer " + testDataAux.getNormalUserToken()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should forbid deletion of Admin's task by User with 403 FORBIDDEN")
    void testDeleteTask_WhenUserNotResponsible_ShouldReturn403() throws Exception {
        Task savedTask = createAdminTask();

        mockMvc.perform(delete("/api/project/{id_project}/task/{id_task}", testDataProjectAux.getProject().getId(), savedTask.getId())
                .header("Authorization", "Bearer " + testDataAux.getNormalUserToken()))
                .andExpect(status().isForbidden());
    }

    private Task createUserTask() {
        Task task = new Task();
        task.setTitle("Review project documentation");
        task.setDescription("Check if the API documentation is updated");
        task.setDueDate(LocalDateTime.now().plusDays(2));
        task.setUsersResponsability(List.of(testDataAux.getNormalUser()));
        task.setOwner(testDataAux.getNormalUser());
        task.setProject(testDataProjectAux.getProject());

        return taskRepository.save(task);
    }

    private Task createAdminTask() {
        Task adminTask = new Task();
        adminTask.setTitle("Admin's Task");
        adminTask.setDescription("Admin's task description");
        adminTask.setDueDate(LocalDateTime.now().plusDays(2));
        adminTask.setUsersResponsability(List.of(testDataAux.getAdminUser()));
        adminTask.setOwner(testDataAux.getAdminUser());
        adminTask.setProject(testDataProjectAux.getProject());

        return taskRepository.save(adminTask);
    }
}
