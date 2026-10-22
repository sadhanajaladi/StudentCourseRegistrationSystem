
package com.studentregistrationbatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StudentRegistrationBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                StudentRegistrationBatchApplication.class, args);
    }
}
