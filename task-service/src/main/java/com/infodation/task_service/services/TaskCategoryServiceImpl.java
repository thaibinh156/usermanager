package com.infodation.task_service.services;

import com.infodation.task_service.models.TaskCategory;
import com.infodation.task_service.repositories.TaskCategoryRepository;
import com.infodation.task_service.services.iServices.ITaskCategoryService;
import com.infodation.task_service.utils.ImportCSVHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class TaskCategoryServiceImpl implements ITaskCategoryService {

    private final TaskCategoryRepository taskCategoryRepository;
    private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);
    private final SimpleDateFormat dateFormatter;

    final int ID_ROW_INDEX = 0;
    final int NAME_ROW_INDEX = 1;
    final int DESCRIPTION_ROW_INDEX = 2;
    final int CREATED_AT_ROW_INDEX = 3;


    public TaskCategoryServiceImpl(TaskCategoryRepository taskCategoryRepository, SimpleDateFormat dateFormatter) {
        this.taskCategoryRepository = taskCategoryRepository;
        this.dateFormatter = dateFormatter;
    }

    @Override
    @Async
    public CompletableFuture<Void> importTaskCategoriesFromCSVFIle(MultipartFile file) throws Exception {
        ImportCSVHandler<TaskCategory> importCSVHandler = new ImportCSVHandler<>();
        importCSVHandler.readAndSaveCSV(taskCategoryRepository, file, this::rowToTaskCategory);

        return CompletableFuture.allOf();
    }

    @Override
    public Optional<TaskCategory> getCategoryById(Long id) {
        return taskCategoryRepository.findById(id);
    }

    private TaskCategory rowToTaskCategory(String[] row) throws Exception {
        TaskCategory newCategory = new TaskCategory();
        String name = row[NAME_ROW_INDEX];

        if (taskCategoryRepository.existsByName(name)) {
            log.error("Category named {} is exist",name);
            return null;
        }

        newCategory.setId(Long.parseLong(row[ID_ROW_INDEX]));
        newCategory.setName(name.toUpperCase());
        newCategory.setDescription(row[DESCRIPTION_ROW_INDEX]);
        Date date = dateFormatter.parse(row[CREATED_AT_ROW_INDEX].substring(0, 23));
        newCategory.setCreatedAt(date);

        return newCategory;
    }
}
