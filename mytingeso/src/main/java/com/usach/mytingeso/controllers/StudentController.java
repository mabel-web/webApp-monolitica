package com.usach.mytingeso.controllers;

import com.usach.mytingeso.dto.*;
import com.usach.mytingeso.entities.StudentEntity;
import com.usach.mytingeso.services.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<List<StudentEntity>> findAll(){
        return ResponseEntity.ok(studentService.findAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<StudentEntity> findById(@PathVariable Long id){
        return ResponseEntity.ok(studentService.findById(id));
    }
    @PostMapping
    public ResponseEntity<StudentEntity> create(@Valid @RequestBody StudentCreateRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.create(request));
    }
    @PutMapping("/{id}")
    public ResponseEntity<StudentEntity> update(@PathVariable Long id,
                                                @Valid @RequestBody StudentUpdateRequest request) {
        return ResponseEntity.ok(studentService.update(id, request));
    }

    @PutMapping("/{id}/state")
    public ResponseEntity<StudentEntity> changeState(@PathVariable Long id,
                                                     @Valid @RequestBody StudentStateRequest request) {
        return ResponseEntity.ok(studentService.changeState(id, request.state()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/academic-history")
    public ResponseEntity<List<EnrollmentResponse>> academicHistory(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.findAcademicHistory(id));
    }


}
