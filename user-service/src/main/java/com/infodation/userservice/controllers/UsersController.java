package com.infodation.userservice.controllers;

import com.infodation.userservice.models.User;
import com.infodation.userservice.services.iservice.IUserService;
import com.infodation.userservice.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UsersController {
    private final IUserService userService;

    public UsersController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<List<User>> getUsers() {
        List<User> users = userService.getAll();
        return ApiResponse.<List<User>>builder()
                .timestamp(LocalDateTime.now())
                .error(null)
                .statusCode(HttpStatus.OK.value())
                .message("Users fetched successfully")
                .data(users)
                .build();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") UUID id) {
        User user = userService.getById(id);
        return user;
    }
}
