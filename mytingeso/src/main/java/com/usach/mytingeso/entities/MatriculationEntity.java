package com.usach.mytingeso.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "matriculations", uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "period_id"}))
public class MatriculationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private StudentEntity student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "period_id")
    private AcademicPeriodEntity period;

}
