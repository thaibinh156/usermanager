package com.infodation.task_service.services;

import com.infodation.task_service.models.TaskStatus;
import com.infodation.task_service.repositories.TaskStatusRepository;
import com.infodation.task_service.services.absservices.AbstractImportCSV;
import com.infodation.task_service.services.iServices.ITaskStatusService;
import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.text.SimpleDateFormat;

import java.util.concurrent.CompletableFuture;

@Service
public class TaskStatusServiceImpl extends AbstractImportCSV<TaskStatus> implements ITaskStatusService {

    private final TaskStatusRepository taskStatusRepository;
    private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    final int ID_ROW_INDEX = 0;
    final int NAME_ROW_INDEX = 1;
    final int DESCRIPTION_ROW_INDEX = 2;
    final int CREATED_AT_ROW_INDEX = 3;

    public TaskStatusServiceImpl(TaskStatusRepository repository) {
        this.taskStatusRepository = repository;
    }

    @Async
    public CompletableFuture<Void> importTaskStatusesFromCSVFIle(MultipartFile file) throws Exception {
        List<TaskStatus> statuses;

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVReader csvReader = new CSVReader(bufferedReader)) {
            List<String[]> rows = csvReader.readAll();
            statuses = this.readAndSaveCSV(rows);
            if (statuses.size() > 0) {
                log.info("Finished reading file. Total valid lines: {}", statuses.size());
                taskStatusRepository.saveAll(statuses);
            } else {
                log.info("Can not save Status");
            }

        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new Exception(ex.getMessage());
        }

        return CompletableFuture.allOf();
    }

    @Override
    public Optional<TaskStatus> getStatusById(Long id) {
        return taskStatusRepository.findById(id);
    }

    @Override
    protected TaskStatus mappingValue(String[] row) throws Exception {

        if (taskStatusRepository.existsByName(row[NAME_ROW_INDEX])) {
            log.error("Status named {} is exist",row[NAME_ROW_INDEX]);
            return null;
        }

        TaskStatus newStatus = new TaskStatus();

        newStatus.setId(Long.parseLong(row[ID_ROW_INDEX]));
        newStatus.setName(row[NAME_ROW_INDEX]);
        newStatus.setDescription(row[DESCRIPTION_ROW_INDEX]);
        Date date = formatter.parse(row[CREATED_AT_ROW_INDEX].substring(0, 23));
        newStatus.setCreatedAt(date);

        return newStatus;
    }
}
