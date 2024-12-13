package com.infodation.task_service.models.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskAssignmentDTO {
    private Long userId;
    private Long taskId;
}