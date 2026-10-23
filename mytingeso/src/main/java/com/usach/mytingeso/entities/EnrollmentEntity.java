package com.usach.mytingeso.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(nullable = false)
    private String period;

    //los datos que guardar al momento de inscribir
    @Column(nullable = false)
    private String subjectCode;
    @Column (nullable = false)
    private String subjectName;
    @Column(nullable = false)
    private int subjectSct;

    private Double finalGrade;


}
