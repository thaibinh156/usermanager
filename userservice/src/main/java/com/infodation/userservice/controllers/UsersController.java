package com.infodation.userservice.controllers;

import com.infodation.userservice.models.User;
import com.infodation.userservice.models.dto.user.CreateUserDTO;
import com.infodation.userservice.models.dto.user.UpdateUserDTO;
import com.infodation.userservice.services.iservice.IUserService;
import com.infodation.userservice.utils.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import com.infodation.userservice.utils.ApiResponseUtil;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UsersController {
    private final IUserService userService;

    public UsersController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getUsers() {
        List<User> users = userService.getAll();
        ApiResponse<List<User>> apiResponse = ApiResponseUtil.buildApiResponse(users, HttpStatus.OK, "Users fetched successfully", null);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable("userId") String userId) {
        User user = userService.getByUserId(userId);
        HttpStatus status = user == null ? HttpStatus.NOT_FOUND : HttpStatus.OK;
        String message = user == null ? String.format("User with ID %s not found", userId) : "User fetched successfully";

        ApiResponse<User> response = ApiResponseUtil.buildApiResponse(user, status, message, null);
        return new ResponseEntity<>(response, status);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<User>> create(@Valid @RequestBody CreateUserDTO userDto) {
        User existingUser = userService.getByUserId(userDto.getUserId());
        HttpStatus status;
        String message;
        User savedUser = null;

        if (existingUser != null) {
            status = HttpStatus.CONFLICT;
            message = String.format("User with ID %s already exists", userDto.getUserId());
        } else {
            savedUser = userService.save(userDto);
            status = HttpStatus.CREATED;
            message = "User created successfully";
        }

        ApiResponse<User> response = ApiResponseUtil.buildApiResponse(savedUser, status, message, null);
        return new ResponseEntity<>(response, status);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<User>> update(@PathVariable("userId") String userId,@Valid @RequestBody UpdateUserDTO userDTO) {
        User existingUser = userService.getByUserId(userId);

        HttpStatus status;
        String message;
        User updatedUser = null;

        if (existingUser == null) {
            status = HttpStatus.NOT_FOUND;
            message = String.format("User with ID %s not found", userId);
        } else {
            updatedUser = userService.update(userId, userDTO);
            status = HttpStatus.OK;
            message = "User updated successfully";
        }

        ApiResponse<User> response = ApiResponseUtil.buildApiResponse(updatedUser, status, message, null);

        return new ResponseEntity<>(response, status);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable("userId") String userId) {
        User existingUser = userService.getByUserId(userId);
        HttpStatus status;
        String message;

        if (existingUser == null) {
            status = HttpStatus.NOT_FOUND;
            message = String.format("User with ID %s not found", userId);
        } else {
            userService.delete(userId);
            status = HttpStatus.OK;
            message = String.format("User with ID %s deleted successfully", userId);
        }

        ApiResponse<?> response = ApiResponseUtil.buildApiResponse(null, status, message, null);
        return new ResponseEntity<>(response, status);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>>  handleValidationException(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(ApiResponse.<Map<String, String>>builder()
                .timestamp(LocalDateTime.now())
                .error("Validation Error")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed")
                .data(errors)
                .build());
    }
}
