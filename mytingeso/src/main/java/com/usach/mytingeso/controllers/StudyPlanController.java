package com.usach.mytingeso.controllers;

import com.usach.mytingeso.dto.StudyPlanCreateRequest;
import com.usach.mytingeso.entities.StudyPlanEntity;
import com.usach.mytingeso.services.StudyPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StudyPlanController {
    private final StudyPlanService service;

    @GetMapping("/careers/{careerId}/study-plans")
    public ResponseEntity<List<StudyPlanEntity>> findByCareer(@PathVariable Long careerId) {
        return ResponseEntity.ok(service.findByCareer(careerId));
    }

    @PostMapping("/careers/{careerId}/study-plans")
    public ResponseEntity<StudyPlanEntity> create(@PathVariable Long careerId,
                                                  @Valid @RequestBody StudyPlanCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(careerId, request));
    }

    @GetMapping("/study-plans/{id}")
    public ResponseEntity<StudyPlanEntity> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/study-plans/{id}/current")
    public ResponseEntity<StudyPlanEntity> setCurrent(@PathVariable Long id) {
        return ResponseEntity.ok(service.setCurrent(id));
    }

    @DeleteMapping("/study-plans/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }


}
