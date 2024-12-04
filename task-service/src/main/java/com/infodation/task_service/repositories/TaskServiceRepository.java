package com.infodation.task_service.repositories;

import com.infodation.task_service.models.TaskProjection;
import com.infodation.task_service.models.UserTaskAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TaskServiceRepository extends JpaRepository<UserTaskAssignment, Long> {

    @Query("SELECT t.id AS taskId, t.title AS title, c.name AS categoryName, c.description AS categoryDescription, s.name AS statusName, s.description AS statusDescription " +
            "FROM Task t " +
            "JOIN UserTaskAssignment uta ON t.id = uta.task.id " +
            "JOIN TaskCategory c ON t.category.id = c.id " +
            "JOIN TaskStatus s ON t.status.id = s.id " +
            "WHERE uta.userId = :userId")
    List<TaskProjection> findTasksByUserId(@Param("userId") Long userId);
}

