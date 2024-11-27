package com.infodation.task_service.controllers;

import com.infodation.task_service.services.iServices.ITaskStatusService;
import com.infodation.task_service.utils.ApiResponse;
import com.infodation.task_service.utils.ApiResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/task-statuses")
public class TaskStatusesController {

    private final ITaskStatusService taskStatusService;
    private static final Logger log = LoggerFactory.getLogger(TasksController.class);

    public TaskStatusesController(ITaskStatusService taskStatusService){
        this.taskStatusService = taskStatusService;
    }

    @GetMapping
    public String hello(){
        return "Hello";
    }

    @PostMapping("/migrate")
    public ResponseEntity<ApiResponse<?>> importStatuses(@RequestParam("file") MultipartFile file) throws Exception {
        log.info("Starting import process for file: '{}'", file.getOriginalFilename());
        String message;
        HttpStatus status;

        if (file != null || file.isEmpty()) {
            log.error("The uploaded file is empty.");
            message = "File is empty";
            status = HttpStatus.BAD_REQUEST;
        } else {
            taskStatusService.importTaskStatusesFromCSVFIle(file);
            log.info("Import file '{}' successfully", file.getOriginalFilename());
            message = "Imported status into Database";
            status = HttpStatus.OK;
        }

        ApiResponse<String> response = ApiResponseUtil.buildApiResponse(null, status, message, null);

        return new ResponseEntity<>(response, status);
    }
}
