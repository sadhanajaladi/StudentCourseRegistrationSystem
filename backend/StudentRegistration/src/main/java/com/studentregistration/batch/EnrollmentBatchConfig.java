
package com.studentregistration.batch;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.studentregistration.entity.Enrollment;
import com.studentregistration.repository.EnrollmentRepository;

@Configuration
public class EnrollmentBatchConfig {

    @Bean
    public Job enrollmentExportJob(
            JobRepository jobRepository,
            Step enrollmentExportStep) {

        return new JobBuilder("enrollmentExportJob", jobRepository)
                .start(enrollmentExportStep)
                .build();
    }

    @Bean
    public Step enrollmentExportStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<Enrollment> enrollmentReader,
            ItemProcessor<Enrollment, String> enrollmentProcessor,
            ItemWriter<String> enrollmentWriter) {

        return new StepBuilder("enrollmentExportStep", jobRepository)
                .<Enrollment, String>chunk(100, transactionManager)
                .reader(enrollmentReader)
                .processor(enrollmentProcessor)
                .writer(enrollmentWriter)
                .build();
    }


@Bean
public ItemReader<Enrollment> enrollmentReader(
        EnrollmentRepository enrollmentRepository) {

    List<Enrollment> enrollments =
            enrollmentRepository.findAllWithStudentAndCourse();

    return new ItemReader<Enrollment>() {
        private int index = 0;

        @Override
        public Enrollment read() {
            if (index < enrollments.size()) {
                return enrollments.get(index++);
            }

            return null;
        }
    };
}

    @Bean
    public ItemProcessor<Enrollment, String> enrollmentProcessor() {
        return enrollment -> {
            if (enrollment.getStudent() == null
                    || enrollment.getCourse() == null) {
                return null;
            }

            String studentName = csv(enrollment.getStudent().getName());
            String studentEmail = csv(enrollment.getStudent().getEmail());
            String courseTitle = csv(enrollment.getCourse().getTitle());
            String courseCode = csv(enrollment.getCourse().getCourseCode());
            String status = csv(enrollment.getStatus().name());
            String enrolledAt = csv(enrollment.getEnrolledAt().toString());

            return enrollment.getId() + ","
                    + studentName + ","
                    + studentEmail + ","
                    + courseTitle + ","
                    + courseCode + ","
                    + status + ","
                    + enrolledAt;
        };
    }



@Bean
public ItemWriter<String> enrollmentWriter() {
    AtomicBoolean firstChunk = new AtomicBoolean(true);

    return chunk -> {
        Path outputPath = Paths.get("reports", "enrollment-report.csv");
        Files.createDirectories(outputPath.getParent());

        boolean first = firstChunk.getAndSet(false);

        try (BufferedWriter writer = Files.newBufferedWriter(
                outputPath,
                StandardOpenOption.CREATE,
                first
                        ? StandardOpenOption.TRUNCATE_EXISTING
                        : StandardOpenOption.APPEND,
                StandardOpenOption.WRITE)) {

            if (first) {
                writer.write(
                        "Enrollment ID,Student Name,Student Email,"
                        + "Course Title,Course Code,Status,Enrolled At");
                writer.newLine();
            }

            for (String row : chunk) {
                writer.write(row);
                writer.newLine();
            }
        }
    };
}



    private static String csv(String value) {
        if (value == null) {
            return "\"\"";
        }

        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
