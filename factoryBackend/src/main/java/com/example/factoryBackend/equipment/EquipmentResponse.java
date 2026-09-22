package com.example.factoryBackend.equipment;

import java.time.Instant;

public record EquipmentResponse(
        Long id,
        String name,
        String type,
        EquipmentStatus status,
        Instant updatedAt
) {
    public static EquipmentResponse from(Equipment e) {
        return new EquipmentResponse(e.getId(), e.getName(), e.getType(), e.getStatus(), e.getUpdatedAt());
    }
}