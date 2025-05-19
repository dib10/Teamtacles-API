package com.teamtacles.teamtacles_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.teamtacles.teamtacles_api.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>{
    
}