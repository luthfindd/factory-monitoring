package com.example.factoryBackend.oee;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "oee_snapshot")
@Getter
@Setter
@NoArgsConstructor
public class OeeSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal availability;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal performance;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal quality;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal oee;
}