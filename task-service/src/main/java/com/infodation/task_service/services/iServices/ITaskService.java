package com.infodation.task_service.services.iServices;

import com.infodation.task_service.utils.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

public interface ITaskService {
    ApiResponse<?> importTaskFromCSVFile(MultipartFile csv) throws Exception;
}
