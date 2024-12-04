package com.infodation.task_service.models;

public interface TaskProjection {
    Long getTaskId();
    String getTitle();
    String getCategoryName();
    String getCategoryDescription();
    String getStatusName();
    String getStatusDescription();
}
