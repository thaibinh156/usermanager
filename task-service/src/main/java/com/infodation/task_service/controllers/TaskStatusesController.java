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

    @PostMapping("/migrate")
    public ResponseEntity<ApiResponse<?>> importStatuses(@RequestParam("file") MultipartFile file) throws Exception {
        log.info("Starting import process for file: '{}'", file.getOriginalFilename());

        String message;
        HttpStatus status;

        if (file == null || file.isEmpty()) {
            message = "The uploaded file is empty.";
            status = HttpStatus.BAD_REQUEST;
            log.error(message);
        } else {
            taskStatusService.importTaskStatusesFromCSVFIle(file);
            message = String.format("Import file '%s' successfully", file.getOriginalFilename());
            status = HttpStatus.OK;
            log.info(message);
        }

        ApiResponse<String> response = ApiResponseUtil.buildApiResponse(null, status, message, null);

        return new ResponseEntity<>(response, status);
    }
}
