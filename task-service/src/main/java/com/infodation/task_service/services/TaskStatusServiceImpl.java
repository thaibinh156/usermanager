package com.infodation.task_service.services;

import com.infodation.task_service.models.TaskStatus;
import com.infodation.task_service.repositories.TaskStatusRepository;
import com.infodation.task_service.services.iServices.ITaskStatusService;
import com.opencsv.CSVReader;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.*;
import java.text.SimpleDateFormat;

import java.util.concurrent.CompletableFuture;

@Service
public class TaskStatusServiceImpl implements ITaskStatusService {

    private final TaskStatusRepository taskStatusRepository;

    public TaskStatusServiceImpl(TaskStatusRepository repository) {
        this.taskStatusRepository = repository;
    }

    @Async
    public CompletableFuture<Void> importTaskStatusesFromCSVFIle(MultipartFile file) throws Exception {
        List<TaskStatus> statuses = new ArrayList<>();

        Set<String> taskStatusNameInDbSet = taskStatusRepository.getAllTaskStatusName();

        try {
            CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()));
            List<String[]> rows = reader.readAll();

            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (taskStatusNameInDbSet.contains(row[1]))
                    throw new Exception("Status is existed");
                else {
                    TaskStatus newStatus = new TaskStatus();

                    newStatus.setId(Long.parseLong(row[0]));
                    newStatus.setName(row[1]);
                    newStatus.setDescription(row[2]);

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    Date date = formatter.parse(row[3].substring(0, 23));
                    newStatus.setCreatedAt(date);

                    statuses.add(newStatus);
                }

            }

            taskStatusRepository.saveAll(statuses);

        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }

        return CompletableFuture.allOf();
    }

    @Override
    public Optional<TaskStatus> getStatusById(Long id) {
        return taskStatusRepository.findById(id);
    }
}
