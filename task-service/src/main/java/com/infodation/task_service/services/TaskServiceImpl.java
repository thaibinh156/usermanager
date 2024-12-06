package com.infodation.task_service.services;

import com.infodation.task_service.components.BadRequestException;
import com.infodation.task_service.models.TaskProjection;
import com.infodation.task_service.repositories.TaskServiceRepository;
import com.infodation.task_service.utils.ImportCSVUtil;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import com.infodation.task_service.models.Priority;
import com.infodation.task_service.models.Task;
import com.infodation.task_service.models.TaskCategory;
import com.infodation.task_service.models.TaskStatus;
import com.infodation.task_service.repositories.TaskRepository;
import com.infodation.task_service.services.iServices.ITaskCategoryService;
import com.infodation.task_service.services.iServices.ITaskService;
import com.infodation.task_service.services.iServices.ITaskStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class TaskServiceImpl implements ITaskService {

    private final TaskRepository taskRepository;
    private final ITaskCategoryService taskCategoryService;
    private final ITaskStatusService taskStatusService;
    private  final TaskServiceRepository taskServiceRepository;
    private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    SimpleDateFormat dueDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    final int TITLE_ROW_INDEX = 1;
    final int DESCRIPTION_ROW_INDEX = 2;
    final int CATEGORY_ROW_INDEX = 3;
    final int STATUS_ROW_INDEX = 4;
    final int DUE_DATE_ROW_INDEX = 5;
    final int PRIORITY_ROW_INDEX = 6;
    final int CREATED_AT_ROW_INDEX = 7;
    final int UPDATED_AT_ROW_INDEX = 8;

    public TaskServiceImpl(TaskRepository taskRepository, ITaskCategoryService taskCategoryService, ITaskStatusService taskStatusService, TaskServiceRepository taskServiceRepository) {
        this.taskRepository = taskRepository;
        this.taskCategoryService = taskCategoryService;
        this.taskStatusService = taskStatusService;
        this.taskServiceRepository = taskServiceRepository;
    }

    @Override
    public List<TaskProjection> getTasksByUserId(Long userId) {
        // Query TaskProjection objects from the repository
        List<TaskProjection> tasks = taskServiceRepository.findTasksByUserId(userId);
        // Check if no tasks are found
        if (tasks.isEmpty()) {
            return new ArrayList<>();
        }
        // Return the list of TaskProjection objects
        return tasks;
    }

    @Override
    @Async
    public CompletableFuture<Void> importTaskFromCSVFile(MultipartFile file) throws Exception {
        ImportCSVUtil<Task> importCSVUtil = new ImportCSVUtil<>();
        importCSVUtil.readAndSaveCSV(taskRepository, file, this::mapRowToTask);

        return CompletableFuture.allOf();
    }

    protected Task mapRowToTask(String[] row) throws Exception {
        Optional<TaskCategory> category = Optional.empty();
        Optional<TaskStatus> taskStatus;

        long statusId;

        if (!row[CATEGORY_ROW_INDEX].isEmpty())
            category = taskCategoryService.getCategoryById((long) Double.parseDouble(row[CATEGORY_ROW_INDEX]));
        if (!row[STATUS_ROW_INDEX].isEmpty()) {
            statusId = Long.parseLong(row[STATUS_ROW_INDEX]);
            taskStatus = taskStatusService.getStatusById(statusId);
        } else {
            log.error("StatusId is empty");
            throw new BadRequestException("StatusId is empty");
        }

        if (taskStatus.isEmpty()) {
            log.error("Status Id {} is not exist", statusId);
            throw new BadRequestException("Status Id " +  statusId + " is not exist");
        }

        Task newTask = new Task();

        newTask.setTitle(row[TITLE_ROW_INDEX]);
        newTask.setDescription(row[DESCRIPTION_ROW_INDEX]);

        newTask.setCategory(category.orElse(null));
        newTask.setStatus(taskStatus.get());


        Date dueDate = row[DUE_DATE_ROW_INDEX].isEmpty()? null: dueDateFormatter.parse(row[DUE_DATE_ROW_INDEX]);
        newTask.setDueDate(dueDate);

        newTask.setPriority(Priority.valueOf(row[PRIORITY_ROW_INDEX].toUpperCase()));

        Date createAt = dateFormatter.parse(row[CREATED_AT_ROW_INDEX].substring(0, 23));
        newTask.setCreatedAt(createAt);
        Date updateAt = dateFormatter.parse(row[UPDATED_AT_ROW_INDEX].substring(0, 23));
        newTask.setUpdatedAt(updateAt);

        return newTask;
    }
}

