package com.usach.mytingeso.controllers;

import com.usach.mytingeso.dto.StudentCreateRequest;
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

}
