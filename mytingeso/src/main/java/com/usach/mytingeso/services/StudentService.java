package com.usach.mytingeso.services;

import com.usach.mytingeso.dto.StudentCreateRequest;
import com.usach.mytingeso.entities.CareerEntity;
import com.usach.mytingeso.entities.StudentEntity;
import com.usach.mytingeso.entities.StudyPlanEntity;
import com.usach.mytingeso.repositories.CareerRepository;
import com.usach.mytingeso.repositories.StudentRepository;
import com.usach.mytingeso.repositories.StudyPlanRepository;
import com.usach.mytingeso.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.usach.mytingeso.utils.RunUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository; //para acceder a la bb indirectamente x repositorios
    private final UserRepository userRepository;
    private final CareerRepository careerRepository;
    private final StudyPlanRepository studyPlanRepository;

    public List<StudentEntity> findAll() {
        return studentRepository.findAll();
    }

    public StudentEntity findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
    }

    @Transactional
    public StudentEntity create(StudentCreateRequest request) {

        //
        String run = RunUtils.normalize(request.run());
        if (!RunUtils.isValid(run)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid RUN");
        }

        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByRun(run)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "RUN already exists");
        }
        if (userRepository.existsByEmail(email)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        CareerEntity career = careerRepository.findById(request.careerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Career not found"));


        StudyPlanEntity plan = studyPlanRepository.findByCareerIdAndCurrentTrue(career.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Career has no current study plan"));

        StudentEntity student = new StudentEntity();
        student.setRun(run);
        student.setFullName(request.fullName().trim());
        student.setEmail(email);
        student.setCareer(career);
        student.setStudyPlan(plan);
        student.setAcademicState("ACTIVE");

        return studentRepository.save(student);
    }
}
