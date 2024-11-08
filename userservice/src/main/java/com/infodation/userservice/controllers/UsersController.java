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
        ApiResponse<List<User>> apiResponse =ApiResponse.<List<User>>builder()
                .timestamp(LocalDateTime.now())
                .error(null)
                .statusCode(HttpStatus.OK.value())
                .message("Users fetched successfully")
                .data(users)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable("userId") String userId) {
        User user = userService.getByUserId(userId);

        if (user == null) {
            ApiResponse<User> response = ApiResponse.<User>builder()
                    .timestamp(LocalDateTime.now())
                    .error(null)
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .message("User is not found")
                    .data(null)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else {
            ApiResponse<User> response = ApiResponse.<User>builder()
                    .timestamp(LocalDateTime.now())
                    .error(null)
                    .statusCode(HttpStatus.OK.value())
                    .message("User fetched successfully")
                    .data(user)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

    }

    @PostMapping
    public ResponseEntity<ApiResponse<User>> create(@Valid @RequestBody CreateUserDTO userDto) {

        if (userService.getByUserId(userDto.getUserId()) != null) {
            ApiResponse<User> response = ApiResponse.<User>builder()
                    .timestamp(LocalDateTime.now())
                    .error(null)
                    .statusCode(HttpStatus.CONFLICT.value())
                    .message("UserId " + userDto.getUserId() +" is exists")
                    .data(null)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } else {
            User savedUser = userService.save(userDto);

            ApiResponse<User> response = ApiResponse.<User>builder()
                    .timestamp(LocalDateTime.now())
                    .error(null)
                    .statusCode(HttpStatus.CREATED.value())
                    .message("User saved successfully")
                    .data(savedUser)
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<User>> update(@PathVariable("userId") String userId,@Valid @RequestBody UpdateUserDTO userDTO) {

        if (userService.getByUserId(userId) == null) {
            ApiResponse<User> response = ApiResponse.<User>builder()
                    .timestamp(LocalDateTime.now())
                    .error(null)
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .message("User not found")
                    .data(null)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else {

            User updatedUser = userService.update(userId, userDTO);

            ApiResponse<User> response = ApiResponse.<User>builder()
                    .timestamp(LocalDateTime.now())
                    .error(null)
                    .statusCode(HttpStatus.OK.value())
                    .message("User updated successfully")
                    .data(updatedUser)
                    .build();

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }


    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable("userId") String userId) {
        if (userService.getByUserId(userId) == null) {
            ApiResponse<User> response = ApiResponse.<User>builder()
                    .timestamp(LocalDateTime.now())
                    .error(null)
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .message("User not found")
                    .data(null)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else {
            userService.delete(userId);

            ApiResponse<String> response = ApiResponse.<String>builder()
                    .timestamp(LocalDateTime.now())
                    .error(null)
                    .statusCode(HttpStatus.OK.value())
                    .message("User deleted successfully")
                    .data(null)
                    .build();

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
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
