
package com.studentregistrationbatch.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.studentregistrationbatch.model.EnrollmentReportRow;

@Component
public class EnrollmentReportProcessor
        implements ItemProcessor<EnrollmentReportRow, EnrollmentReportRow> {

    private static final Logger logger =
            LoggerFactory.getLogger(EnrollmentReportProcessor.class);

    @Override
    public EnrollmentReportRow process(EnrollmentReportRow item) {

        if (item.getEnrollmentId() == null
                || item.getStudentName() == null
                || item.getCourseTitle() == null) {

            logger.warn(
                    "Filtering invalid enrollment record. Enrollment ID: {}",
                    item.getEnrollmentId());

            return null;
        }

        return item;
    }
}
