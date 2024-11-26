package com.infodation.task_service.controllers;

import com.infodation.task_service.services.iServices.ITaskService;
import com.infodation.task_service.utils.ApiResponse;
import com.infodation.task_service.utils.ApiResponseUtil;
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

    public TasksController(ITaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/migrate")
    public ResponseEntity<ApiResponse<?>> importTasks(@RequestParam("file") MultipartFile file) throws Exception{

        HttpStatus status;
        ApiResponse<?> response;

        if (file.isEmpty()) {
            status = HttpStatus.BAD_REQUEST;
            response = ApiResponseUtil.buildApiResponse(null, HttpStatus.BAD_REQUEST, "File is empty", null);
        } else {
            status = HttpStatus.OK;
            response = taskService.importTaskFromCSVFile(file);

        }

        return new ResponseEntity<>(response, status);
    }
}
