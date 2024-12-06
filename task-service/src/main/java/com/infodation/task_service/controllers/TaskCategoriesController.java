package com.infodation.task_service.controllers;

import com.infodation.task_service.services.iServices.ITaskCategoryService;
import com.infodation.task_service.utils.ApiResponse;
import com.infodation.task_service.utils.ApiResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.Console;

@RestController
@RequestMapping("/api/task-categories")
public class TaskCategoriesController {

    private final ITaskCategoryService taskCategoryService;
    private static final Logger log = LoggerFactory.getLogger(TasksController.class);

    public TaskCategoriesController(ITaskCategoryService taskCategoryService) {
        this.taskCategoryService = taskCategoryService;
    }

    @GetMapping
    public String Demo() {
        return "Hello world";
    }

    @PostMapping("/migrate")
    public ResponseEntity<ApiResponse<?>> importCategories(@RequestParam("file") MultipartFile file) throws Exception {
        log.info("Starting import process for file: '{}'", file.getOriginalFilename());

        String message;
        HttpStatus status;

        if (file == null || file.isEmpty()) {
            message = "The uploaded file is empty.";
            status = HttpStatus.BAD_REQUEST;
            log.error(message);
        } else {
            taskCategoryService.importTaskCategoriesFromCSVFIle(file);
            message = String.format("Import file '%s' successfully", file.getOriginalFilename());
            status = HttpStatus.OK;
            log.info(message);
        }

        ApiResponse<String> response = ApiResponseUtil.buildApiResponse(null, status, message, null);
        return new ResponseEntity<>(response, status);
    }
}
