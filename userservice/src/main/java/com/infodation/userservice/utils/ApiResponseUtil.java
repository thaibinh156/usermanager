package com.infodation.userservice.utils;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ApiResponseUtil {

    public static <T> ApiResponse<T> buildApiResponse(T data, HttpStatus status, String message, String error) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .error(error)
                .statusCode(status.value())
                .message(message)
                .data(data)
                .build();
    }
}
