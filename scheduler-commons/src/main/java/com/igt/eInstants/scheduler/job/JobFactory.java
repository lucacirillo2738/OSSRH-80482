package com.igt.eInstants.scheduler.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.function.Supplier;

/**
 * The factory used to create job dynamically
 */
@Slf4j
@Component
public class JobFactory {
    private static final String STEP_SUFFIX = "_Step";
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final ApplicationContext context;

    private final BeanDefinitionRegistry registry;

    public JobFactory(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, ApplicationContext context) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.context = context;
        this.registry = (BeanDefinitionRegistry) context.getAutowireCapableBeanFactory();
    }

    public <J extends AbstractJob> Job job(String jobName, J job, PlatformTransactionManager transactionManager) {
        StepBuilder stepBuilder = stepBuilderFactory.get(jobName.concat(STEP_SUFFIX));
        if (transactionManager != null) {
            stepBuilder.transactionManager(transactionManager);
        }
        return jobBuilderFactory.get(jobName)
                .start(stepBuilder.tasklet((stepContribution, chunkContext) ->
                        job.execute(stepContribution.getStepExecution().getJobParameters())
                ).build()).build();
    }

    public Job job(String jobName, Supplier<RepeatStatus> fun, PlatformTransactionManager transactionManager) {
        StepBuilder stepBuilder = stepBuilderFactory.get(jobName.concat(STEP_SUFFIX));
        if (transactionManager != null) {
            stepBuilder.transactionManager(transactionManager);
        }
        return jobBuilderFactory.get(jobName)
                .start(stepBuilder.tasklet((stepContribution, chunkContext) ->
                    fun.get()
                ).build()).build();
    }


    public <J extends AbstractJob> Job job(String jobName, Class<J> clazz, PlatformTransactionManager transactionManager) {
        if (!registry.containsBeanDefinition(clazz.getName())) {
            registry.registerBeanDefinition(clazz.getName(), BeanDefinitionBuilder.rootBeanDefinition(clazz).getBeanDefinition());
        }
        return job(jobName, context.getBean(clazz), transactionManager);
    }
}
