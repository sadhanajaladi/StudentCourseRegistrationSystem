
package com.studentregistrationbatch.controller;

import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final JobLauncher jobLauncher;
    private final Job enrollmentReportJob;
    private final JobExplorer jobExplorer;

    public ReportController(
            JobLauncher jobLauncher,
            Job enrollmentReportJob,
            JobExplorer jobExplorer) {

        this.jobLauncher = jobLauncher;
        this.enrollmentReportJob = enrollmentReportJob;
        this.jobExplorer = jobExplorer;
    }

    @PostMapping("/enrollments")
    public ResponseEntity<String> generateEnrollmentReport() {
        try {
            String reportMonth = YearMonth.now().toString();

            JobParameters parameters = new org.springframework.batch.core
                    .JobParametersBuilder()
                    .addString("reportMonth", reportMonth)
                    .addLong("run.timestamp", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution execution =
                    jobLauncher.run(enrollmentReportJob, parameters);

            return ResponseEntity.ok(
                    "Enrollment report job launched. Status: "
                    + execution.getStatus()
                    + ". Report month: " + reportMonth);

        } catch (Exception exception) {
            return ResponseEntity.internalServerError()
                    .body("Failed to launch enrollment report: "
                            + exception.getMessage());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getReportStatus() {

        List<JobInstance> instances =
                jobExplorer.getJobInstances(
                        "enrollmentReportJob", 0, 1);

        if (instances.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message",
                            "No enrollment report execution found"));
        }

        JobInstance latestInstance = instances.get(0);

        List<JobExecution> executions =
                jobExplorer.getJobExecutions(latestInstance);

        if (executions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message",
                            "No execution history found"));
        }

        JobExecution latestExecution = executions.stream()
                .max((first, second) ->
                        Long.compare(first.getId(), second.getId()))
                .orElseThrow();

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("jobName",
                latestExecution.getJobInstance().getJobName());
        response.put("jobExecutionId", latestExecution.getId());
        response.put("status",
                latestExecution.getStatus().toString());
        response.put("reportMonth",
                latestExecution.getJobParameters()
                        .getString("reportMonth"));
        response.put("startTime", latestExecution.getStartTime());
        response.put("endTime", latestExecution.getEndTime());

        return ResponseEntity.ok(response);
    }
    

@GetMapping("/download")
public ResponseEntity<?> downloadEnrollmentReport() throws IOException {

    String reportMonth = YearMonth.now().toString();

    Path reportFile = Path.of(
            "C:/Users/HP/OneDrive/Desktop/File_Net Training/"
            + "StudentRegistrationBatch/reports",
            "enrollment-report-" + reportMonth + ".csv"
    );

    if (!Files.exists(reportFile) || !Files.isRegularFile(reportFile)) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Report not found for " + reportMonth);
    }

    InputStreamResource resource =
            new InputStreamResource(Files.newInputStream(reportFile));

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + reportFile.getFileName() + "\"")
            .contentType(MediaType.parseMediaType("text/csv"))
            .contentLength(Files.size(reportFile))
            .body(resource);
}
@GetMapping("/test-exception")
public String testException() throws IOException {
    throw new IOException("Testing global exception handler");
}

}
