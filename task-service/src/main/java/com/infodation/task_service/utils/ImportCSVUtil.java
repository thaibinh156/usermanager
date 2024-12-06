package com.infodation.task_service.utils;

import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ImportCSVUtil<T> {
    private static final Logger log = LoggerFactory.getLogger(ImportCSVUtil.class);

    public void readAndSaveCSV(JpaRepository repository, MultipartFile file, Mapper<T> mapper) throws Exception {
        List<T> entities = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVReader csvReader = new CSVReader(bufferedReader)) {

            List<String[]> rows = csvReader.readAll();

            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                try {
                    T entity = mapper.mappingData(row);
                    if (entity != null) {
                        entities.add(entity);
                    }
                } catch (Exception e) {
                    log.warn("Error processing row: {} message: {}", i, e.getMessage());
                }
            }

            if (!entities.isEmpty()) {
                log.info("Finished reading file. Total valid lines: {}", entities.size());
                repository.saveAll(entities);
            } else {
                throw new Exception("Can not save " + file.getOriginalFilename());
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new Exception(ex.getMessage());
        }
    }
}
