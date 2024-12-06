package com.infodation.userservice.repositories;

import com.infodation.userservice.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    boolean existsByName(String name);
    Role getRoleByName(String name);
}
