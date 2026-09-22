package com.example.factoryBackend.dashboard;

import java.math.BigDecimal;

public record ProductionSummaryResponse(
        BigDecimal plannedKg,
        BigDecimal actualKg,
        BigDecimal goodKg,
        BigDecimal rejectKg,
        BigDecimal yieldPercent
) {}