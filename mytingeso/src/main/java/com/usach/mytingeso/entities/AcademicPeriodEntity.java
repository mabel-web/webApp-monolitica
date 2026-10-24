package com.usach.mytingeso.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "academic_periods", uniqueConstraints = @UniqueConstraint(columnNames = {"year","semester"}))
@Data
@RequiredArgsConstructor
public class AcademicPeriodEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "year", nullable = false)
    private int year;

    @Column(nullable = false)
    private int semester;

    @Column(nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PeriodStatus status;

}
