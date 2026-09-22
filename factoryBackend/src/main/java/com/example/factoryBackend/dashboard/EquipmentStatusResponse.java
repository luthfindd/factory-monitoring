package com.example.factoryBackend.dashboard;

public record EquipmentStatusResponse(
        Long equipmentId,
        String name,
        String status,
        long runningTimeSeconds,
        long downtimeSeconds
) {}