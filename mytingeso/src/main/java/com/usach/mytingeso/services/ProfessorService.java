package com.usach.mytingeso.services;

import com.usach.mytingeso.dto.ProfessorCreateRequest;
import com.usach.mytingeso.dto.ProfessorUpdateRequest;
import com.usach.mytingeso.entities.PeriodStatus;
import com.usach.mytingeso.entities.ProfessorEntity;
import com.usach.mytingeso.entities.ProfessorStatus;
import com.usach.mytingeso.repositories.ProfessorRepository;
import com.usach.mytingeso.repositories.SectionRepository;
import com.usach.mytingeso.repositories.UserRepository;
import com.usach.mytingeso.utils.RunUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfessorService{

    private final ProfessorRepository professorRepository;
    private final UserRepository userRepository;
    private final SectionRepository sectionRepository;
    public List<ProfessorEntity> findAll(){
        return professorRepository.findAll();
    }

    public ProfessorEntity findById(Long id){
        return professorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesor/a no encontrado."));
    }

    @Transactional
    public ProfessorEntity create(ProfessorCreateRequest request){
        String run = RunUtils.normalize(request.run());
        if (!RunUtils.isValid(run)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RUN Inválido.");
        }
        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByRun(run)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya existe.");
        }

        ProfessorEntity professor = new ProfessorEntity();
        professor.setRun(run);
        professor.setFullName(request.fullName().trim());
        professor.setEmail(email);
        professor.setProfessionalTitle(request.professionalTitle().trim());
        professor.setAcademicDegree(request.academicDegree().trim());
        professor.setStatus(ProfessorStatus.ACTIVO);
        return professorRepository.save(professor);
    }

    @Transactional
    public ProfessorEntity update(Long id, ProfessorUpdateRequest request){
        ProfessorEntity professor = findById(id);
        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmailAndIdNot(email, id)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya existe.");
        }
        professor.setFullName(request.fullName().trim());
        professor.setEmail(email);
        professor.setProfessionalTitle(request.professionalTitle().trim());
        professor.setAcademicDegree(request.academicDegree().trim());
        return professorRepository.save(professor);
    }

    @Transactional
    public ProfessorEntity changeStatus(Long id, ProfessorStatus newStatus){
        ProfessorEntity professor = findById(id);

        if (newStatus == ProfessorStatus.INACTIVO && hasSectionsInOpenPeriod(id)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No puedo desactivarse por tener periodo academico abierto.");
        }
        professor.setStatus(newStatus);
        return professorRepository.save(professor);
    }

    @Transactional
    public void delete(Long id){
        ProfessorEntity professor = findById(id);

        if (hasSections(id)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Profesor/a tiene secciones y no puede ser eliminado.");
        }
        professorRepository.delete(professor);
    }

    private boolean hasSections(Long professorId) {
        return sectionRepository.existsByProfessorId(professorId);
    }

    private boolean hasSectionsInOpenPeriod(Long professorId) {
        return sectionRepository.existsByProfessorIdAndPeriodStatus(professorId, PeriodStatus.ABIERTO);
    }


}
