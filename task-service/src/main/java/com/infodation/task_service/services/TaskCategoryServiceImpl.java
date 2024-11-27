package com.infodation.task_service.services;

import com.infodation.task_service.models.TaskCategory;
import com.infodation.task_service.repositories.TaskCategoryRepository;
import com.infodation.task_service.services.iServices.ITaskCategoryService;
import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class TaskCategoryServiceImpl implements ITaskCategoryService {

    private final TaskCategoryRepository taskCategoryRepository;
    private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    public TaskCategoryServiceImpl(TaskCategoryRepository taskCategoryRepository) {
        this.taskCategoryRepository = taskCategoryRepository;
    }

    @Override
    @Async
    public CompletableFuture<Void> importTaskCategoriesFromCSVFIle(MultipartFile file) throws Exception {
        List<TaskCategory> statuses = new ArrayList<>();

        Set<String> taskCategoryNameInDbSet = taskCategoryRepository.getAllCategoryName();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVReader csvReader = new CSVReader(bufferedReader)) {
            List<String[]> rows = csvReader.readAll();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (taskCategoryNameInDbSet.contains(row[1]))
                {
                    log.warn("Categories is existed");
                    throw new Exception("Categories is existed");
                }
                else {
                    TaskCategory newCategory = new TaskCategory();
                    newCategory.setId(Long.parseLong(row[0]));
                    newCategory.setName(row[1]);
                    newCategory.setDescription(row[2]);
                    Date date = formatter.parse(row[3].substring(0, 23));
                    newCategory.setCreatedAt(date);

                    log.info("Read row " + i + " successfully");
                    statuses.add(newCategory);
                }

            }
            log.info("Finished reading file. Total valid lines: {}", rows.size());
            taskCategoryRepository.saveAll(statuses);

        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }

        return CompletableFuture.allOf();
    }

    @Override
    public Optional<TaskCategory> getCategoryById(Long id) {
        return taskCategoryRepository.findById(id);
    }
}
