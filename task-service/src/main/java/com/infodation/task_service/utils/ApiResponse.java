package com.infodation.task_service.utils;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class ApiResponse<T> {
    private LocalDateTime timestamp;
    private String error;
    private int statusCode;
    private String message;
    private T data;
}
