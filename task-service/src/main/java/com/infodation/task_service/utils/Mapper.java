package com.infodation.task_service.utils;

@FunctionalInterface
public interface Mapper<T> {
    T mappingData(String[] row) throws Exception;
}
