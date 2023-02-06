package com.lucas.scheduler.job;

import lombok.Data;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * Abstract class used for create the logic of a job that wil be executed
 * Every subclass must implement the execute method
 */
@Data
public abstract class AbstractJob {

    public abstract RepeatStatus execute(JobParameters parameters);
}
