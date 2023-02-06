package com.lucas.scheduler;

import com.lucas.scheduler.conf.BatchConfig;
import com.lucas.scheduler.conf.DataSourceConfig;
import com.lucas.scheduler.conf.SchedulerConfig;
import com.lucas.scheduler.job.AbstractJob;
import com.lucas.scheduler.job.JobFactory;
import com.lucas.scheduler.job.JobInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Supplier;

@Slf4j
@Component
@EnableAsync
@EnableScheduling
@EnableBatchProcessing
@Import({JobFactory.class, BatchConfig.class})
@EnableConfigurationProperties({
        SchedulerConfig.class,
        DataSourceConfig.class
})
public class JobScheduler {

    private final JobLauncher jobLauncher;
    private final Map<String, ScheduledFuture<?>> scheduledTasks;
    private final Map<String, JobInfo> jobInfoMap;
    private final ThreadPoolTaskScheduler taskScheduler;


    private final JobFactory jobFactory;

    private static final String CRON_SECONDS = "0 ";

    public JobScheduler(ThreadPoolTaskScheduler taskScheduler, JobLauncher jobLauncher, JobFactory jobFactory, SchedulerConfig schdulerConfigs) {
        this.scheduledTasks = new HashMap<>();
        this.jobInfoMap = new HashMap<>();
        taskScheduler.setPoolSize(schdulerConfigs.getPool());
        this.taskScheduler = taskScheduler;
        this.jobLauncher = jobLauncher;
        this.jobFactory = jobFactory;
    }

    /**
     * Schedule the start of a job
     *
     * @param jobName            the name used to register the job into the ThreadPoolTaskScheduler, every job must have an unique name
     *                           otherwise the ThreadPoolTaskScheduler will stop the existent job and restart new one with the same name
     * @param cron               cron expression to schedule the job execution
     * @param jobImpl            a subclass of AbstractJob that contains the logic to execute for every job execution
     * @param <J>                J extends AbstractJob
     * @param parameters         parameters used by jobImpl during job execution
     * @param transactionManager user for transactional operations
     */
    public <J extends AbstractJob> void startScheduledJob(String jobName, String cron, Class<J> jobImpl, Map<String, JobParameter> parameters, PlatformTransactionManager transactionManager) {
        if (scheduledTasks.containsKey(jobName)) {
            log.warn("job named {} already exists. Recreating new job...", jobName);
            stopScheduledJob(jobName);
        }
        ScheduledFuture scheduledFeature = startJob(jobName, cron, jobImpl, parameters, transactionManager);
        if (scheduledFeature != null) {
            this.scheduledTasks.put(jobName, scheduledFeature);
            this.jobInfoMap.put(jobName, buildJobInfo(jobName, cron, jobImpl.getName(), parameters));
        }
    }

    /**
     * Schedule the start of a job
     *
     * @param jobName    the name used to register the job into the ThreadPoolTaskScheduler, every job must have an unique name
     *                   otherwise the ThreadPoolTaskScheduler will stop the existent job and restart new one with the same name
     * @param cron       cron expression to schedule the job execution
     * @param jobImpl    a subclass of AbstractJob that contains the logic to execute for every job execution
     * @param <J>        J extends AbstractJob
     * @param parameters parameters used by jobImpl during job execution
     */
    public <J extends AbstractJob> void startScheduledJob(String jobName, String cron, Class<J> jobImpl, Map<String, JobParameter> parameters) {
        startScheduledJob(jobName, cron, jobImpl, parameters, null);
    }

