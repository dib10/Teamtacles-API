package com.teamtacles.teamtacles_api.task;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.ActiveProfiles;

import com.teamtacles.teamtacles_api.mapper.PagedResponseMapper;
import com.teamtacles.teamtacles_api.repository.ProjectRepository;
import com.teamtacles.teamtacles_api.repository.TaskRepository;
import com.teamtacles.teamtacles_api.repository.UserRepository;
import com.teamtacles.teamtacles_api.service.ProjectService;
import com.teamtacles.teamtacles_api.service.TaskService;

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
}
