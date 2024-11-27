package com.infodation.task_service.services;

import com.infodation.task_service.models.Priority;
import com.infodation.task_service.models.Task;
import com.infodation.task_service.models.TaskCategory;
import com.infodation.task_service.models.TaskStatus;
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
import org.springframework.stereotype.Service;
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
    private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    public TaskServiceImpl(TaskRepository taskRepository, ITaskCategoryService taskCategoryService, ITaskStatusService taskStatusService) {
        this.taskRepository = taskRepository;
        this.taskCategoryService = taskCategoryService;
        this.taskStatusService = taskStatusService;
    }

    @Override
    @Async
    public ApiResponse<?> importTaskFromCSVFile(MultipartFile file) throws Exception {
        List<Task> tasks = new ArrayList<>();
        String message;
        HttpStatus status;

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVReader csvReader = new CSVReader(bufferedReader)) {

            List<String[]> rows = csvReader.readAll();
            SimpleDateFormat dueDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                Optional<TaskCategory> category = Optional.empty();
                Optional<TaskStatus> taskStatus = Optional.empty();

                if (!row[3].isEmpty())
                    category = taskCategoryService.getCategoryById((long) Double.parseDouble(row[3]));
                if (!row[4].isEmpty())
                    taskStatus = taskStatusService.getStatusById(Long.parseLong(row[4]));

                if (taskStatus.isEmpty()) {
                    message = "Status not found at row " + i;
                    log.error("Status not found at row " + i);
                    status = HttpStatus.NOT_FOUND;
                    return ApiResponseUtil.buildApiResponse(null, status, message, null);
                }

                Task newTask = new Task();

                newTask.setTitle(row[1]);
                newTask.setDescription(row[2]);

                newTask.setCategory(category.isEmpty() ? null : category.get());
                newTask.setStatus(taskStatus.get());


                Date dueDate = row[5].isEmpty()? null: dueDateFormatter.parse(row[5]);
                newTask.setDueDate(dueDate);

                newTask.setPriority(Priority.valueOf(row[6].toUpperCase()));

                Date createAt = dateFormatter.parse(row[7].substring(0, 23));
                newTask.setCreatedAt(createAt);
                Date updateAt = dateFormatter.parse(row[8].substring(0, 23));
                newTask.setCreatedAt(updateAt);
                log.info("Read row " + i + " successfully");
                tasks.add(newTask);
            }
            log.info("Finished reading file. Total valid lines: {}", rows.size());

            taskRepository.saveAll(tasks);
            status = HttpStatus.OK;
            message = "Imported Tasks into Database";

        } catch (Exception ex) {
            throw new Exception(ex);
        }

        return ApiResponseUtil.buildApiResponse(null, status, message, null);

    }
}

