package com.usach.mytingeso.controllers;

import com.usach.mytingeso.dto.PeriodCreateRequest;
import com.usach.mytingeso.dto.PeriodStatusRequest;
import com.usach.mytingeso.entities.AcademicPeriodEntity;
import com.usach.mytingeso.services.AcademicPeriodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/periods")
@RequiredArgsConstructor
public class AcademicPeriodController {
    private final AcademicPeriodService service;

    @GetMapping
    public ResponseEntity<List<AcademicPeriodEntity>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AcademicPeriodEntity> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<AcademicPeriodEntity> create(@Valid @RequestBody PeriodCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AcademicPeriodEntity> changeStatus(@PathVariable Long id,
                                                             @Valid @RequestBody PeriodStatusRequest request) {
        return ResponseEntity.ok(service.changeStatus(id, request.status()));
    }
}
