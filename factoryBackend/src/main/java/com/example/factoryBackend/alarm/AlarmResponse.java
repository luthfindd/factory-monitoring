package com.example.factoryBackend.alarm;

import java.time.Instant;

public record AlarmResponse(
        Long id,
        Long equipmentId,
        String equipmentName,
        String area,
        String message,
        AlarmLevel level,
        Instant raisedAt,
        Instant acknowledgedAt
) {
    public static AlarmResponse from(Alarm a) {
        return new AlarmResponse(
                a.getId(),
                a.getEquipment().getId(),
                a.getEquipment().getName(),
                a.getArea(),
                a.getMessage(),
                a.getLevel(),
                a.getRaisedAt(),
                a.getAcknowledgedAt()
        );
    }
}