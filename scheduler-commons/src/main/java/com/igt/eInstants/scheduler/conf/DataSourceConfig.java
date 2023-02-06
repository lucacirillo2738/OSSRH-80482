package com.igt.eInstants.scheduler.conf;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Isolation.READ_UNCOMMITTED;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "com.igt.scheduler.datasource")
@Data
@Primary
public class DataSourceConfig {

    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private boolean recreateSchema;


    @Value("classpath:org/springframework/batch/core/schema-h2.sql")
    private Resource h2SchemaScript;

    @Value("classpath:org/springframework/batch/core/schema-postgresql.sql")
    private Resource postgresqlSchemaScript;

    @Value("classpath:org/springframework/batch/core/schema-drop-postgresql.sql")
    private Resource postgresqlDropSchemaScript;


    @Bean
    @Primary
    public DataSource schedulerDataSource() {
        DataSource dataSource = null;
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        if (driverClassName != null && username != null && password != null && url != null) {
            dataSourceBuilder.driverClassName(driverClassName);
            dataSourceBuilder.url(url);
            dataSourceBuilder.username(username);
            dataSourceBuilder.password(password);
            dataSource = dataSourceBuilder.build();
            if (org.h2.Driver.class.getName().equals(driverClassName)) {
                DatabasePopulatorUtils.execute(databasePopulator(h2SchemaScript), dataSource);
            } else if (org.postgresql.Driver.class.getName().equals(driverClassName)) {
                if (recreateSchema) {
                    DatabasePopulatorUtils.execute(databasePopulator(postgresqlDropSchemaScript), dataSource);
                }
                try {
                    DatabasePopulatorUtils.execute(databasePopulator(postgresqlSchemaScript), dataSource);
                } catch (Exception e) {
                    log.warn("schema already exists");
                }
            }
        }
        if (dataSource == null) {
            dataSourceBuilder.driverClassName("org.h2.Driver");
            dataSourceBuilder.url("jdbc:h2:mem:test");
            dataSourceBuilder.username("scheduler");
            dataSourceBuilder.password("scheduler");
            dataSource = dataSourceBuilder.build();
            DatabasePopulatorUtils.execute(this.databasePopulator(this.h2SchemaScript), dataSource);
        }
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager schedulerTransactionManager(DataSource schedulerDataSource) {
        return new DataSourceTransactionManager(schedulerDataSource);
    }

    @Bean
    protected JobRepository schedulerJobRepository(DataSource schedulerDataSource, PlatformTransactionManager schedulerTransactionManager) throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(schedulerDataSource);
        factory.setTransactionManager(schedulerTransactionManager);
        factory.setIsolationLevelForCreate("ISOLATION_" + READ_UNCOMMITTED.name());
        return factory.getObject();
    }

    private DatabasePopulator databasePopulator(Resource schemaScript) {
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(schemaScript);
        return populator;
    }
}
