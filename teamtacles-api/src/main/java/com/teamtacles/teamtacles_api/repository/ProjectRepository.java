package com.teamtacles.teamtacles_api.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.teamtacles.teamtacles_api.model.Project;
import com.teamtacles.teamtacles_api.model.User;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>{
    Page<Project> findByCreator(User creator, Pageable pageable);
    Page<Project> findById(Long id, Pageable pageable);
}