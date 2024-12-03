package com.infodation.userservice.models.TaskDTO;

import com.infodation.userservice.models.UserDTO;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskUserResponseDTO {
    private List<TaskDTO> tasks;
    private UserDTO user;
}
