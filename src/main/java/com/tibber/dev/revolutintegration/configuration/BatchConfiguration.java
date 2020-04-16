package com.tibber.dev.revolutintegration.configuration;

import com.tibber.dev.revolutintegration.entity.FlattenTransactionData;
import com.tibber.dev.revolutintegration.entity.TransactionData;
import com.tibber.dev.revolutintegration.listener.JobCompletionNotificationListener;
import com.tibber.dev.revolutintegration.processor.FlattenTransactionDataProcessor;
import com.tibber.dev.revolutintegration.reader.RESTRevolutTransactionDataReader;
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
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.io.File;
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

    @Autowired
    private DataSource dataSource;

    @Bean
    ItemReader<TransactionData> restReader(RestTemplate restTemplate) {
        return new RESTRevolutTransactionDataReader(
                environment.getRequiredProperty("testurl"),
                restTemplate
        );
    }

    @Bean
    public FlattenTransactionDataProcessor processor() {
        return new FlattenTransactionDataProcessor();
    }

    @Bean
    public FlatFileItemWriter<FlattenTransactionData> transactionDataFileWriter() throws Exception {
        FlatFileItemWriter<FlattenTransactionData> itemWriter = new FlatFileItemWriter<>();
//        itemWriter.setLineAggregator(new PassThroughLineAggregator<>());
        // parse string as json string
        itemWriter.setLineAggregator(new TransactionDataLineAggregator());
        String testDataOutputPath =
                File.createTempFile("test", ".out", new File(System.getProperty("user.dir"))).getAbsolutePath();
        System.out.println(">> output path: " + testDataOutputPath);
        itemWriter.setResource(new FileSystemResource(testDataOutputPath));
        itemWriter.afterPropertiesSet();
        return itemWriter;
    }

    @Bean
    public JdbcBatchItemWriter<FlattenTransactionData> transactionDataJDBCWriter() {
        return new JdbcBatchItemWriterBuilder<FlattenTransactionData>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO transaction_data (id, type, state, request_id, reason_code, created_at, updated_at, completed_at, scheduled_for, related_transaction_id, reference, leg_id, leg_amount, leg_currency, leg_bill_amount, leg_bill_currency, leg_account_id, leg_counterparty_id, leg_counterparty_account_id, leg_counterparty_account_type, leg_description, leg_balance, leg_fee, card_number, card_first_name, card_last_name, card_phone, merchant_name, merchant_city, merchant_category_code, merchant_country) VALUES (:id,:type,:state,:requestId,:reasonCode,:createdAt,:updatedAt,:completedAt,:scheduledFor,:relatedTransactionId,:reference,:legId,:legAmount,:legCurrency,:legBillAmount,:legBillCurrency,:legAccountId,:legCounterpartyId,:legCounterpartyAccountId,:legCounterpartyAccountType,:legDescription,:legBalance,:legFee,:cardNumber,:cardFirstName,:cardLastName,:cardPhone,:merchantName,:merchantCity,:merchantCategoryCode,:merchantCountry)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public CompositeItemWriter<FlattenTransactionData> compositeItemWriter() throws Exception{
        List<ItemWriter<? super FlattenTransactionData>> writers = new ArrayList<>(2);
        writers.add(transactionDataFileWriter());
        writers.add(transactionDataJDBCWriter());
        CompositeItemWriter<FlattenTransactionData> itemWriter = new CompositeItemWriter<>();
        itemWriter.setDelegates(writers);
        itemWriter.afterPropertiesSet();
        return itemWriter;
    }

    @Bean
    public Job revolutIntegrationJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(RestTemplate restTemplate) throws Exception {
        return stepBuilderFactory.get("step1")
                .<TransactionData, FlattenTransactionData>chunk(10)
                .reader(restReader(restTemplate))
                .processor(processor())
//                .writer(writer)
                .writer(compositeItemWriter())
                .build();
    }

}
