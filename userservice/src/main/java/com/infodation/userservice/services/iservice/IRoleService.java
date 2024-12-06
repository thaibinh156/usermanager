package com.infodation.userservice.services.iservice;

import com.infodation.userservice.models.Role;

import java.util.Set;

public interface IRoleService {
    void createDefaultRoles();
    Role getRole(String name);
}
