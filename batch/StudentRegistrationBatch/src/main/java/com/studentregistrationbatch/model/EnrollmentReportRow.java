
package com.studentregistrationbatch.model;

import java.time.LocalDateTime;

public class EnrollmentReportRow {

    private Long enrollmentId;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private Long courseId;
    private String courseCode;
    private String courseTitle;
    private String status;
    private LocalDateTime enrolledAt;

    public EnrollmentReportRow() {
    }

    public EnrollmentReportRow(
            Long enrollmentId,
            Long studentId,
            String studentName,
            String studentEmail,
            Long courseId,
            String courseCode,
            String courseTitle,
            String status,
            LocalDateTime enrolledAt) {

        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.status = status;
        this.enrolledAt = enrolledAt;
    }

    public Long getEnrollmentId() {
        return enrollmentId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public Long getCourseId() {
        return courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }
}
