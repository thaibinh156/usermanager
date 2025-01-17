package com.infodation.task_service.services.iServices;

import com.infodation.task_service.models.Task;
import com.infodation.task_service.models.TaskProjection;
import com.infodation.task_service.models.dto.TaskAssignmentDTO;
import com.infodation.task_service.models.dto.TaskAssignmentDTO;
import com.infodation.task_service.models.dto.TaskCreateDTO;
import com.infodation.task_service.utils.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ITaskService {
    CompletableFuture<Void> importTaskFromCSVFile(MultipartFile csv) throws Exception;
    List<TaskProjection> getTasksByUserId(Long userId);
    void assignTaskToUser(TaskAssignmentDTO taskAssignmentDTO);
    Task saveTask(TaskCreateDTO task);
}
