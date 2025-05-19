package com.teamtacles.teamtacles_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.teamtacles.teamtacles_api.model.enums.ERole;
import com.teamtacles.teamtacles_api.model.Role;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}