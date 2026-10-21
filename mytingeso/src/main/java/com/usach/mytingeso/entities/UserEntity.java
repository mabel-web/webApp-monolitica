package com.usach.mytingeso.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 12)
    private String run;
    private String fullName;
    @Column(nullable = false, unique = true)
    private String email;
}