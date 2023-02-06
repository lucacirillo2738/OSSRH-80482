package com.lucas.scheduler.conf;

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@Import(DataSourceConfig.class)
public class BatchConfig extends DefaultBatchConfigurer {
    @Override
    @Autowired
    public void setDataSource(@Qualifier("schedulerDataSource") DataSource dataSource) {
        super.setDataSource(dataSource);
    }

    @Autowired
    @Qualifier("schedulerTransactionManager")
    private PlatformTransactionManager transactionManager;

    @Autowired
    @Qualifier("schedulerJobRepository")
    private JobRepository jobRepository;

    @Override
    public PlatformTransactionManager getTransactionManager() {
        return transactionManager;
    }

    @Override
    public JobRepository getJobRepository() {
        return jobRepository;
    }
}
