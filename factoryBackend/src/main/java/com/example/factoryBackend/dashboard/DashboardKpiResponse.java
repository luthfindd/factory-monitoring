package com.example.factoryBackend.dashboard;

import java.math.BigDecimal;

public record DashboardKpiResponse(
        BigDecimal oee, BigDecimal oeeTarget,
        BigDecimal availability, BigDecimal availabilityTarget,
        BigDecimal performance, BigDecimal performanceTarget,
        BigDecimal quality, BigDecimal qualityTarget,
        long runningTimeSeconds,
        long downtimeSeconds
) {}
