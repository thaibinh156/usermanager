package com.infodation.task_service.services;

import com.infodation.task_service.models.TaskProjection;
import java.util.List;

public interface ITaskService {
    List<TaskProjection> getTasksByUserId(Long userId);
}
