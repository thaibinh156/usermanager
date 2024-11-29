package com.infodation.userservice.config.users;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "username is blank")
    @JsonAlias("username")
    private String username;
    @NotBlank(message = "password is blank")
    @JsonAlias("password")
    private String password;
}
