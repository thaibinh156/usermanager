package com.infodation.task_service.services;

import com.infodation.task_service.models.DTO.TaskDTO;
import com.infodation.task_service.models.Task;
import com.infodation.task_service.models.TaskProjection;
import com.infodation.task_service.models.UserTaskAssignment;
import com.infodation.task_service.repositories.UserTaskAssignmentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class TaskServiceImpl implements ITaskService {

    private final UserTaskAssignmentRepository userTaskAssignmentRepository;

    // Constructor injection để nhận repository
    public TaskServiceImpl(UserTaskAssignmentRepository userTaskAssignmentRepository) {
        this.userTaskAssignmentRepository = userTaskAssignmentRepository;
    }

    @Override
    public List<TaskProjection> getTasksByUserId(Long userId) {
        // Truy vấn các TaskProjection từ repository
        List<TaskProjection> tasks = userTaskAssignmentRepository.findTasksByUserId(userId);
        tasks.forEach(task -> System.out.println("Task title: " + task.getTitle()));

        // Kiểm tra nếu không có task
        if (tasks.isEmpty()) {
            return new ArrayList<>();
        }

        // Trả về danh sách TaskProjection
        return tasks;
    }
}
