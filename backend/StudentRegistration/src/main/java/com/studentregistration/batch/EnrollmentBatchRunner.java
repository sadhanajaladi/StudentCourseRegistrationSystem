
package com.studentregistration.batch;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentBatchRunner implements ApplicationRunner {

    private final JobLauncher jobLauncher;
    private final Job enrollmentExportJob;

    public EnrollmentBatchRunner(
            JobLauncher jobLauncher,
            Job enrollmentExportJob) {
        this.jobLauncher = jobLauncher;
        this.enrollmentExportJob = enrollmentExportJob;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (!args.containsOption("run-enrollment-export")) {
            return;
        }

        JobParameters parameters = new JobParametersBuilder()
                .addLong("runTimestamp", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(enrollmentExportJob, parameters);

        System.out.println("Enrollment export job was launched.");
    }
}
