package com.lucas.scheduler;

import com.lucas.scheduler.conf.SchedulerConfig;
import com.lucas.scheduler.job.AbstractJob;
import com.lucas.scheduler.job.JobFactory;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Supplier;

@Slf4j
@Component
@EnableAsync
@EnableScheduling
@EnableBatchProcessing
@Import(JobFactory.class)
@EnableConfigurationProperties({
        SchedulerConfig.class
})
public class JobScheduler {

    private final JobLauncher jobLauncher;
    private final Map<String, ScheduledFuture<?>> scheduledTasks;
    private final ThreadPoolTaskScheduler taskScheduler;


    private final JobFactory jobFactory;

    private static final String CRON_SECONDS = "0 ";
    public JobScheduler(ThreadPoolTaskScheduler taskScheduler, JobLauncher jobLauncher, JobFactory jobFactory, SchedulerConfig schdulerConfigs){
        this.scheduledTasks =  new HashMap<>();
        taskScheduler.setPoolSize(schdulerConfigs.getPool());
        this.taskScheduler = taskScheduler;
        this.jobLauncher = jobLauncher;
        this.jobFactory = jobFactory;
    }

    /** Schedule the start of a job
     * @param jobName the name used to register the job into the ThreadPoolTaskScheduler, every job must have an unique name
     *                 otherwise the ThreadPoolTaskScheduler will stop the existent job and restart new one with the same name
     * @param cron cron expression to schedule the job execution
     * @param jobImpl a subclass of AbstractJob that contains the logic to execute for every job execution
     * @param <J> J extends AbstractJob
     * @param parameters parameters used by jobImpl during job execution
     * */
    public <J extends AbstractJob> void startScheduledJob(String jobName, String cron, Class<J> jobImpl, Map<String, JobParameter> parameters){
        if(scheduledTasks.containsKey(jobName)){
            log.warn("job named {} already exists. Recreating new job...", jobName);
            stopScheduledJob(jobName);
        }
        ScheduledFuture scheduledFeature = startJob(jobName, cron, jobImpl, parameters);
        if(scheduledFeature != null){
            this.scheduledTasks.put(jobName, scheduledFeature);
        }
    }

    /** Schedule the start of a job
     * @param jobName the name used to register the job into the ThreadPoolTaskScheduler, every job must have an unique name
     *                 otherwise the ThreadPoolTaskScheduler will stop the existent job and restart new one with the same name
     * @param cron cron expression to schedule the job execution
     * @param fun the logic to execute for evenry job execution
     * */
    public void startScheduledJob(String jobName, String cron, Supplier<RepeatStatus> fun){
        if(scheduledTasks.containsKey(jobName)){
            log.warn("job named {} already exists. Recreating new job...", jobName);
            stopScheduledJob(jobName);
        }
        CronTrigger cronTrigger = new CronTrigger(cron);
        ScheduledFuture scheduledFeature =  this.taskScheduler.schedule(() -> {
            JobParameters params = new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters();
            Job job = jobFactory.job(jobName,fun);
            try {
                jobLauncher.run(job, params);
            } catch (Exception e) {
                log.error("Error during Job execution {}", e.getMessage());
            }
        }, cronTrigger);
        if(scheduledFeature != null){
            this.scheduledTasks.put(jobName, scheduledFeature);
        }
    }

    /** Cancel all the job scheduler
     * */
    public void stopAllScheduledJobs(){
        this.scheduledTasks.entrySet().forEach(s -> stopScheduledJob(s.getKey()));
    }

    /** Cancel the scheduler of a specific job
     * @param jobName the name of the job to be cancelled
     * */
    public void stopScheduledJob(String jobName){
        ScheduledFuture<?> task = this.scheduledTasks.get(jobName);
        if(task != null){
            task.cancel(true);
            scheduledTasks.remove(jobName);
        }
    }

    private <J extends AbstractJob> ScheduledFuture startJob(String jobName, String cron, Class<J> jobImpl, Map<String, JobParameter> parameters){
        if(cron != null){
            if(cron.trim().split(" ").length == 5){
                cron = CRON_SECONDS.concat(cron.trim());
            }

            CronTrigger cronTrigger;
            Job job;
            try {
                cronTrigger = new CronTrigger(cron);
                job = jobFactory.job(jobName, jobImpl);
            }catch (Exception e){
                log.error("Error during Job execution {}", e.getMessage());
                return null;
            }

            return this.taskScheduler.schedule(() -> {
                try {
                    parameters.put("time", new JobParameter(System.currentTimeMillis()));
                    JobParametersBuilder jobParamBuilder = new JobParametersBuilder();
                    parameters.entrySet().forEach(e -> jobParamBuilder.addParameter(e.getKey(), e.getValue()));
                    JobParameters param = jobParamBuilder
                            .toJobParameters();
                    jobLauncher.run(job, param);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, cronTrigger);
        }
        return null;
    }
}
