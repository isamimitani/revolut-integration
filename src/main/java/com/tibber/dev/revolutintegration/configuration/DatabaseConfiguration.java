package com.tibber.dev.revolutintegration.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {

    @Bean(name = "destinationDB")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource getDestinationDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "batchDB")
    @Primary
    @ConfigurationProperties(prefix = "spring.batch.datasource")
    public DataSource getBatchDataSource() {
        return DataSourceBuilder.create().build();
    }

}
