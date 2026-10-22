
package com.studentregistrationbatch.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.studentregistrationbatch.model.EnrollmentReportRow;
import com.studentregistrationbatch.listener.ReportJobListener;
import com.studentregistrationbatch.listener.ReportStepListener;
import com.studentregistrationbatch.processor.EnrollmentReportProcessor;

@Configuration
public class BatchConfig {

    private static final String SQL = """
        SELECT
            e.id AS enrollment_id,
            u.id AS student_id,
            u.name AS student_name,
            u.email AS student_email,
            c.id AS course_id,
            c.course_code,
            c.title AS course_title,
            e.status,
            e.enrolled_at
        FROM enrollments e
        JOIN users u ON e.student_id = u.id
        JOIN courses c ON e.course_id = c.id
        WHERE u.role = 'STUDENT'
        ORDER BY e.id
        """;

    @Bean
    public JdbcCursorItemReader<EnrollmentReportRow> enrollmentReader(
            DataSource dataSource) {

        return new JdbcCursorItemReaderBuilder<EnrollmentReportRow>()
                .name("enrollmentReader")
                .dataSource(dataSource)
                .sql(SQL)
                .rowMapper(this::mapRow)
                .build();
    }

    private EnrollmentReportRow mapRow(
            ResultSet rs, int rowNum) throws SQLException {

        Timestamp timestamp = rs.getTimestamp("enrolled_at");

        LocalDateTime enrolledAt = timestamp == null
                ? null
                : timestamp.toLocalDateTime();

        return new EnrollmentReportRow(
                rs.getLong("enrollment_id"),
                rs.getLong("student_id"),
                rs.getString("student_name"),
                rs.getString("student_email"),
                rs.getLong("course_id"),
                rs.getString("course_code"),
                rs.getString("course_title"),
                rs.getString("status"),
                enrolledAt
        );
    }

    @Bean
    public ItemProcessor<EnrollmentReportRow, EnrollmentReportRow>
            enrollmentProcessor() {

        return item -> {
            if (item.getEnrollmentId() == null
                    || item.getStudentName() == null
                    || item.getCourseTitle() == null) {

                throw new IllegalArgumentException(
                        "Enrollment contains missing required data");
            }

            return item;
        };
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<EnrollmentReportRow> enrollmentWriter(
            @Value("#{jobParameters['reportMonth']}") String reportMonth)
            throws IOException {

        Path reportDirectory = Path.of(
                "C:/Users/HP/OneDrive/Desktop/File_Net Training/"
                + "StudentRegistrationBatch/reports"
        );

        Files.createDirectories(reportDirectory);

        Path reportFile = reportDirectory.resolve(
                "enrollment-report-" + reportMonth + ".csv"
        );

        String[] headers = {
            "enrollmentId", "studentId", "studentName", "studentEmail",
            "courseId", "courseCode", "courseTitle", "status", "enrolledAt"
        };

        return new FlatFileItemWriterBuilder<EnrollmentReportRow>()
                .name("enrollmentWriter")
                .resource(new FileSystemResource(reportFile.toFile()))
                .headerCallback(writer ->
                        writer.write(String.join(",", headers)))
                .lineAggregator(item ->
                        Stream.of(
                                item.getEnrollmentId(),
                                item.getStudentId(),
                                item.getStudentName(),
                                item.getStudentEmail(),
                                item.getCourseId(),
                                item.getCourseCode(),
                                item.getCourseTitle(),
                                item.getStatus(),
                                item.getEnrolledAt()
                        )
                        .map(BatchConfig::csvValue)
                        .collect(Collectors.joining(",")))
                .shouldDeleteIfExists(true)
                .build();
    }

    private static String csvValue(Object value) {
        String text = value == null ? "" : value.toString();
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }


@Bean
public Step enrollmentReportStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        JdbcCursorItemReader<EnrollmentReportRow> enrollmentReader,
        EnrollmentReportProcessor enrollmentProcessor,
        FlatFileItemWriter<EnrollmentReportRow> enrollmentWriter,
        ReportStepListener reportStepListener) {

    return new StepBuilder("enrollmentReportStep", jobRepository)
            .<EnrollmentReportRow, EnrollmentReportRow>chunk(
                    100, transactionManager)
            .reader(enrollmentReader)
            .processor(enrollmentProcessor)
            .writer(enrollmentWriter)
            .listener(reportStepListener)
            .build();
}


    @Bean
    public Job enrollmentReportJob(
            JobRepository jobRepository,
            Step enrollmentReportStep,
            ReportJobListener reportJobListener) {

        return new JobBuilder("enrollmentReportJob", jobRepository)
                .listener(reportJobListener)
                .start(enrollmentReportStep)
                .build();
    }
}
