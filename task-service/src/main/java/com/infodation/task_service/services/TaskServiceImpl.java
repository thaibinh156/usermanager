package com.infodation.task_service.services;

import com.infodation.task_service.models.TaskProjection;
import com.infodation.task_service.repositories.TaskServiceRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
@Service
public class TaskServiceImpl implements ITaskService {

    private final TaskServiceRepository taskServiceRepository;

    // Constructor injection to receive the repository
    public TaskServiceImpl(TaskServiceRepository taskServiceRepository) {
        this.taskServiceRepository = taskServiceRepository;
    }

    @Override
    public List<TaskProjection> getTasksByUserId(Long userId) {
        // Query TaskProjection objects from the repository
        List<TaskProjection> tasks = taskServiceRepository.findTasksByUserId(userId);
        // Check if no tasks are found
        if (tasks.isEmpty()) {
            return new ArrayList<>();
        }
        // Return the list of TaskProjection objects
        return tasks;
    }
}

