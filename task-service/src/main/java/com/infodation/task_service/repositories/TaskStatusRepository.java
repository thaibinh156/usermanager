package com.infodation.task_service.repositories;

import com.infodation.task_service.models.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {

    @Query("SELECT s.name FROM TaskStatus s")
    public Set<String> getAllTaskStatusName();
}
