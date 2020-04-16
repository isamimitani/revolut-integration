package com.tibber.dev.revolutintegration.configuration;

import com.tibber.dev.revolutintegration.entity.TestData;
import com.tibber.dev.revolutintegration.entity.TransactionData;
import com.tibber.dev.revolutintegration.reader.TransactionDataRowMapper;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

public class CodeReference {

    private final RestTemplate restTemplate;
    private final DataSource dataSource;

    public CodeReference(RestTemplate restTemplate, DataSource dataSource) {
        this.restTemplate = restTemplate;
        this.dataSource = dataSource;
    }

    // Call API with RestTemplate
    private List<TestData> fetchTransactionDataFromAPI() {
        ResponseEntity<TestData[]> response = restTemplate.getForEntity(
                "apiUrl",
                TestData[].class
        );
        System.out.println(response.getBody());
        TestData[] fetchedTransactionData = response.getBody();
        return Arrays.asList(fetchedTransactionData);
    }

    // reader for file
//    @Bean
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

    // reader for jdbc
//    @Bean
    public JdbcCursorItemReader<TransactionData> cursorItemReader(DataSource dataSource){
        JdbcCursorItemReader<TransactionData> reader = new JdbcCursorItemReader<>();
        reader.setSql("select * from table order by id");
        reader.setDataSource(dataSource);
        reader.setRowMapper(new TransactionDataRowMapper());
        return reader;
    }

//    @Bean
    public JdbcBatchItemWriter<TransactionData> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<TransactionData>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO people (first_name, last_name) VALUES (:id, :type)")
                .dataSource(dataSource)
                .build();
    }
}
