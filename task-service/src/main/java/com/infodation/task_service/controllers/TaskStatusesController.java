package com.infodation.task_service.controllers;

import com.infodation.task_service.services.iServices.ITaskStatusService;
import com.infodation.task_service.utils.ApiResponse;
import com.infodation.task_service.utils.ApiResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/task-statuses")
public class TaskStatusesController {

    private final ITaskStatusService taskStatusService;

    public TaskStatusesController(ITaskStatusService taskStatusService){
        this.taskStatusService = taskStatusService;
    }

    @GetMapping
    public String hello(){
        return "Hello";
    }

    @PostMapping("/migrate")
    public ResponseEntity<ApiResponse<?>> importStatuses(@RequestParam("file") MultipartFile file) throws Exception {

        String message;
        HttpStatus status;

        if (file.isEmpty()) {
            message = "File is empty";
            status = HttpStatus.BAD_REQUEST;
        } else {
            taskStatusService.importTaskStatusesFromCSVFIle(file);
            message = "Imported status into Database";
            status = HttpStatus.OK;
        }

        ApiResponse<String> response = ApiResponseUtil.buildApiResponse(null, status, message, null);
        return new ResponseEntity<>(response, status);
    }
}
