package com.tibber.dev.revolutintegration.listener;

import com.tibber.dev.revolutintegration.model.TransactionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * A listener class to define tasks to execute before / after batch job.
 *
 * @auther Isami Mitani
 * @version 1.0
 */
@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final JdbcTemplate jdbcTemplate;
    private final Environment environment;

    @Autowired
    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate, Environment environment) {
        this.jdbcTemplate = jdbcTemplate;
        this.environment = environment;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.debug("!!! JOB FINISHED! Time to verify the results");

            try {
                // delete temporary file
                Files.delete(Paths.get(environment.getRequiredProperty("revolut.auth.file.path")));
            } catch (IOException e) {
                e.printStackTrace();
            }

            jdbcTemplate.query(environment.getRequiredProperty("sql.select.transactiondata"),
                    (rs, row) -> new TransactionData(
                            rs.getString(1),
                            rs.getString(2))
            ).forEach(data -> log.debug("Found <" + data + "> in the database."));
        } else {
            log.error("!!! BATCH JOB FAILED !!!");
        }
    }
}