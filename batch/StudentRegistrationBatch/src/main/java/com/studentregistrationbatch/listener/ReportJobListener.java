
package com.studentregistrationbatch.listener;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class ReportJobListener implements JobExecutionListener {

    private static final Logger logger =
            LoggerFactory.getLogger(ReportJobListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info("Enrollment Report Job Started");
        logger.info("Job ID: {}", jobExecution.getId());
        logger.info("Job Name: {}",
                jobExecution.getJobInstance().getJobName());
        logger.info("Start Time: {}", jobExecution.getStartTime());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        logger.info("Enrollment Report Job Finished");
        logger.info("Job ID: {}", jobExecution.getId());
        logger.info("End Time: {}", jobExecution.getEndTime());
        logger.info("Final Status: {}", jobExecution.getStatus());

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            logger.info("Result: Report generated successfully");
        } else {
            logger.error("Result: Report generation failed");
            logger.error("Reason: {}",
                    jobExecution.getExitStatus().getExitDescription());
        }

        if (jobExecution.getStartTime() != null
                && jobExecution.getEndTime() != null) {
            Duration duration = Duration.between(
                    jobExecution.getStartTime(),
                    jobExecution.getEndTime());

            logger.info("Duration: {} milliseconds",
                    duration.toMillis());
        }
    }
}
