package com.infodation.task_service.services;

import com.infodation.task_service.models.TaskStatus;
import com.infodation.task_service.repositories.TaskStatusRepository;
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
public class TaskStatusServiceImpl implements ITaskStatusService {

    private final TaskStatusRepository taskStatusRepository;
    private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    public TaskStatusServiceImpl(TaskStatusRepository repository) {
        this.taskStatusRepository = repository;
    }

    @Async
    public CompletableFuture<Void> importTaskStatusesFromCSVFIle(MultipartFile file) throws Exception {
        List<TaskStatus> statuses = new ArrayList<>();

        Set<String> taskStatusNameInDbSet = taskStatusRepository.getAllTaskStatusName();

        final int ID_ROW_INDEX = 0;
        final int NAME_ROW_INDEX = 1;
        final int DESCRIPTION_ROW_INDEX = 2;
        final int CREATED_AT_ROW_INDEX = 3;

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVReader csvReader = new CSVReader(bufferedReader)) {

            List<String[]> rows = csvReader.readAll();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (taskStatusNameInDbSet.contains(row[1]))
                {
                    log.warn("Status is existed");
                    throw new Exception("Status is existed");
                }
                else {
                    TaskStatus newStatus = new TaskStatus();

                    newStatus.setId(Long.parseLong(row[ID_ROW_INDEX]));
                    newStatus.setName(row[NAME_ROW_INDEX]);
                    newStatus.setDescription(row[DESCRIPTION_ROW_INDEX]);
                    Date date = formatter.parse(row[CREATED_AT_ROW_INDEX].substring(0, 23));
                    newStatus.setCreatedAt(date);

                    statuses.add(newStatus);
                }

            }
            log.info("Finished reading file. Total valid lines: {}", rows.size());
            taskStatusRepository.saveAll(statuses);

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
}
