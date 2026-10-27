package com.usach.mytingeso.services;

import com.usach.mytingeso.dto.PeriodCreateRequest;
import com.usach.mytingeso.entities.AcademicPeriodEntity;
import com.usach.mytingeso.enums.PeriodStatus;
import com.usach.mytingeso.repositories.AcademicPeriodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AcademicPeriodService {
     private final AcademicPeriodRepository periodRepository;

     public List<AcademicPeriodEntity> findAll(){
         return periodRepository.findAll();
     }

     public AcademicPeriodEntity findById(Long id){
         return periodRepository.findById(id)
                 .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "El periodo academico ya existe"));
     }
     @Transactional
    public AcademicPeriodEntity create(PeriodCreateRequest request){
         if (periodRepository.existsByYearAndSemester(request.year(), request.semester())){
             throw new ResponseStatusException(HttpStatus.CONFLICT, "El periodo academico ya existe");
         }
         AcademicPeriodEntity period = new AcademicPeriodEntity();
         period.setYear(request.year());
         period.setSemester(request.semester());
         period.setCode(request.year() + "-" + request.semester());
         period.setStatus(PeriodStatus.ABIERTO);
         return periodRepository.save(period);

     }
}
