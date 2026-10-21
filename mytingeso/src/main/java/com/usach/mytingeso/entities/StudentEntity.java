package com.usach.mytingeso.entities;

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
    @Column(nullable = false)
    private String academicState;
}
