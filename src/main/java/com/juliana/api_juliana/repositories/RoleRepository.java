package com.juliana.api_juliana.repositories;

import com.juliana.api_juliana.entities.Role;
import com.juliana.api_juliana.enums.RoleList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(RoleList name);
}
