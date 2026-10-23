package com.usach.mytingeso.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "subjects", uniqueConstraints = @UniqueConstraint(columnNames = {"study_plan_id", "code"}))
@Data
@NoArgsConstructor

public class SubjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    private String name;

    @Column(nullable = false)
    private int semester;

    private int theoryHours;
    private int exerciseHours;
    private int labHours;

    @Column(nullable = false)
    private int sct;

    @ManyToOne(optional = false)
    @JoinColumn(name = "study_plan_id")
    private StudyPlanEntity studyPlan;

    @ManyToMany
    @JoinTable(name = "subject_prerequisites",
            joinColumns = @JoinColumn(name = "subject_id"),
    inverseJoinColumns = @JoinColumn(name = "prerequisite_id"))
    private Set<SubjectEntity> prerequisites = new HashSet<>(); //no entiendo esta linea


}
