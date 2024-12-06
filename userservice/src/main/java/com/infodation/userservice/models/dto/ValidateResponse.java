package com.infodation.userservice.models.dto;

import com.infodation.userservice.models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

@Data
public class ValidateResponse {
    private Long userId;
    private ArrayList<Role> roles;
}
