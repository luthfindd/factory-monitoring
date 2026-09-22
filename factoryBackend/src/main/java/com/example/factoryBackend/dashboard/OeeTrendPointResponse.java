package com.example.factoryBackend.dashboard;

import java.math.BigDecimal;
import java.time.Instant;

public record OeeTrendPointResponse(Instant recordedAt, BigDecimal oee) {}