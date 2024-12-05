package com.infodation.task_service.services.absservices;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractImportCSV<T> {

    private static final Logger log = LoggerFactory.getLogger(AbstractImportCSV.class);

    protected abstract T mappingValue (String[] row) throws Exception;

    protected List<T> readAndSaveCSV(List<String[]> rows) {
        List<T> entities = new ArrayList<>();

        for (int i = 1; i < rows.size();i++) {
            String[] row = rows.get(i);
            try {
                T entity = mappingValue(row);
                if (entity != null)
                    entities.add(null);
            } catch (Exception e) {
                log.warn("Error processing row: {} message: {}", i, e.getMessage());
            }
        }

        return entities;
    }

}
