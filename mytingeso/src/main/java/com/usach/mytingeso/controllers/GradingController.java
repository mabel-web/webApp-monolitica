package com.usach.mytingeso.controllers;

import com.usach.mytingeso.dto.EnrollmentResponse;
import com.usach.mytingeso.dto.GradeRequest;
import com.usach.mytingeso.dto.PeriodCloseResponse;
import com.usach.mytingeso.services.GradingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GradingController {

    private final GradingService service;

    @GetMapping("/sections/{sectionId}/enrollments")
    public ResponseEntity<List<EnrollmentResponse>> findEnrolled(@PathVariable Long sectionId) {
        return ResponseEntity.ok(service.findEnrolled(sectionId));
    }

    @PutMapping("/sections/{sectionId}/enrollments/{enrollmentId}/grade")
    public ResponseEntity<EnrollmentResponse> grade(@PathVariable Long sectionId,
                                                    @PathVariable Long enrollmentId, @Valid @RequestBody GradeRequest request) {
        return ResponseEntity.ok(service.grade(sectionId, enrollmentId, request));
    }

    @PostMapping("/periods/{periodId}/close")
    public ResponseEntity<PeriodCloseResponse> close(@PathVariable Long periodId) {
        return ResponseEntity.ok(service.close(periodId));
    }
}
