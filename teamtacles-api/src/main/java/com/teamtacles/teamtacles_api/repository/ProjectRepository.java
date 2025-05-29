package com.teamtacles.teamtacles_api.repository;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.teamtacles.teamtacles_api.model.Project;
import com.teamtacles.teamtacles_api.model.User;
import com.teamtacles.teamtacles_api.model.enums.Status;


@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>{
    Page<Project> findByCreator(User creator, Pageable pageable);
    Page<Project> findById(Long id, Pageable pageable);
    Page<Project> findByTeam(User user, Pageable pageable);
    
    // Método para buscas personalizadas em JPQL - filtro
    @Query("""
        SELECT DISTINCT p FROM Project p
        JOIN p.tasks t
        LEFT JOIN t.usersResponsability ur
        WHERE (:statusEnum IS NULL OR t.status = :statusEnum)
        AND (:dueDate IS NULL OR t.dueDate <= :dueDate)
        AND (:projectId IS NULL OR p.id = :projectId)
        AND (:userId IS NULL OR (t.owner.id = :userId OR ur.id = :userId))
        """)
    Page<Project> findProjectsFilteredByUser(Status statusEnum, LocalDateTime dueDate, Long projectId, Long userId, Pageable pageable);

    // Método para buscas personalizadas em JPQL - filtro
    @Query("""
        SELECT DISTINCT p FROM Project p
        JOIN p.tasks t
        WHERE (:statusEnum IS NULL OR t.status = :statusEnum)
        AND (:dueDate IS NULL OR t.dueDate <= :dueDate)
        AND (:projectId IS NULL OR p.id = :projectId)
        """)
    Page<Project> findProjectsFiltered(Status statusEnum, LocalDateTime dueDate, Long projectId, Pageable pageable);
}