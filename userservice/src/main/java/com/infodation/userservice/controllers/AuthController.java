package com.infodation.userservice.controllers;

import com.infodation.userservice.components.JwtTokenProvider;
import com.infodation.userservice.config.users.CustomUserDetails;
import com.infodation.userservice.config.users.LoginRequest;
import com.infodation.userservice.config.users.LoginResponse;
import com.infodation.userservice.utils.ApiResponse;
import com.infodation.userservice.utils.ApiResponseUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, JwtTokenProvider jwtTokenProvider) {
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = null;
        String message;
        HttpStatus status;
        String error = null;

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = tokenProvider.generateToken((CustomUserDetails) authentication.getPrincipal());
            loginResponse = new LoginResponse(jwt);
            status = HttpStatus.OK;
            message = "Login Successfully";

        } catch (Exception ex) {
            message = "Invalid username or password"; // Default error message
            if (ex instanceof BadCredentialsException) {
                message = "Incorrect username or password";
            } else if (ex instanceof DisabledException) {
                message = "Account is disabled";
            }

            status = HttpStatus.UNAUTHORIZED;
            error = ex.getMessage();
        }

        ApiResponse<?> response = ApiResponseUtil.buildApiResponse(loginResponse, status, message, error);

        return new ResponseEntity<>(response, status);
    }


    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<?>> accessToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        String message;
        HttpStatus status;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            status = HttpStatus.UNAUTHORIZED;
            message = "Invalid Token";
        } else {

            String token = authHeader.substring(7);
            boolean isValid = jwtTokenProvider.validateToken(token);

            if (isValid) {
                status = HttpStatus.OK;
                message = "Valid Token";
            } else {
                status = HttpStatus.UNAUTHORIZED;
                message = "Invalid Token";
            }
        }

        ApiResponse<?> response = ApiResponseUtil.buildApiResponse(null, status, message, null);

        return new ResponseEntity<>(response,status);
    }

    @GetMapping("/random")
    public String randomStuff(){
        return "JWT Hợp lệ mới có thể thấy được message này";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(MethodArgumentNotValidException ex) {
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
