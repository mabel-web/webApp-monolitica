package com.usach.mytingeso.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
@Data
@NoArgsConstructor
public class EnrollmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private StudentEntity student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "subject_id")
    private SubjectEntity subject;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private SectionEntity section;

    @ManyToOne
    @JoinColumn(name = "period_id")
    private AcademicPeriodEntity period;

    //los datos que guardar al momento de inscribir
    @Column(nullable = false)
    private String subjectCode;
    @Column (nullable = false)
    private String subjectName;
    @Column(nullable = false)
    private int subjectSct;

    private Double finalGrade;

    @Column(nullable = false, updatable = false)
    private LocalDateTime enrolledAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status;


}
