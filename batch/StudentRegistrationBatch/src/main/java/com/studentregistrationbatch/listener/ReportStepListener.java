
package com.studentregistrationbatch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class ReportStepListener implements StepExecutionListener {

    private static final Logger logger =
            LoggerFactory.getLogger(ReportStepListener.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
        logger.info("Report Step Started");
        logger.info("Step Name: {}", stepExecution.getStepName());
        logger.info("Start Time: {}", stepExecution.getStartTime());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.info("Report Step Finished");
        logger.info("Read Count: {}", stepExecution.getReadCount());
        logger.info("Write Count: {}", stepExecution.getWriteCount());
        logger.info("Filter Count: {}", stepExecution.getFilterCount());
        logger.info("Skip Count: {}", stepExecution.getSkipCount());
        logger.info("Commit Count: {}", stepExecution.getCommitCount());
        logger.info("Exit Status: {}", stepExecution.getExitStatus());

        return stepExecution.getExitStatus();
    }
}
