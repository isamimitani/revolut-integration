package com.tibber.dev.revolutintegration.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

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

    @Bean
    public JdbcTemplate createJdbcTemplate(@Qualifier("destinationDB") DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }

}
