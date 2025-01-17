package com.infodation.task_service.services;

import com.infodation.task_service.models.*;
import com.infodation.task_service.models.dto.TaskAssignmentDTO;
import com.infodation.task_service.models.dto.TaskCreateDTO;
import com.infodation.task_service.repositories.TaskAssignmentRepository;
import com.infodation.task_service.components.BadRequestException;
import com.infodation.task_service.models.TaskProjection;
import com.infodation.task_service.repositories.TaskServiceRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.infodation.task_service.utils.ImportCSVHandler;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class TaskServiceImpl implements ITaskService {

    private final TaskRepository taskRepository;
    private final ITaskCategoryService taskCategoryService;
    private final ITaskStatusService taskStatusService;
    private  final TaskServiceRepository taskServiceRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final RabbitTemplate rabbitTemplate;
    private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final SimpleDateFormat dueDateFormatter;
    private final SimpleDateFormat dateFormatter;

    final int TITLE_ROW_INDEX = 1;
    final int DESCRIPTION_ROW_INDEX = 2;
    final int CATEGORY_ROW_INDEX = 3;
    final int STATUS_ROW_INDEX = 4;
    final int DUE_DATE_ROW_INDEX = 5;
    final int PRIORITY_ROW_INDEX = 6;
    final int CREATED_AT_ROW_INDEX = 7;
    final int UPDATED_AT_ROW_INDEX = 8;

    public TaskServiceImpl(TaskRepository taskRepository, ITaskCategoryService taskCategoryService,
                           ITaskStatusService taskStatusService,
                           TaskServiceRepository taskServiceRepository,
                           TaskAssignmentRepository taskAssignmentRepository,
                           RabbitTemplate rabbitTemplate,
                           SimpleDateFormat dueDateFormatter,
                           SimpleDateFormat dateFormatter) {
        this.taskRepository = taskRepository;
        this.taskCategoryService = taskCategoryService;
        this.taskStatusService = taskStatusService;
        this.taskServiceRepository = taskServiceRepository;
        this.taskAssignmentRepository = taskAssignmentRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.dueDateFormatter = dueDateFormatter;
        this.dateFormatter = dateFormatter;
    }

    public void assignTaskToUser(TaskAssignmentDTO taskAssignmentDTO) {
        try {
            Task task = taskRepository.findById(taskAssignmentDTO.getTaskId())
                    .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskAssignmentDTO.getTaskId()));
            UserTaskAssignment assignment = new UserTaskAssignment();
            assignment.setUserId(taskAssignmentDTO.getUserId());
            assignment.setTask(task);
            taskAssignmentRepository.save(assignment);
            sendNotificationToRabbitMQ(taskAssignmentDTO);
        } catch (Exception e) {
            log.error("Error occurred while assigning task: ", e);
            throw new RuntimeException("Error occurred while assigning task", e);
        }
    }
    public void sendNotificationToRabbitMQ(TaskAssignmentDTO message) {
        rabbitTemplate.convertAndSend("sendNotificationToRabbitMQ", message);
        log.debug("Messages sent: -----> " + message);
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
        ImportCSVHandler<Task> importCSVHandler = new ImportCSVHandler<>();
        importCSVHandler.readAndSaveCSV(taskRepository, file, this::mapRowToTask);

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

    public Task saveTask(TaskCreateDTO task) {
        Task newTask = new Task();
        newTask.setTitle(task.getTitle());
        newTask.setDescription(task.getDescription());
        newTask.setCategory(taskCategoryService.getCategoryById(task.getCategoryId()).orElse(null));
        newTask.setStatus(taskStatusService.getStatusById(task.getStatusId()).orElse(null));
        newTask.setPriority(Priority.valueOf(task.getPriority().toUpperCase()));
        newTask.setCreatedBy(task.getUserId());
        newTask.setCreatedAt(new Date());
        newTask.setUpdatedAt(new Date());
        newTask.setPriority(Priority.valueOf(task.getPriority().toUpperCase()));
        return taskRepository.save(newTask);
    }

    public Task updateTask(Long taskId, TaskCreateDTO task) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new BadRequestException("Task not found with ID: " + taskId));
        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setCategory(taskCategoryService.getCategoryById(task.getCategoryId()).orElse(null));
        existingTask.setStatus(taskStatusService.getStatusById(task.getStatusId()).orElse(null));
        existingTask.setPriority(Priority.valueOf(task.getPriority().toUpperCase()));
        existingTask.setUpdatedAt(new Date());
        return taskRepository.save(existingTask);
    }
    

}

