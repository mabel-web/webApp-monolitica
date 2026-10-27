package com.usach.mytingeso.entities;

import com.usach.mytingeso.enums.AcademicState;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
public class StudentEntity extends UserEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "career_id")
    private CareerEntity career;

    @ManyToOne(optional = false)
    @JoinColumn(name = "study_plan_id")
    private StudyPlanEntity studyPlan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AcademicState academicState;
}
