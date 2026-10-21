package com.usach.mytingeso.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "professors")
@Data
@NoArgsConstructor
public class ProfessorEntity extends UserEntity{
    private String professionalTitle;
    private String academicDegree;
    private boolean active; //true es activo, false inactivo
    private String responsibleSection;
}
