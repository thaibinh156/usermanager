package com.infodation.userservice.models.TaskDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private Long taskId;
    private String title;
    private String categoryName;
    private String categoryDescription;
    private String statusName;
    private String statusDescription;
}