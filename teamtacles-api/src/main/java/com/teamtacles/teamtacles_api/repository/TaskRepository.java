package com.teamtacles.teamtacles_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamtacles.teamtacles_api.model.Project;
import com.teamtacles.teamtacles_api.model.Task;
import com.teamtacles.teamtacles_api.model.enums.Status;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByProject(Project project, Pageable Pageable);
    Page<Task> findByStatus(Status status, Pageable pageable);
}


