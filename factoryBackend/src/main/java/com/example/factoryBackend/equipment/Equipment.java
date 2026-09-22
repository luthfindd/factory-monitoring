package com.example.factoryBackend.equipment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "equipment")
@Getter
@Setter
@NoArgsConstructor
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false, length = 30)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EquipmentStatus status;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}