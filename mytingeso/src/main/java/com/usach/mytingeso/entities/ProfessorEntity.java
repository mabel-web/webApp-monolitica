package com.usach.mytingeso.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "professors")
@Data
@NoArgsConstructor
public class ProfessorEntity extends UserEntity{
    @Column(nullable = false)
    private String professionalTitle;

    @Column(nullable = false)
    private String academicDegree;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProfessorStatus status; //true es activo, false inactivo

    private String responsibleSection;
}
