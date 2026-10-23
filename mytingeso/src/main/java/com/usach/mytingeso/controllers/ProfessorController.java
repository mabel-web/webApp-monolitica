package com.usach.mytingeso.controllers;


import com.usach.mytingeso.dto.ProfessorCreateRequest;
import com.usach.mytingeso.dto.ProfessorStatusRequest;
import com.usach.mytingeso.dto.ProfessorUpdateRequest;
import com.usach.mytingeso.entities.ProfessorEntity;
import com.usach.mytingeso.services.ProfessorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professors")
@RequiredArgsConstructor
public class ProfessorController {
    private final ProfessorService professorService;

    @GetMapping
    public ResponseEntity<List<ProfessorEntity>> findAll() {
        return ResponseEntity.ok(professorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfessorEntity> findById(@PathVariable Long id) {
        return ResponseEntity.ok(professorService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProfessorEntity> create(@Valid @RequestBody ProfessorCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(professorService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfessorEntity> update(@PathVariable Long id,
                                                  @Valid @RequestBody ProfessorUpdateRequest request) {
        return ResponseEntity.ok(professorService.update(id, request));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ProfessorEntity> changeStatus(@PathVariable Long id,
                                                        @Valid @RequestBody ProfessorStatusRequest request) {
        return ResponseEntity.ok(professorService.changeStatus(id, request.status()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        professorService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