    /**
     * Schedule the start of a job
     *
     * @param jobName            the name used to register the job into the ThreadPoolTaskScheduler, every job must have an unique name
     *                           otherwise the ThreadPoolTaskScheduler will stop the existent job and restart new one with the same name
     * @param cron               cron expression to schedule the job execution
     * @param fun                the logic to execute for evenry job execution
     * @param transactionManager user for transactional operations
     */
    public void startScheduledJob(String jobName, String cron, Supplier<RepeatStatus> fun, PlatformTransactionManager transactionManager) {
        if (scheduledTasks.containsKey(jobName)) {
            log.warn("job named {} already exists. Recreating new job...", jobName);
            stopScheduledJob(jobName);
        }
        ScheduledFuture scheduledFeature = startJob(jobName, cron, fun, new HashMap<>(), transactionManager);
        if (scheduledFeature != null) {
            this.scheduledTasks.put(jobName, scheduledFeature);
            this.jobInfoMap.put(jobName, buildJobInfo(jobName, cron, fun.toString(), null));
        }
    }

    /**
     * Schedule the start of a job
     *
     * @param jobName the name used to register the job into the ThreadPoolTaskScheduler, every job must have an unique name
     *                otherwise the ThreadPoolTaskScheduler will stop the existent job and restart new one with the same name
     * @param cron    cron expression to schedule the job execution
     * @param fun     the logic to execute for evenry job execution
     */
    public void startScheduledJob(String jobName, String cron, Supplier<RepeatStatus> fun) {
        startScheduledJob(jobName, cron, fun, null);
    }

    /**
     * Cancel all the job scheduler
     */
    public void stopAllScheduledJobs() {
        this.scheduledTasks.entrySet().forEach(s -> stopScheduledJob(s.getKey()));
    }

    /**
     * Cancel the scheduler of a specific job
     *
     * @param jobName the name of the job to be cancelled
     */
    public void stopScheduledJob(String jobName) {
        ScheduledFuture<?> task = this.scheduledTasks.get(jobName);
        if (task != null) {
            task.cancel(true);
            scheduledTasks.remove(jobName);
            jobInfoMap.remove(jobName);
        }
    }

    private <J extends AbstractJob> ScheduledFuture startJob(String jobName, String cron, Object jobImpl, Map<String, JobParameter> parameters, PlatformTransactionManager transactionManager) {
        if (cron != null) {
            if (cron.trim().split(" ").length == 5) {
                cron = CRON_SECONDS.concat(cron.trim());
            }

            CronTrigger cronTrigger;
            Job job = null;
            try {
                cronTrigger = new CronTrigger(cron);
                if (jobImpl instanceof Class) {
                    job = jobFactory.job(jobName, (Class<J>) jobImpl, transactionManager);
                } else if (jobImpl instanceof Supplier) {
                    job = jobFactory.job(jobName, (Supplier<RepeatStatus>) jobImpl, transactionManager);
                }
            } catch (Exception e) {
                log.error("Error during Job execution {}", e.getMessage());
                return null;
            }

            Job finalJob = job;
            return this.taskScheduler.schedule(() -> {
                try {
                    parameters.put("time", new JobParameter(System.currentTimeMillis()));
                    JobParametersBuilder jobParamBuilder = new JobParametersBuilder();
                    parameters.entrySet().forEach(e -> jobParamBuilder.addParameter(e.getKey(), e.getValue()));
                    JobParameters param = jobParamBuilder
                            .toJobParameters();
                    jobLauncher.run(finalJob, param);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, cronTrigger);
        }
        return null;
    }

    private JobInfo buildJobInfo(String name, String cron, String jobPrototypeClass, Map<String, JobParameter> parameters) {
        Map<String, Object> params = new HashMap<>();
        if (parameters != null) {
            parameters.entrySet().forEach(e -> params.put(e.getKey(), e.getValue().getValue()));
        }

        return JobInfo.builder()
                .name(name)
                .cron(cron)
                .prototypeClass(jobPrototypeClass)
                .parameters(params)
                .build();
    }

    public boolean hasScheduledTasks() {
        return scheduledTasks.size() > 0;
    }

    public Map<String, JobInfo> getJobInfoMap() {
        return jobInfoMap;
    }
}
