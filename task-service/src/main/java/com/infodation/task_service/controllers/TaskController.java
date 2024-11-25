package com.infodation.task_service.controllers;

import com.infodation.task_service.models.DTO.TaskDTO;
import com.infodation.task_service.models.TaskProjection;
import com.infodation.task_service.services.ITaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/tasks")
public class TaskController {
    private final ITaskService iTaskService;

    public TaskController(ITaskService iTaskService) {
        this.iTaskService = iTaskService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskProjection>> getTasksByUserId(@PathVariable Long userId) {
        try {
            // Lấy danh sách task từ TaskService
            List<TaskProjection> tasks = iTaskService.getTasksByUserId(userId);

            // Nếu không có task
            if (tasks.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Trả về danh sách task
            return ResponseEntity.ok(tasks);

        } catch (Exception e) {
            // Nếu có lỗi xảy ra
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

