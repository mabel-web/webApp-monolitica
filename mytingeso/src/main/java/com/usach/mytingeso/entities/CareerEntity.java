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
    private Long careerId;

    @Column(nullable = false, unique = true)
    private String nameCareer;
    private String description;
    private boolean active; //inicialmente activa
}
