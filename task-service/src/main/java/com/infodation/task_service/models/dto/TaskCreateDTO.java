package com.infodation.task_service.models.dto;

import lombok.Data;

@Data
public class TaskCreateDTO {
    private String title;
    private String description;
    private Long categoryId;
    private String userId;
    private Long statusId;
    private String priority;
}
