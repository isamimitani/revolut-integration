package com.tibber.dev.revolutintegration.configuration;

import com.tibber.dev.revolutintegration.entity.TestData;
import com.tibber.dev.revolutintegration.entity.TransactionData;
import com.tibber.dev.revolutintegration.listener.JobCompletionNotificationListener;
import com.tibber.dev.revolutintegration.processor.TransactionDataItemProcessor;
import com.tibber.dev.revolutintegration.reader.RESTRevolutTransactionDataReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    ItemReader<TestData> restReader(Environment environment,
                                    RestTemplate restTemplate) {
        return new RESTRevolutTransactionDataReader(
//               environment.getRequiredProperty("https://jsonplaceholder.typicode.com/todos/1"),
                "https://jsonplaceholder.typicode.com/todos/",
                restTemplate
        );
    }

    @Bean
    public FlatFileItemReader<TransactionData> reader() {
        return new FlatFileItemReaderBuilder<TransactionData>()
                .name("personItemReader")
                .resource(new ClassPathResource("sample-data.csv"))
                .delimited()
                .names(new String[]{"id", "type"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<TransactionData>() {{
                    setTargetType(TransactionData.class);
                }})
                .build();
    }

    @Bean
    public TransactionDataItemProcessor processor() {
        return new TransactionDataItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<TestData> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<TestData>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO people (first_name, last_name) VALUES (:id, :userId)")
                .dataSource(dataSource)
                .build();
    }

//    @Bean
//    public JdbcBatchItemWriter<TransactionData> writer(DataSource dataSource) {
//        return new JdbcBatchItemWriterBuilder<TransactionData>()
//                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
//                .sql("INSERT INTO people (first_name, last_name) VALUES (:id, :type)")
//                .dataSource(dataSource)
//                .build();
//    }

    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(step1)
//                .flow(step1)
//                .end()
                .build();
    }

    @Bean
    public Step step1(JdbcBatchItemWriter<TestData> writer, Environment environment, RestTemplate restTemplate) {
        return stepBuilderFactory.get("step1")
                .<TestData, TestData> chunk(10)
                .reader(restReader(environment, restTemplate))
                .processor(processor())
                .writer(writer)
                .build();
    }

//    @Bean
//    public Step step1(JdbcBatchItemWriter<TransactionData> writer) {
//        return stepBuilderFactory.get("step1")
//                .<TransactionData, TransactionData> chunk(10)
//                .reader(reader())
////                .processor(processor())
//                .writer(writer)
//                .build();
//    }

}
