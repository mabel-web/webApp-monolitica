package com.usach.mytingeso.controllers;

import com.usach.mytingeso.dto.SectionCreateRequest;
import com.usach.mytingeso.dto.SectionResponse;
import com.usach.mytingeso.dto.SectionUpdateRequest;
import com.usach.mytingeso.services.SectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SectionController {
    private final SectionService service;

    @GetMapping("/periods/{periodId}/sections")
    public ResponseEntity<List<SectionResponse>> findByPeriod(@PathVariable Long periodId) {
        return ResponseEntity.ok(service.findByPeriod(periodId));
    }

    @PostMapping("/periods/{periodId}/sections")
    public ResponseEntity<SectionResponse> create(@PathVariable Long periodId,
                                                  @Valid @RequestBody SectionCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(periodId, request));
    }

    @GetMapping("/sections/{id}")
    public ResponseEntity<SectionResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/sections/{id}")
    public ResponseEntity<SectionResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody SectionUpdateRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/sections/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
