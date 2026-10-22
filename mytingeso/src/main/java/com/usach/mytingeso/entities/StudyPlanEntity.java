package com.usach.mytingeso.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "study_plans", uniqueConstraints = @UniqueConstraint(columnNames = {"career_id", "code"})) //entidad debil depende de carrera
@Data
@NoArgsConstructor
public class StudyPlanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String year;       //code "2024"

    @Column(nullable = false)
    private String name;

    @Column(name = "is_current", nullable = false)
    private boolean current; //si es vigente o no

    @ManyToOne(optional = false)
    @JoinColumn(name = "career_id")
    private CareerEntity career;
}
