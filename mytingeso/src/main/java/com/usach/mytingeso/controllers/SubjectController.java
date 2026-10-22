package com.usach.mytingeso.controllers;

import com.usach.mytingeso.dto.SubjectCreateRequest;
import com.usach.mytingeso.dto.SubjectResponse;
import com.usach.mytingeso.dto.SubjectUpdateRequest;
import com.usach.mytingeso.services.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SubjectController {

        private final SubjectService service;

        @GetMapping("/study-plans/{planId}/subjects")
        public ResponseEntity<List<SubjectResponse>> findByPlan(@PathVariable Long planId) {
            return ResponseEntity.ok(service.findByPlan(planId));
        }

        @PostMapping("/study-plans/{planId}/subjects")
        public ResponseEntity<SubjectResponse> create(@PathVariable Long planId,
                                                      @Valid @RequestBody SubjectCreateRequest request) {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.create(planId, request));
        }

        @GetMapping("/subjects/{id}")
        public ResponseEntity<SubjectResponse> findById(@PathVariable Long id) {
            return ResponseEntity.ok(service.findById(id));
        }

        @PutMapping("/subjects/{id}")
        public ResponseEntity<SubjectResponse> update(@PathVariable Long id,
                                                      @Valid @RequestBody SubjectUpdateRequest request) {
            return ResponseEntity.ok(service.update(id, request));
        }

        @DeleteMapping("/subjects/{id}")
        public ResponseEntity<Void> delete(@PathVariable Long id) {
            service.delete(id);
            return ResponseEntity.noContent().build();
        }
    }
