
package com.studentregistration.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.studentregistration.dto.EnrollmentRequest;
import com.studentregistration.dto.EnrollmentResponse;
import com.studentregistration.service.EnrollmentService;

@RestController
@RequestMapping
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    // Student enrolls in a course
    @PostMapping("/api/student/enrollments")
    @ResponseStatus(HttpStatus.CREATED)
    public EnrollmentResponse enrollStudent(
            @Valid @RequestBody EnrollmentRequest request,
            Authentication authentication) {

        return enrollmentService.enrollStudent(
                authentication.getName(),
                request.getCourseId());
    }

    // Student views their own enrollments
    @GetMapping("/api/student/enrollments")
    public List<EnrollmentResponse> getMyEnrollments(
            Authentication authentication) {

        return enrollmentService.getStudentEnrollments(
                authentication.getName());
    }

    // Admin views all enrollments
    @GetMapping("/api/admin/enrollments")
    public List<EnrollmentResponse> getAllEnrollments() {

        return enrollmentService.getAllEnrollments();
    }
}
