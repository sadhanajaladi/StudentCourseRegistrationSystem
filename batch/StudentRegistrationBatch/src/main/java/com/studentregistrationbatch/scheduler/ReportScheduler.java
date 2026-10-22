
package com.studentregistrationbatch.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.YearMonth;

@Component
public class ReportScheduler {

    private static final Logger logger =
            LoggerFactory.getLogger(ReportScheduler.class);

    private final JobLauncher jobLauncher;
    private final Job enrollmentReportJob;

    public ReportScheduler(
            JobLauncher jobLauncher,
            Job enrollmentReportJob) {
        this.jobLauncher = jobLauncher;
        this.enrollmentReportJob = enrollmentReportJob;
    }

    // First report: 2 minutes after application startup.
    // Subsequent demo runs: every 24 hours.
   // @Scheduled(initialDelay = 120000, fixedDelay = 86400000)
    public void generateStartupReport() {
    	 logger.info("========== SCHEDULER TRIGGERED ==========");
        runEnrollmentReport();
    }

 // Run at 11:00 PM on the last day of every month.
 // Generates the monthly enrollment report and preserves previous months' reports.
    @Scheduled(cron = "0 0 23 L * *")
    public void generateMonthlyReport() {
    	logger.info("Monthly enrollment report triggered.");
        runEnrollmentReport();
    }

    private void runEnrollmentReport() {
        try {
            logger.info("Enrollment report job starting.");

            String reportMonth = YearMonth.now().toString();

            JobParameters parameters = new JobParametersBuilder()
                    .addString("reportMonth", reportMonth)
                    .addLong("run.timestamp", System.currentTimeMillis())
                    .toJobParameters();

            var execution = jobLauncher.run(
                    enrollmentReportJob, parameters);

            logger.info(
                    "Enrollment report job finished with status: {}",
                    execution.getStatus());

        } catch (Exception exception) {
            logger.error("Enrollment report job failed.", exception);
        }
    }
}
