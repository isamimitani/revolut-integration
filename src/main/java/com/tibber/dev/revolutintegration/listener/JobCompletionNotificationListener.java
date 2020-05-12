package com.tibber.dev.revolutintegration.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * A listener class to define tasks to execute before / after batch job.
 *
 * @version 1.0
 * @auther Isami Mitani
 */
@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final Environment environment;

    @Autowired
    public JobCompletionNotificationListener(Environment environment) {
        this.environment = environment;
    }

    /**
     * Defines tasks to execute after job execution.
     * Deletes temporary file for auth information when job is completed.
     *
     * @param jobExecution
     */
    @Override
    public void afterJob(JobExecution jobExecution) {

        // delete temporary file
        try {
            Files.deleteIfExists(Paths.get(environment.getRequiredProperty("revolut.auth.file.path")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.debug("!!! JOB COMPLETED !!!");
        } else {
            log.error("!!! BATCH JOB FAILED !!!");
        }
    }
}