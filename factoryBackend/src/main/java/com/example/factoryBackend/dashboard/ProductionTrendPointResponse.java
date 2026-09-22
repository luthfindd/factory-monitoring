package com.example.factoryBackend.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductionTrendPointResponse(
        LocalDate date,
        BigDecimal plannedKg,
        BigDecimal actualKg,
        BigDecimal oeePercent
) {}