package com.tibber.dev.revolutintegration.configuration;

import com.tibber.dev.revolutintegration.listener.ItemCountListener;
import com.tibber.dev.revolutintegration.listener.JobCompletionNotificationListener;
import com.tibber.dev.revolutintegration.model.FlattenTransactionData;
import com.tibber.dev.revolutintegration.model.RevolutAuthInfo;
import com.tibber.dev.revolutintegration.model.TransactionData;
import com.tibber.dev.revolutintegration.processor.FlattenTransactionDataProcessor;
import com.tibber.dev.revolutintegration.reader.RevolutAuthInfoDataRowMapper;
import com.tibber.dev.revolutintegration.reader.RevolutTransactionDataReader;
import com.tibber.dev.revolutintegration.writer.RevolutAuthInfoDataLineAggregator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A configuration class for batch job and its steps for Revolut integration.
 * In the first step, it fetches authentication information from database and save it in a temporary file.
 * In the second step, it fetches transaction data from Revolut API and save it to destination database.
 *
 * @version 1.0
 * @auther Isami Mitani
 */
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final Environment environment;
    private final JdbcTemplate jdbcTemplate;

    public BatchConfiguration(
            JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
            Environment environment, JdbcTemplate jdbcTemplate) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.environment = environment;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Returns a reader instance to read transaction data from Revolut API
     *
     * @param from from date to fetch data if provided
     * @param to   to date to fetch data if provided
     * @return {@code ItemReader<TransactionData>}
     */
    @Bean
    @StepScope
    public ItemReader<TransactionData> revolutTransactionDataReader(
            @Value("#{jobParameters['from'] ?: null}") String from,
            @Value("#{jobParameters['to'] ?: null}") String to
    ) {
        return new RevolutTransactionDataReader(
                environment,
                jdbcTemplate,
                from,
                to
        );
    }

    /**
     * Returns a reader instance to read auth information from database
     *
     * @param dataSource datasource component to read auth information
     * @return {@code JdbcCursorItemReader<RevolutAuthInfo>}
     */
    @Bean(destroyMethod = "")
    public JdbcCursorItemReader<RevolutAuthInfo> revolutAuthInfoJdbcCursorItemReader(@Qualifier("destinationDB") final DataSource dataSource) {
        JdbcCursorItemReader<RevolutAuthInfo> reader = new JdbcCursorItemReader<>();
        reader.setSql(environment.getRequiredProperty("sql.select.auth"));
        reader.setDataSource(dataSource);
        reader.setRowMapper(new RevolutAuthInfoDataRowMapper());
        return reader;
    }

    /**
     * Returns a writer instance to save auth information temporarily
     *
     * @return {@code FlatFileItemWriter<RevolutAuthInfo>}
     * @throws Exception
     */
    @Bean
    public FlatFileItemWriter<RevolutAuthInfo> revolutAuthInfoDataFileWriter() throws Exception {
        // create temporary file to store auth information
        Path tokenFilePath = Paths.get(environment.getRequiredProperty("revolut.auth.file.path"));
        if (!Files.exists(tokenFilePath)) {
            Files.createFile(tokenFilePath);
        }

        FlatFileItemWriter<RevolutAuthInfo> itemWriter = new FlatFileItemWriter<>();
        itemWriter.setLineAggregator(new RevolutAuthInfoDataLineAggregator());
        itemWriter.setResource(new FileSystemResource(tokenFilePath.normalize().toAbsolutePath().toString()));
        itemWriter.afterPropertiesSet();
        return itemWriter;
    }

    /**
     * Returns a processor instance to flatten transaction data
     *
     * @return {@code FlattenTransactionDataProcessor}
     */
    @Bean
    public FlattenTransactionDataProcessor processor() {
        return new FlattenTransactionDataProcessor();
    }

    /**
     * Returns a writer instance to save transaction data to database.
     *
     * @param dataSource datasource component to write transaction data
     * @return {@code JdbcBatchItemWriter<FlattenTransactionData}
     */
    @Bean
    public JdbcBatchItemWriter<FlattenTransactionData> transactionDataJDBCWriter(@Qualifier("destinationDB") final DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<FlattenTransactionData>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql(environment.getRequiredProperty("sql.insert.transactiondata"))
                .dataSource(dataSource)
                .build();
    }

    /**
     * Returns a listener class which counts processed data count
     *
     * @return {@code ItemCountListener}
     */
    @Bean
    public ItemCountListener itemCountListener() {
        return new ItemCountListener();
    }

    /**
     * Defines whole batch job.
     *
     * @param dataSource
     * @param listener
     * @return {@code Job}
     * @throws Exception
     */
    @Bean
    public Job revolutIntegrationJob(@Qualifier("destinationDB") final DataSource dataSource, JobCompletionNotificationListener listener) throws Exception {
        return jobBuilderFactory.get("revolutIntegrationJob" + System.currentTimeMillis())
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1(dataSource))
                .next(step2(dataSource))
                .end()
                .build();
    }

    /**
     * Defines first step for the batch job.
     * It will fetch auth information from database and save it in a temporary file
     *
     * @param dataSource
     * @return {@code Step}
     * @throws Exception
     */
    @Bean
    public Step step1(@Qualifier("destinationDB") final DataSource dataSource) throws Exception {
        return stepBuilderFactory.get("step1")
                .<RevolutAuthInfo, RevolutAuthInfo>chunk(1)
                .reader(revolutAuthInfoJdbcCursorItemReader(dataSource))
                .writer(revolutAuthInfoDataFileWriter())
                .startLimit(1)
                .build();
    }

    /**
     * Defines second step for the batch job.
     * It fetches transaction data from Revolut API and save data to database.
     *
     * @param dataSource
     * @return {@code Step}
     * @throws Exception
     */
    @Bean
    public Step step2(@Qualifier("destinationDB") final DataSource dataSource) throws Exception {
        return stepBuilderFactory.get("step2")
                .<TransactionData, FlattenTransactionData>chunk(10)
                .reader(revolutTransactionDataReader(null, null))
                .processor(processor())
                .writer(transactionDataJDBCWriter(dataSource))
                .faultTolerant()
                .retry(Exception.class)
                .retryLimit(10)
                .listener(itemCountListener())
                .build();
    }

}
