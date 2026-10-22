package com.usach.mytingeso.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "careers")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CareerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false, unique = true)
    private String name;


    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CareerStatus status; //inicialmente activa

    private int durationSemesters;
}
