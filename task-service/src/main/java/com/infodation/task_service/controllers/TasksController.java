package com.infodation.task_service.controllers;

import com.infodation.task_service.models.TaskProjection;
import com.infodation.task_service.models.dto.TaskAssignmentDTO;
import com.infodation.task_service.services.iServices.ITaskService;
import com.infodation.task_service.utils.ApiResponse;
import com.infodation.task_service.utils.ApiResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TasksController {

    private final ITaskService taskService;
    private static final Logger log = LoggerFactory.getLogger(TasksController.class);

    public TasksController(ITaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/assign")
    public ResponseEntity<String> assignTaskToUser(@RequestBody TaskAssignmentDTO taskAssignmentDTO) {
        try {
            log.info("Assigning task {} to user {}", taskAssignmentDTO.getTaskId(), taskAssignmentDTO.getUserId());
            // Save assignment to database
            taskService.assignTaskToUser(taskAssignmentDTO);
            return ResponseEntity.ok("Task assigned successfully.");
        } catch (Exception e) {
            log.error("Error occurred while assigning task: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while assigning task.");
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskProjection>> getTasksByUserId(@PathVariable Long userId) {
        log.info("Received request to get tasks for user with ID: {}", userId);
        try {
            // Retrieve the list of tasks from TaskService
            List<TaskProjection> tasks = taskService.getTasksByUserId(userId);
            // If no tasks are found
            if (tasks.isEmpty()) {
                log.warn("No tasks found for user with ID: {}", userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            log.info("Successfully retrieved tasks for user with ID: {}", userId);
            // Return the list of tasks
            return ResponseEntity.ok(tasks);

        } catch (Exception e) {
            // If an error occurs
            log.error("Error occurred while fetching tasks for user with ID: {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/migrate")
    public ResponseEntity<ApiResponse<?>> importTasks(@RequestParam("file") MultipartFile file) throws Exception{
        log.info("Starting import process for file: '{}'", file.getOriginalFilename());
        String message;
        HttpStatus status;

        if (file == null || file.isEmpty()) {
            message = ("The uploaded file is empty.");
            status = HttpStatus.BAD_REQUEST;

            log.error(message);
        } else {
            taskService.importTaskFromCSVFile(file);
            status = HttpStatus.OK;
            message = "Upload file successfully";
            log.info(message);
        }
        ApiResponse<?> response = ApiResponseUtil.buildApiResponse(null, HttpStatus.OK, message, null);
        return new ResponseEntity<>(response, status);
    }
}
