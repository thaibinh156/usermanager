package com.infodation.userservice.models.dto;

import com.infodation.userservice.models.Role;
import lombok.Data;

@Data
public class AssignRoleRequest {
    private String role;
}
