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

        HttpStatus status;
        ApiResponse<?> response;

        if (file == null || file.isEmpty()) {
            log.error("The uploaded file is empty.");
            status = HttpStatus.BAD_REQUEST;
            response = ApiResponseUtil.buildApiResponse(null, HttpStatus.BAD_REQUEST, "File is empty", null);
        } else {
            status = HttpStatus.OK;
            response = taskService.importTaskFromCSVFile(file);
            log.info("Import file '{}' successfully", file.getOriginalFilename());
        }
        return new ResponseEntity<>(response, status);
    }
}
