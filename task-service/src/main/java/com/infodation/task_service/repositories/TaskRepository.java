package com.infodation.task_service.repositories;

import com.infodation.task_service.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

}
