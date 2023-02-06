package com.lucas.scheduler.job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobInfo {
    String name;
    String cron;
    String prototypeClass;
    Map<String, Object> parameters;
}
