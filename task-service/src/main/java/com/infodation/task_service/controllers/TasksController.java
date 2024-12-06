package com.infodation.task_service.controllers;

import com.infodation.task_service.services.iServices.ITaskService;
import com.infodation.task_service.utils.ApiResponse;
import com.infodation.task_service.utils.ApiResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/tasks")
public class TasksController {

    private final ITaskService taskService;
    private static final Logger log = LoggerFactory.getLogger(TasksController.class);

    public TasksController(ITaskService taskService) {
        this.taskService = taskService;
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
