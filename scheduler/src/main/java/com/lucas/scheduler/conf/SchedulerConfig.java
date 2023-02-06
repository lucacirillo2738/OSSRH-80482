package com.lucas.scheduler.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ConfigurationProperties(prefix = "com.igt.scheduler")
@Data
@Primary
public class SchedulerConfig {
    private int pool = 1;
}
