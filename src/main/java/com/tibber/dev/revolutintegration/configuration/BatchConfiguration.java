package com.tibber.dev.revolutintegration.configuration;

import com.tibber.dev.revolutintegration.model.FlattenTransactionData;
import com.tibber.dev.revolutintegration.model.RevolutAuthInfo;
import com.tibber.dev.revolutintegration.model.TransactionData;
import com.tibber.dev.revolutintegration.listener.JobCompletionNotificationListener;
import com.tibber.dev.revolutintegration.processor.FlattenTransactionDataProcessor;
import com.tibber.dev.revolutintegration.reader.RevolutTransactionDataReader;
import com.tibber.dev.revolutintegration.reader.RevolutAuthInfoDataRowMapper;
import com.tibber.dev.revolutintegration.writer.RevolutAuthInfoDataLineAggregator;
import com.tibber.dev.revolutintegration.writer.TransactionDataLineAggregator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private Environment environment;

//    @Autowired
//    private DataSource dataSource;

    @Bean
    ItemReader<TransactionData> revolutTransactionDataReader() {
        return new RevolutTransactionDataReader(
                environment.getRequiredProperty("revolut.api.url.transaction"),
                environment.getRequiredProperty("revolut.api.url.refreshtoken"),
                environment.getRequiredProperty("revolut.auth.file.path")
        );
    }

    @Bean(destroyMethod = "")
    public JdbcCursorItemReader<RevolutAuthInfo> revolutAuthInfoJdbcCursorItemReader(@Qualifier("destinationDB") final DataSource dataSource) {
        JdbcCursorItemReader<RevolutAuthInfo> reader = new JdbcCursorItemReader<>();
        reader.setSql("select * from trading_parameters.revolut_auth_info limit 1");
        reader.setDataSource(dataSource);
        reader.setRowMapper(new RevolutAuthInfoDataRowMapper());
        return reader;
    }


    @Bean
    public FlatFileItemWriter<RevolutAuthInfo> revolutAuthInfoDataFileWriter() throws Exception {
        // create temporary file to store auth info
        Path tokenFilePath = Paths.get(environment.getProperty("revolut.auth.file.path"));
        if (!Files.exists(tokenFilePath)) {
            tokenFilePath = Files.createFile(tokenFilePath);
        }
        System.out.println(">> output path: " + tokenFilePath.normalize().toAbsolutePath());

        FlatFileItemWriter<RevolutAuthInfo> itemWriter = new FlatFileItemWriter<>();
        itemWriter.setLineAggregator(new RevolutAuthInfoDataLineAggregator());
        itemWriter.setResource(new FileSystemResource(tokenFilePath.normalize().toAbsolutePath().toString()));
        itemWriter.afterPropertiesSet();
        return itemWriter;
    }

    @Bean
    public FlattenTransactionDataProcessor processor() {
        return new FlattenTransactionDataProcessor();
    }

    @Bean
    public FlatFileItemWriter<FlattenTransactionData> transactionDataFileWriter() throws Exception {
        // create temporary file to store data
        String testDataOutputPath =
                File.createTempFile("test", ".out", new File(System.getProperty("user.dir"))).getAbsolutePath();
        System.out.println(">> output path: " + testDataOutputPath);

        FlatFileItemWriter<FlattenTransactionData> itemWriter = new FlatFileItemWriter<>();
        itemWriter.setLineAggregator(new TransactionDataLineAggregator());
        itemWriter.setResource(new FileSystemResource(testDataOutputPath));
        itemWriter.afterPropertiesSet();
        return itemWriter;
    }

    @Bean
    public JdbcBatchItemWriter<FlattenTransactionData> transactionDataJDBCWriter(@Qualifier("destinationDB") final DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<FlattenTransactionData>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO trading_parameters.transaction_data (id, type, state, request_id, reason_code, created_at, updated_at, completed_at, scheduled_for, related_transaction_id, reference, leg_id, leg_amount, leg_currency, leg_bill_amount, leg_bill_currency, leg_account_id, leg_counterparty_id, leg_counterparty_account_id, leg_counterparty_account_type, leg_description, leg_balance, leg_fee, card_number, card_first_name, card_last_name, card_phone, merchant_name, merchant_city, merchant_category_code, merchant_country) VALUES (:id,:type,:state,:requestId,:reasonCode,:createdAt,:updatedAt,:completedAt,:scheduledFor,:relatedTransactionId,:reference,:legId,:legAmount,:legCurrency,:legBillAmount,:legBillCurrency,:legAccountId,:legCounterpartyId,:legCounterpartyAccountId,:legCounterpartyAccountType,:legDescription,:legBalance,:legFee,:cardNumber,:cardFirstName,:cardLastName,:cardPhone,:merchantName,:merchantCity,:merchantCategoryCode,:merchantCountry)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public CompositeItemWriter<FlattenTransactionData> compositeItemWriter(@Qualifier("destinationDB") final DataSource dataSource) throws Exception {
        List<ItemWriter<? super FlattenTransactionData>> writers = new ArrayList<>(2);
        writers.add(transactionDataFileWriter());
        writers.add(transactionDataJDBCWriter(dataSource));
        CompositeItemWriter<FlattenTransactionData> itemWriter = new CompositeItemWriter<>();
        itemWriter.setDelegates(writers);
        itemWriter.afterPropertiesSet();
        return itemWriter;
    }

    @Bean
    public Job revolutIntegrationJob(@Qualifier("destinationDB") final DataSource dataSource, JobCompletionNotificationListener listener) throws Exception {
        return jobBuilderFactory.get("revolutIntegrationJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1(dataSource))
                .next(step2(dataSource))
                .end()
                .build();
    }

    @Bean
    public Step step2(@Qualifier("destinationDB") final DataSource dataSource) throws Exception {
        return stepBuilderFactory.get("step2")
                .<TransactionData, FlattenTransactionData>chunk(10)
                .reader(revolutTransactionDataReader())
                .processor(processor())
                .writer(compositeItemWriter(dataSource))
                .build();
    }

    @Bean
    public Step step1(@Qualifier("destinationDB") final DataSource dataSource) throws Exception {
        return stepBuilderFactory.get("step1")
                .<RevolutAuthInfo, RevolutAuthInfo>chunk(1)
                .reader(revolutAuthInfoJdbcCursorItemReader(dataSource))
                .writer(revolutAuthInfoDataFileWriter())
                .build();
    }

}
