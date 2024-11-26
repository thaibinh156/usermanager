package com.infodation.task_service.services;

import com.infodation.task_service.models.TaskCategory;
import com.infodation.task_service.repositories.TaskCategoryRepository;
import com.infodation.task_service.services.iServices.ITaskCategoryService;
import com.opencsv.CSVReader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class TaskCategoryServiceImpl implements ITaskCategoryService {

    private final TaskCategoryRepository taskCategoryRepository;

    public TaskCategoryServiceImpl(TaskCategoryRepository taskCategoryRepository) {
        this.taskCategoryRepository = taskCategoryRepository;
    }

    @Override
    public CompletableFuture<Void> importTaskCategoriesFromCSVFIle(MultipartFile file) throws Exception {
        List<TaskCategory> statuses = new ArrayList<>();

        Set<String> taskCategoryNameInDbSet = taskCategoryRepository.getAllCategoryName();

        try {
            CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()));
            List<String[]> rows = reader.readAll();

            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (taskCategoryNameInDbSet.contains(row[1]))
                    throw new Exception("Categories is existed");
                else {
                    TaskCategory newCategory = new TaskCategory();

                    newCategory.setId(Long.parseLong(row[0]));
                    newCategory.setName(row[1]);
                    newCategory.setDescription(row[2]);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    Date date = formatter.parse(row[3].substring(0, 23));
                    newCategory.setCreatedAt(date);

                    statuses.add(newCategory);
                }

            }

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
