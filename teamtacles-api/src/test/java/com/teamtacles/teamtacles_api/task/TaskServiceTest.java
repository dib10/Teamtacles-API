package com.teamtacles.teamtacles_api.task;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.ActiveProfiles;
import com.teamtacles.teamtacles_api.dto.request.TaskRequestDTO;
import com.teamtacles.teamtacles_api.dto.response.TaskResponseDTO;
import com.teamtacles.teamtacles_api.exception.ResourceNotFoundException;
import com.teamtacles.teamtacles_api.mapper.PagedResponseMapper;
import com.teamtacles.teamtacles_api.model.Project;
import com.teamtacles.teamtacles_api.model.Role;
import com.teamtacles.teamtacles_api.model.Task;
import com.teamtacles.teamtacles_api.model.User;
import com.teamtacles.teamtacles_api.model.enums.ERole;
import com.teamtacles.teamtacles_api.repository.ProjectRepository;
import com.teamtacles.teamtacles_api.repository.TaskRepository;
import com.teamtacles.teamtacles_api.repository.UserRepository;
import com.teamtacles.teamtacles_api.service.ProjectService;
import com.teamtacles.teamtacles_api.service.TaskService;
import com.teamtacles.teamtacles_api.model.enums.Status; 

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PagedResponseMapper pagedResponseMapper;

    @InjectMocks
    private TaskService taskService;

    private User adminUser;
    private User normalUser;
    private User responsibleUser;
    private User otherUser;
    private Project testProject; 
    private Role adminRoleInstance;
    private Role userRoleInstance;
    private Task existingTask; 

    @BeforeEach 
    void setUp() {

        // Configuração dos papéis
        adminRoleInstance = new Role();
        adminRoleInstance.setId(1L); 
        adminRoleInstance.setRoleName(ERole.ADMIN);

        userRoleInstance = new Role();
        userRoleInstance.setId(2L);
        userRoleInstance.setRoleName(ERole.USER);

        // Configuração do Usuário Admin
        adminUser = new User();
        adminUser.setUserId(1L);      
        adminUser.setUserName("admin"); 
        adminUser.setRoles(Set.of(adminRoleInstance));

        // Configuração do Usuário Normal
        normalUser = new User();
        normalUser.setUserId(2L);
        normalUser.setUserName("normaluser");
        normalUser.setRoles(Set.of(userRoleInstance));

        // Configuração do Usuário Responsável
        responsibleUser = new User();
        responsibleUser.setUserId(3L);
        responsibleUser.setUserName("responsibleuser");
        responsibleUser.setRoles(Set.of(userRoleInstance));

        // Configuração do Outro Usuário
        otherUser = new User();
        otherUser.setUserId(4L);
        otherUser.setUserName("otheruser");
        otherUser.setRoles(Set.of(userRoleInstance));

        // Configuração do Projeto de Teste

        testProject = new Project();
        testProject.setId(100L);
        testProject.setTitle("Test Project");
        testProject.setCreator(normalUser);
        testProject.setTeam(List.of(normalUser, responsibleUser, adminUser));

        // Configurando Tarefa existente
        existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Existing Task");
        existingTask.setProject(testProject);
        existingTask.setOwner(normalUser); // O dono da tarefa é o usuário normal
        existingTask.setStatus(Status.INPROGRESS);
        existingTask.setUsersResponsability(List.of(responsibleUser));
        existingTask.setDueDate(LocalDateTime.now().plusDays(5));
        

    }

    @Test
    @DisplayName("1.1: Should create a task successfully when data is valid")
    void createTask_shouldCreateTaskSuccessfully_whenDataIsValid() {

        // Arrange
        TaskRequestDTO requestDTO = new TaskRequestDTO();
        requestDTO.setTitle("New Task");
        requestDTO.setDescription("Task description");
        requestDTO.setDueDate(LocalDateTime.now().plusDays(7));
        requestDTO.setUsersResponsability(List.of(responsibleUser.getUserId()));

        when(projectRepository.findById(testProject.getId())).thenReturn(Optional.of(testProject));
        when(userRepository.findById(normalUser.getUserId())).thenReturn(Optional.of(normalUser));
        when(userRepository.findById(responsibleUser.getUserId())).thenReturn(Optional.of(responsibleUser));

        Task taskToBeMapped = new Task(); 
        taskToBeMapped.setTitle(requestDTO.getTitle());
        taskToBeMapped.setDescription(requestDTO.getDescription());
        taskToBeMapped.setDueDate(requestDTO.getDueDate());
        when(modelMapper.map(requestDTO, Task.class)).thenReturn(taskToBeMapped);

        // Configuração do que o taskRepository deve retornar
        Task savedTaskEntity = new Task();
        savedTaskEntity.setId(1L);
        savedTaskEntity.setTitle(requestDTO.getTitle());
        savedTaskEntity.setDescription(requestDTO.getDescription());
        savedTaskEntity.setDueDate(requestDTO.getDueDate());
        savedTaskEntity.setProject(testProject);
        savedTaskEntity.setOwner(normalUser);
        savedTaskEntity.setStatus(Status.TODO); 
        savedTaskEntity.setUsersResponsability(List.of(responsibleUser));
        when(taskRepository.save(any(Task.class))).thenReturn(savedTaskEntity);

        // Configuração do que o modelMapper deve retornar
        TaskResponseDTO expectedResponseDTO = new TaskResponseDTO();
        expectedResponseDTO.setId(savedTaskEntity.getId());
        expectedResponseDTO.setTitle(savedTaskEntity.getTitle());

        when(modelMapper.map(savedTaskEntity, TaskResponseDTO.class)).thenReturn(expectedResponseDTO);

        // Act
        TaskResponseDTO actualResponseDTO = taskService.createTask(testProject.getId(), requestDTO, normalUser);

        // Assert
        assertNotNull(actualResponseDTO, "The response DTO should not be null.");
        assertEquals(expectedResponseDTO.getId(), actualResponseDTO.getId(), "Response DTO ID should match.");
        assertEquals(expectedResponseDTO.getTitle(), actualResponseDTO.getTitle(), "Response DTO title should match.");

        verify(taskRepository, times(1)).save(any(Task.class));

        // Usando o ArgumentCaptor para capturar o Task passado para o save  
        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskArgumentCaptor.capture()); 
        Task taskPassedToSave = taskArgumentCaptor.getValue();     

        assertEquals(Status.TODO, taskPassedToSave.getStatus(), "The initial status of the task should be TODO.");
        assertEquals(normalUser, taskPassedToSave.getOwner(), "The owner of the task should be the user who created it.");
        assertEquals(testProject, taskPassedToSave.getProject(), "The task should be associated with the correct project.");
        assertTrue(taskPassedToSave.getUsersResponsability().contains(responsibleUser), "The responsible user should be in the list.");
        assertEquals(requestDTO.getTitle(), taskPassedToSave.getTitle(), "The title of the task passed to save should match the DTO.");
    }

    @Test
    @DisplayName("1.2: Should throw ResourceNotFoundException when project does not exist")
    void createTask_shouldThrowResourceNotFoundException_whenProjectNotFound() {

        // Arrange
        TaskRequestDTO requestDTO = new TaskRequestDTO();
        requestDTO.setTitle("Task for Nonexistent Project");
        requestDTO.setDescription("This task should not be created.");
        requestDTO.setDueDate(LocalDateTime.now().plusDays(7));
        requestDTO.setUsersResponsability(List.of(responsibleUser.getUserId()));

        // Simulando id de projeto inexistente
        Long nonexistentProjectId = 999L;

        when(projectRepository.findById(nonexistentProjectId)).thenReturn(Optional.empty());
            // Act & Assert
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                taskService.createTask(nonexistentProjectId, requestDTO, normalUser);
            });
            verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("1.3: Should throw ResourceNotFoundException_whenResponsibleUserNotFound") 
    void createTask_shouldThrowResourceNotFoundException_whenResponsibleUserNotFound() {
        //Arrange
        TaskRequestDTO requestDTO = new TaskRequestDTO();
        requestDTO.setTitle("Task with Nonexistent Responsible User");
        requestDTO.setDescription("This task should not be created.");
        requestDTO.setDueDate(LocalDateTime.now().plusDays(7));

        Long existentResponsibleUserId = responsibleUser.getUserId();
        Long nonexistentResponsibleUserId = 999L;
        requestDTO.setUsersResponsability(List.of(existentResponsibleUserId, nonexistentResponsibleUserId));

        when(projectRepository.findById(testProject.getId())).thenReturn(Optional.of(testProject));
        when(userRepository.findById(normalUser.getUserId())).thenReturn(Optional.of(normalUser));
        when(userRepository.findById(existentResponsibleUserId)).thenReturn(Optional.of(responsibleUser));
        when(userRepository.findById(nonexistentResponsibleUserId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            taskService.createTask(testProject.getId(), requestDTO, normalUser);
        });
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("2.1: Admin should get any task")
    void getTasksById_shouldReturnTask_whenUserIsAdmin() {
        //Arrange
        when(taskRepository.findById(existingTask.getId())).thenReturn(Optional.of(existingTask));

        //Mock model mapper
        TaskResponseDTO expectedResponseDTO = new TaskResponseDTO();
        expectedResponseDTO.setId(existingTask.getId());
        expectedResponseDTO.setTitle(existingTask.getTitle());
        when(modelMapper.map(existingTask, TaskResponseDTO.class)).thenReturn(expectedResponseDTO);
        // Act
        TaskResponseDTO actualResponseDTO = taskService.getTasksById(testProject.getId(), existingTask.getId(), adminUser);
        // Assert
        assertNotNull(actualResponseDTO, "Response DTO should not be null for admin.");
        assertEquals(expectedResponseDTO.getId(), actualResponseDTO.getId(), "Task ID should match.");
        assertEquals(expectedResponseDTO.getTitle(), actualResponseDTO.getTitle(), "Task title should match.");
        verify(taskRepository, times(1)).findById(existingTask.getId());
        verify(modelMapper, times(1)).map(existingTask, TaskResponseDTO.class);
    }

    






}
