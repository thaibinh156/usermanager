package com.infodation.userservice.services;

import com.infodation.userservice.models.Role;
import com.infodation.userservice.repositories.RoleRepository;
import com.infodation.userservice.services.iservice.IRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class RoleServiceImpl implements IRoleService {

    private static final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);
    private final RoleRepository repository;

    public RoleServiceImpl(RoleRepository repository) {
        this.repository = repository;
    }

    @Override
    public void createDefaultRoles() {
        Set<Role> roles = new HashSet<>();

        if (!repository.existsByName("ROLE_USER")) {
            roles.add(new Role(2,"ROLE_USER"));
            log.info("Role 'USER' created.");
        } else {
            log.info("Role 'USER' already exists.");
        }

        if (!repository.existsByName("ROLE_ADMIN")) {
            roles.add(new Role(1,"ROLE_ADMIN"));
            log.info("Role 'ADMIN' created.");
        } else {
            log.info("Role 'ADMIN' already exists.");
        }

        try {
            if (!roles.isEmpty()) {
                repository.saveAllAndFlush(roles);
                log.info("Default roles saved to the database.");
            }
        } catch (Exception e) {
            log.error("Error while creating default roles: {}", e.getMessage());
        }
    }

    @Override
    public Role getRole(String name) {
        return repository.getRoleByName(name);
    }
}
