package com.infodation.task_service.services;

import com.infodation.task_service.models.*;
import com.infodation.task_service.models.dto.TaskAssignmentDTO;
import com.infodation.task_service.repositories.TaskAssignmentRepository;
import com.infodation.task_service.repositories.TaskServiceRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import com.infodation.task_service.repositories.TaskRepository;
import com.infodation.task_service.services.iServices.ITaskCategoryService;
import com.infodation.task_service.services.iServices.ITaskService;
import com.infodation.task_service.services.iServices.ITaskStatusService;
import com.infodation.task_service.utils.ApiResponse;
import com.infodation.task_service.utils.ApiResponseUtil;
import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TaskServiceImpl implements ITaskService {
    private final TaskRepository taskRepository;
    private final ITaskCategoryService taskCategoryService;
    private final ITaskStatusService taskStatusService;
    private  final TaskServiceRepository taskServiceRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final RabbitTemplate rabbitTemplate;
    private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    public TaskServiceImpl(TaskRepository taskRepository, ITaskCategoryService taskCategoryService, ITaskStatusService taskStatusService, TaskServiceRepository taskServiceRepository, TaskAssignmentRepository taskAssignmentRepository, RabbitTemplate rabbitTemplate) {
        this.taskRepository = taskRepository;
        this.taskCategoryService = taskCategoryService;
        this.taskStatusService = taskStatusService;
        this.taskServiceRepository = taskServiceRepository;
        this.taskAssignmentRepository = taskAssignmentRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void assignTaskToUser(TaskAssignmentDTO taskAssignmentDTO) {
        try {
            Task task = taskRepository.findById(taskAssignmentDTO.getTaskId())
                    .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskAssignmentDTO.getTaskId()));
            UserTaskAssignment assignment = new UserTaskAssignment();
            assignment.setUserId(taskAssignmentDTO.getUserId());
            assignment.setTask(task);
            sendNotificationToRabbitMQ(taskAssignmentDTO);
            taskAssignmentRepository.save(assignment);
        } catch (Exception e) {
            log.error("Error occurred while assigning task: ", e);
            throw new RuntimeException("Error occurred while assigning task", e);
        }
    }
    public void sendNotificationToRabbitMQ(TaskAssignmentDTO message) {
        rabbitTemplate.convertAndSend("sendNotification", message);
        log.info("Messages sent: -----> " + message);
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
    public ApiResponse<?> importTaskFromCSVFile(MultipartFile file) throws Exception {
        List<Task> tasks = new ArrayList<>();
        String message;
        HttpStatus status;

        final int TITLE_ROW_INDEX = 1;
        final int DESCRIPTION_ROW_INDEX = 2;
        final int CATEGORY_ROW_INDEX = 3;
        final int STATUS_ROW_INDEX = 4;
        final int DUE_DATE_ROW_INDEX = 5;
        final int PRIORITY_ROW_INDEX = 6;
        final int CREATED_AT_ROW_INDEX = 7;
        final int UPDATED_AT_ROW_INDEX = 8;

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVReader csvReader = new CSVReader(bufferedReader)) {

            List<String[]> rows = csvReader.readAll();
            SimpleDateFormat dueDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                Optional<TaskCategory> category = Optional.empty();
                Optional<TaskStatus> taskStatus = Optional.empty();

                if (!row[CATEGORY_ROW_INDEX].isEmpty())
                    category = taskCategoryService.getCategoryById((long) Double.parseDouble(row[3]));
                if (!row[STATUS_ROW_INDEX].isEmpty())
                    taskStatus = taskStatusService.getStatusById(Long.parseLong(row[4]));

                if (taskStatus.isEmpty()) {
                    message = "Status not found at row " + i;
                    status = HttpStatus.NOT_FOUND;
                    log.error(message);
                    return ApiResponseUtil.buildApiResponse(null, status, message, null);
                }

                Task newTask = new Task();

                newTask.setTitle(row[TITLE_ROW_INDEX]);
                newTask.setDescription(row[DESCRIPTION_ROW_INDEX]);

                newTask.setCategory(category.isEmpty() ? null : category.get());
                newTask.setStatus(taskStatus.get());


                Date dueDate = row[DUE_DATE_ROW_INDEX].isEmpty()? null: dueDateFormatter.parse(row[5]);
                newTask.setDueDate(dueDate);

                newTask.setPriority(Priority.valueOf(row[PRIORITY_ROW_INDEX].toUpperCase()));

                Date createAt = dateFormatter.parse(row[CREATED_AT_ROW_INDEX].substring(0, 23));
                newTask.setCreatedAt(createAt);
                Date updateAt = dateFormatter.parse(row[UPDATED_AT_ROW_INDEX].substring(0, 23));
                newTask.setCreatedAt(updateAt);
                tasks.add(newTask);
            }
            log.info("Finished reading file. Total valid lines: {}", rows.size());

            taskRepository.saveAll(tasks);
            status = HttpStatus.OK;
            message = String.format("Import file '%s' successfully", file.getOriginalFilename());
            log.info(message);

        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new Exception(ex.getMessage());
        }

        return ApiResponseUtil.buildApiResponse(null, status, message, null);
    }
}

