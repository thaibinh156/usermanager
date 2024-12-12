package com.infodation.task_service.services;

import com.infodation.task_service.models.TaskStatus;
import com.infodation.task_service.repositories.TaskStatusRepository;
import com.infodation.task_service.services.iServices.ITaskStatusService;
import com.infodation.task_service.utils.ImportCSVHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.text.SimpleDateFormat;

import java.util.concurrent.CompletableFuture;

@Service
public class TaskStatusServiceImpl implements ITaskStatusService {

    private final TaskStatusRepository taskStatusRepository;
    private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);
    private final SimpleDateFormat dateFormatter;

    final int ID_ROW_INDEX = 0;
    final int NAME_ROW_INDEX = 1;
    final int DESCRIPTION_ROW_INDEX = 2;
    final int CREATED_AT_ROW_INDEX = 3;

    public TaskStatusServiceImpl(TaskStatusRepository repository, SimpleDateFormat dateFormatter) {
        this.taskStatusRepository = repository;
        this.dateFormatter = dateFormatter;
    }

    @Async
    public CompletableFuture<Void> importTaskStatusesFromCSVFIle(MultipartFile file) throws Exception {

        ImportCSVHandler<TaskStatus> importCSVHandler = new ImportCSVHandler<>();
        importCSVHandler.readAndSaveCSV(taskStatusRepository, file ,this::mapRowToData);

        return CompletableFuture.allOf();
    }

    @Override
    public Optional<TaskStatus> getStatusById(Long id) {
        return taskStatusRepository.findById(id);
    }

    protected TaskStatus mapRowToData(String[] row) throws Exception {
        String name = row[NAME_ROW_INDEX].toUpperCase();

        if (taskStatusRepository.existsByName(name)) {
            log.error("Status named {} is exist",name);
            return null;
        }

        TaskStatus newStatus = new TaskStatus();

        newStatus.setId(Long.parseLong(row[ID_ROW_INDEX]));
        newStatus.setName(name);
        newStatus.setDescription(row[DESCRIPTION_ROW_INDEX]);
        Date date = dateFormatter.parse(row[CREATED_AT_ROW_INDEX].substring(0, 23));
        newStatus.setCreatedAt(date);

        return newStatus;
    }
}
