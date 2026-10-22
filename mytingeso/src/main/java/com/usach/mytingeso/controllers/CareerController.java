package com.usach.mytingeso.controllers;

import com.usach.mytingeso.dto.CareerCreateRequest;
import com.usach.mytingeso.dto.CareerStatusRequest;
import com.usach.mytingeso.dto.CareerUpdateRequest;
import com.usach.mytingeso.entities.CareerEntity;
import com.usach.mytingeso.services.CareerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/careers")
@RequiredArgsConstructor
public class CareerController {
    private final CareerService careerService;

    @GetMapping
    public ResponseEntity<List<CareerEntity>> findAll(){
        return ResponseEntity.ok(careerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CareerEntity> findById(@PathVariable Long id){
        return ResponseEntity.ok(careerService.findById(id));
    }
    @PostMapping
    public ResponseEntity<CareerEntity> create(@Valid @RequestBody CareerCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(careerService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CareerEntity> update(@PathVariable Long id,
                                               @Valid @RequestBody CareerUpdateRequest request) {
        return ResponseEntity.ok(careerService.update(id, request));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<CareerEntity> changeStatus(@PathVariable Long id,
                                                     @Valid @RequestBody CareerStatusRequest request) {
        return ResponseEntity.ok(careerService.changeStatus(id, request.status()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        careerService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
