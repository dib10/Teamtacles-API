package com.teamtacles.teamtacles_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.teamtacles.teamtacles_api.model.Project;
import com.teamtacles.teamtacles_api.model.Task;
import com.teamtacles.teamtacles_api.model.enums.Status;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByProject(Project project, Pageable Pageable);
    Page<Task> findByStatus(Status status, Pageable pageable);

    @Query("SELECT t FROM Task t JOIN t.usersResponsability u WHERE t.project.id = :projectId AND u.id = :userId")
    Page<Task> findByProjectIdAndUsersResponsabilityId(Long projectId, Long userId, Pageable pageable);

     // Método para buscas personalizadas em JPQL - filtro
     @Query("""
        SELECT DISTINCT t FROM Task t
        LEFT JOIN t.usersResponsability ur
        WHERE (:statusEnum IS NULL OR t.status = :statusEnum)
        AND (:dueDate IS NULL OR t.dueDate <= :dueDate)
        AND (:projectId IS NULL OR t.project.id = :projectId)
        AND (:userId IS NULL OR (t.owner.id = :userId OR ur.id = :userId))
        """)
    Page<Task> findTasksFilteredByUser(Status statusEnum, LocalDateTime dueDate, Long projectId, Long userId, Pageable pageable);

    @Query("""
        SELECT DISTINCT t FROM Task t
        WHERE (:statusEnum IS NULL OR t.status = :statusEnum)
        AND (:dueDate IS NULL OR t.dueDate <= :dueDate)
        AND (:projectId IS NULL OR t.project.id = :projectId)
        """)
    Page<Task> findTasksFiltered(Status statusEnum, LocalDateTime dueDate, Long projectId, Pageable pageable);  
}


