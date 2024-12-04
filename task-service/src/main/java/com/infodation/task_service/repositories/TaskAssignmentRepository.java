package com.infodation.task_service.repositories;

import com.infodation.task_service.models.Task;
import com.infodation.task_service.models.UserTaskAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskAssignmentRepository extends JpaRepository<UserTaskAssignment, Long> {

}
