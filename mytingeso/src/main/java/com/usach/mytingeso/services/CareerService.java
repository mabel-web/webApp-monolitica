package com.usach.mytingeso.services;


import com.usach.mytingeso.dto.CareerCreateRequest;
import com.usach.mytingeso.dto.CareerUpdateRequest;
import com.usach.mytingeso.entities.CareerEntity;
import com.usach.mytingeso.enums.CareerStatus;
import com.usach.mytingeso.repositories.CareerRepository;
import com.usach.mytingeso.repositories.StudentRepository;
import com.usach.mytingeso.repositories.StudyPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CareerService {
    public static  final int duration_semesters = 4;
    private final CareerRepository careerRepository;
    private final StudyPlanRepository studyPlanRepository;
    private final StudentRepository studentRepository;

    public List<CareerEntity> findAll(){
        return careerRepository.findAll();
    }
    public CareerEntity findById(Long id){
        return careerRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carrera no encontrada"));
    }

    @Transactional
    public CareerEntity create(CareerCreateRequest request){
        String code = request.code().trim().toUpperCase();
        //regla de negocio
        if (careerRepository.existsByCode(code)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El codigo de la carrera ya existe");
        }

        CareerEntity career = new CareerEntity();
        career.setCode(code);
        career.setName(request.name().trim());
        career.setDescription(request.description().trim());
        career.setStatus(CareerStatus.ACTIVA);     //regla de negocio
        career.setDurationSemesters(duration_semesters);   //regla
        return careerRepository.save(career);
    }
    @Transactional
    public CareerEntity update(Long id, CareerUpdateRequest request){
        CareerEntity career = findById(id);
        career.setName(request.name().trim());
        career.setDescription(request.description().trim());
        return careerRepository.save(career);
    }

    @Transactional
    public CareerEntity changeStatus(Long id, CareerStatus status){
        CareerEntity career = findById(id);
        career.setStatus(status);
        return careerRepository.save(career);
    }

    @Transactional
    public void delete(Long id){
        CareerEntity career = findById(id);

        if(studentRepository.existsByCareerId(id) || studyPlanRepository.existsByCareerId(id)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La carrera tiene estudiantes y no se puede eliminar");
        }
        careerRepository.delete(career);
    }
}
