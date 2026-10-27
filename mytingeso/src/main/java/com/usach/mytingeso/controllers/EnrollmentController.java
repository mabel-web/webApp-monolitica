package com.usach.mytingeso.controllers;

import com.usach.mytingeso.dto.*;
import com.usach.mytingeso.services.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService service;

    @PostMapping("/periods/{periodId}/matriculations")
    public ResponseEntity<MatriculationResponse> matriculate(@PathVariable Long periodId,
                                                             @Valid @RequestBody MatriculationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.matriculate(periodId, request.studentId()));
    }

    @GetMapping("/students/{studentId}/periods/{periodId}/available-subjects")
    public ResponseEntity<List<AvailableSubjectResponse>> available(@PathVariable Long studentId,
                                                                    @PathVariable Long periodId) {
        return ResponseEntity.ok(service.findAvailable(studentId, periodId));
    }

    @PostMapping("/students/{studentId}/periods/{periodId}/enrollments")
    public ResponseEntity<List<EnrollmentResponse>> enroll(@PathVariable Long studentId,
                                                           @PathVariable Long periodId, @Valid @RequestBody EnrollmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.enroll(studentId, periodId, request));
    }

    @DeleteMapping("/students/{studentId}/enrollments/{enrollmentId}")
    public ResponseEntity<EnrollmentResponse> annul(@PathVariable Long studentId,
                                                    @PathVariable Long enrollmentId) {
        return ResponseEntity.ok(service.annul(studentId, enrollmentId));
    }

}
