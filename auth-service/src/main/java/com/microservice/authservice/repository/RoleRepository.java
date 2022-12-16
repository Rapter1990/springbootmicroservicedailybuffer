package com.microservice.authservice.repository;

import com.microservice.authservice.model.ERole;
import com.microservice.authservice.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(ERole name);
}
