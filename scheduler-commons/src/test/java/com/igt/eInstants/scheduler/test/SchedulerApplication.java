package com.igt.eInstants.scheduler.test;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableBatchProcessing
@EnableAsync
public class SchedulerApplication {

    public static void main(String... args){
        SpringApplication.run(SchedulerApplication.class, args);
    }
}
