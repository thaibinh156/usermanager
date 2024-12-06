package com.infodation.userservice.models.TaskDTO;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskAssignmentDTO {
    @JsonAlias("userId")
    @NotBlank
    @NotNull
    private String userId;
    @JsonAlias("taskId")
    @NotNull
    @NotBlank
    private String taskId;
}