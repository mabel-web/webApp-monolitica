package com.usach.mytingeso.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "academic_administrators")
@NoArgsConstructor
public class AcademicAdministratorEntity extends UserEntity{
}
