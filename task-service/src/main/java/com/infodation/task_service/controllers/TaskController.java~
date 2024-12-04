package com.infodation.task_service.controllers;

import com.infodation.task_service.models.TaskProjection;
import com.infodation.task_service.models.dto.TaskAssignmentDTO;
import com.infodation.task_service.services.iServices.ITaskService;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import java.util.List;
import org.slf4j.Logger;


@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final ITaskService taskService;
    private final RabbitTemplate rabbitTemplate;

    public TaskController(ITaskService taskService, RabbitTemplate rabbitTemplate) {
        this.taskService = taskService;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendNotificationToUser(TaskAssignmentDTO message) {
        rabbitTemplate.convertAndSend("sendNotification", message);
        logger.info("Messages sent: -----> " + message);

    }
    @PostMapping("/assign")
    public ResponseEntity<String> assignTaskToUser(@RequestBody TaskAssignmentDTO taskAssignmentDTO) {
        try {
            logger.info("Assigning task {} to user {}", taskAssignmentDTO.getTaskId(), taskAssignmentDTO.getUserId());

            // Save assignment to database
            taskService.assignTaskToUser(taskAssignmentDTO);

            sendNotificationToUser(taskAssignmentDTO);
            return ResponseEntity.ok("Task assigned successfully.");
        } catch (Exception e) {
            logger.error("Error occurred while assigning task: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while assigning task.");
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskProjection>> getTasksByUserId(@PathVariable Long userId) {
        logger.info("Received request to get tasks for user with ID: {}", userId);
        try {
            // Retrieve the list of tasks from TaskService
            List<TaskProjection> tasks = taskService.getTasksByUserId(userId);

            // If no tasks are found
            if (tasks.isEmpty()) {
                logger.warn("No tasks found for user with ID: {}", userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            logger.info("Successfully retrieved tasks for user with ID: {}", userId);
            // Return the list of tasks
            return ResponseEntity.ok(tasks);

        } catch (Exception e) {
            // If an error occurs
            logger.error("Error occurred while fetching tasks for user with ID: {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

