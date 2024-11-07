package com.infodation.userservice.controllers;

import com.infodation.userservice.models.User;
import com.infodation.userservice.services.UserService;
import com.infodation.userservice.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsersController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getUsers() {
        List<User> users = userService.getAll();
        ApiResponse<List<User>> apiResponse =ApiResponse.<List<User>>builder()
                .timestamp(LocalDateTime.now())
                .error(null)
                .statusCode(HttpStatus.OK.value())
                .message("Users fetched successfully")
                .data(users)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable("id") Long id) {
        User user = userService.getById(id);
        if (user == null) {
            ApiResponse<User> response = ApiResponse.<User>builder()
                    .timestamp(LocalDateTime.now())
                    .error("Not Found")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .message("User not found")
                    .data(null)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        ApiResponse<User> response = ApiResponse.<User>builder()
                .timestamp(LocalDateTime.now())
                .error(null)
                .statusCode(HttpStatus.OK.value())
                .message("User fetched successfully")
                .data(user)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public User saveUser(@RequestBody User user) {
        return userService.save(user);
    }
}
