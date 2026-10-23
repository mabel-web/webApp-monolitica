package com.usach.mytingeso.services;

import com.usach.mytingeso.dto.EnrollmentCreateRequest;
import com.usach.mytingeso.dto.EnrollmentResponse;
import com.usach.mytingeso.dto.StudentCreateRequest;
import com.usach.mytingeso.dto.StudentUpdateRequest;
import com.usach.mytingeso.entities.*;
import com.usach.mytingeso.repositories.*;
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
    private final EnrollmentRepository enrollmentRepository;
    private final SubjectRepository subjectRepository;

    public List<StudentEntity> findAll() {
        return studentRepository.findAll();
    }

    public StudentEntity findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante no encontrado"));
    }

    @Transactional
    public StudentEntity create(StudentCreateRequest request) {

        //
        String run = RunUtils.normalize(request.run());
        if (!RunUtils.isValid(run)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RUT Invalido");
        }

        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByRun(run)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El RUT ya esta registrado.");
        }
        if (userRepository.existsByEmail(email)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya esta registrado.");
        }

        CareerEntity career = findCareer(request.careerId());
        if (career.getStatus() != CareerStatus.ACTIVA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Carrera esta inactiva y no puede recibir nuevos estudiantes");
        }
            StudyPlanEntity plan = (request.studyPlanId() == null)
                ? findCurrentPlan(career.getId())
                : findPlanOfCareer(request.studyPlanId(), career.getId());

        StudentEntity student = new StudentEntity();
        student.setRun(run);
        student.setFullName(request.fullName().trim());
        student.setEmail(email);
        student.setCareer(career);
        student.setStudyPlan(plan);
        student.setAcademicState(AcademicState.REGULAR); //regla de negocio

        return studentRepository.save(student);
    }

    @Transactional
    public StudentEntity update(Long id, StudentUpdateRequest request){
        StudentEntity student = findById(id);
        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmailAndIdNot(email, id)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya existe");
        }
        CareerEntity career = findCareer(request.careerId());
        StudyPlanEntity plan = findPlanOfCareer(request.studyPlanId(), career.getId());

        boolean careerOrPlanChanged =
                !student.getCareer().getId().equals(career.getId()) || !student.getStudyPlan().getId().equals(plan.getId());
        if (careerOrPlanChanged && hasAcademicHistory(id)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La carrera y el plan de estudio no se pueden modificar por historial academico del estudiante.");
        }

        student.setFullName(request.fullName().trim());
        student.setEmail(email);
        student.setCareer(career);
        student.setStudyPlan(plan);
        return studentRepository.save(student);

    }
    @Transactional
    public StudentEntity changeState(Long id, AcademicState newState){
        StudentEntity student = findById(id);

        if (newState == AcademicState.EGRESADO || newState == AcademicState.ELIMINADO){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los estados EGRESADO y ELIMINADO no se pueden cambiar manualmente.");
        }

        student.setAcademicState(newState);
        return studentRepository.save(student);
    }
    @Transactional
    public void delete(Long id){
        StudentEntity student = findById(id);
        if (hasAcademicHistory(id)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Estudiante con historial academico no  puede ser eliminado/a.");
        }
        studentRepository.delete(student);
    }

    public List<EnrollmentResponse> findAcademicHistory(Long studentId) {
        findById(studentId);   // throws 404 if the student does not exist
        return enrollmentRepository.findByStudentId(studentId).stream()
                .map(e -> new EnrollmentResponse(e.getId(), e.getPeriod(), e.getSubjectCode(), e.getSubjectName(), e.getSubjectSct(), e.getFinalGrade()))
                .toList();
    }

    @Transactional
    public EnrollmentResponse enroll(Long studentId, EnrollmentCreateRequest request){
        StudentEntity student = findById(studentId);
        SubjectEntity subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La asignatura no se encontró.") );

    if (!subject.getStudyPlan().getId().equals(student.getStudyPlan().getId())){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esta asignatura no pertenece al plan de estudio del estudiante.");
    }
    EnrollmentEntity e = new EnrollmentEntity();
    e.setStudent(student);
    e.setSubject(subject);
    e.setPeriod(request.period().trim());
    e.setSubjectCode(subject.getCode());
    e.setSubjectName(subject.getName());
    e.setSubjectSct(subject.getSct());
    e.setFinalGrade(request.finalGrade());
    EnrollmentEntity saved = enrollmentRepository.save(e);

    return new EnrollmentResponse(saved.getId(), saved.getPeriod(), saved.getSubjectCode(),
            saved.getSubjectName(), saved.getSubjectSct(), saved.getFinalGrade());

    }

    //helpers

    private CareerEntity findCareer(Long careerId) {
        return careerRepository.findById(careerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Carrera no encontrada."));
    }

    private StudyPlanEntity findCurrentPlan(Long careerId) {
        return studyPlanRepository.findByCareerIdAndCurrentTrue(careerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.CONFLICT, "Carrera no tiene plan de estudios actual."));
    }

    private StudyPlanEntity findPlanOfCareer(Long planId, Long careerId) {
        return studyPlanRepository.findByIdAndCareerId(planId, careerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Este plan de estudios no pertenece a la carrera."));
    }
    private boolean hasAcademicHistory(Long studentId) {
        return enrollmentRepository.existsByStudentId(studentId);
    }

}
