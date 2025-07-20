package com.hopeclinic.api.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hopeclinic.api.models.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{
    Optional<Role> findByName(String name);
}


