package com.infodation.task_service.services.iServices;

import com.infodation.task_service.models.TaskStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ITaskStatusService {

    CompletableFuture<Void> importTaskStatusesFromCSVFIle(MultipartFile file) throws Exception;
    Optional<TaskStatus> getStatusById(Long id);
}
