package com.infodation.task_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration
public class DateFormatConfig {

    @Bean
    public SimpleDateFormat dueDateFormatter() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }

    @Bean
    public SimpleDateFormat dateFormatter() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    }
}
