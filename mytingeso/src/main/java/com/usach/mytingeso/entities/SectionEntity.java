package com.usach.mytingeso.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sections", uniqueConstraints = @UniqueConstraint(columnNames = {"subject_id", "period_id"}))
@Data
@NoArgsConstructor
public class SectionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "subject_id")
    private SubjectEntity subject;

    @ManyToOne(optional = false)
    @JoinColumn(name = "period_id")
    private AcademicPeriodEntity period;

    @ManyToOne(optional = false)
    @JoinColumn(name = "professor_id")
    private ProfessorEntity professor;

    private int capacity;

    @ElementCollection
    @CollectionTable(name = "section_schedules", joinColumns = @JoinColumn(name = "section_id"))
    @Column(name = "schedule_code", nullable = false)
    private Set<String> schedule = new HashSet<>();

}
