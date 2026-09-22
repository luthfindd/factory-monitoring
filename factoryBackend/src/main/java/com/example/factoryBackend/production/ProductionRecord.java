package com.example.factoryBackend.production;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "production_record")
@Getter
@Setter
@NoArgsConstructor
public class ProductionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    @Column(name = "planned_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal plannedKg;

    @Column(name = "actual_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal actualKg;

    @Column(name = "good_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal goodKg;

    @Column(name = "reject_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal rejectKg;
}