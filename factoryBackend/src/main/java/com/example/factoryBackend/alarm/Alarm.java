package com.example.factoryBackend.alarm;

import com.example.factoryBackend.equipment.Equipment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "alarm")
@Getter
@Setter
@NoArgsConstructor
public class Alarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @Column(nullable = false, length = 50)
    private String area;

    @Column(nullable = false, length = 200)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AlarmLevel level;

    @Column(name = "raised_at", nullable = false)
    private Instant raisedAt;

    @Column(name = "acknowledged_at")
    private Instant acknowledgedAt;
}