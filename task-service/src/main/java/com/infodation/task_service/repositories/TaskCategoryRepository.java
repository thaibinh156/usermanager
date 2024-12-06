package com.infodation.task_service.repositories;

import com.infodation.task_service.models.TaskCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface TaskCategoryRepository extends JpaRepository<TaskCategory, Long> {

    @Query("SELECT c.name FROM TaskCategory c")
    Set<String> getAllCategoryName();

    boolean existsByName(String name);
}
