package com.infodation.task_service.services.iServices;

import com.infodation.task_service.models.TaskCategory;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ITaskCategoryService {
    CompletableFuture<Void> importTaskCategoriesFromCSVFIle(MultipartFile file) throws Exception;

    Optional<TaskCategory> getCategoryById(Long id);
}
