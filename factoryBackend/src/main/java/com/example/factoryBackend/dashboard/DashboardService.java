package com.example.factoryBackend.dashboard;

import com.example.factoryBackend.equipment.EquipmentRepository;
import com.example.factoryBackend.equipment.EquipmentStatus;
import com.example.factoryBackend.equipment.EquipmentStatusLog;
import com.example.factoryBackend.equipment.EquipmentStatusLogRepository;
import com.example.factoryBackend.oee.OeeSnapshot;
import com.example.factoryBackend.oee.OeeSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.example.factoryBackend.production.ProductionRecord;
import com.example.factoryBackend.production.ProductionRecordRepository;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;


import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final BigDecimal OEE_TARGET = BigDecimal.valueOf(85);
    private static final BigDecimal AVAILABILITY_TARGET = BigDecimal.valueOf(90);
    private static final BigDecimal PERFORMANCE_TARGET = BigDecimal.valueOf(95);
    private static final BigDecimal QUALITY_TARGET = BigDecimal.valueOf(98);

    private final OeeSnapshotRepository oeeRepository;
    private final EquipmentRepository equipmentRepository;
    private final EquipmentStatusLogRepository statusLogRepository;
    private final ProductionRecordRepository productionRepository;

    public DashboardKpiResponse getKpi() {
        OeeSnapshot latest = oeeRepository.findTopByOrderByRecordedAtDesc()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Belum ada data OEE"));

        Instant now = Instant.now();
        Instant startOfDay = now.truncatedTo(ChronoUnit.DAYS);

        long equipmentCount = equipmentRepository.count();
        List<EquipmentStatusLog> logs = statusLogRepository.findOverlapping(startOfDay, now);

        long runningSeconds = 0;
        long totalSeconds = 0;

        for (EquipmentStatusLog log : logs) {
            Instant start = log.getStartedAt().isBefore(startOfDay) ? startOfDay : log.getStartedAt();
            Instant end = (log.getEndedAt() == null || log.getEndedAt().isAfter(now)) ? now : log.getEndedAt();
            if (end.isBefore(start)) continue;

            long seconds = Duration.between(start, end).getSeconds();
            totalSeconds += seconds;
            if (log.getStatus() == EquipmentStatus.RUNNING) {
                runningSeconds += seconds;
            }
        }

        long avgRunning = equipmentCount == 0 ? 0 : runningSeconds / equipmentCount;
        long avgTotal = equipmentCount == 0 ? 0 : totalSeconds / equipmentCount;
        long avgDowntime = Math.max(0, avgTotal - avgRunning);

        return new DashboardKpiResponse(
                latest.getOee(), OEE_TARGET,
                latest.getAvailability(), AVAILABILITY_TARGET,
                latest.getPerformance(), PERFORMANCE_TARGET,
                latest.getQuality(), QUALITY_TARGET,
                avgRunning,
                avgDowntime
        );
    }

    public List<OeeTrendPointResponse> getOeeTrend() {
        Instant since = Instant.now().minus(Duration.ofHours(24));
        return oeeRepository.findByRecordedAtGreaterThanEqualOrderByRecordedAtAsc(since).stream()
                .map(s -> new OeeTrendPointResponse(s.getRecordedAt(), s.getOee()))
                .toList();
    }

    public List<EquipmentStatusResponse> getEquipmentStatus() {
        Instant now = Instant.now();
        Instant startOfDay = now.truncatedTo(ChronoUnit.DAYS);

        List<EquipmentStatusLog> logs = statusLogRepository.findOverlapping(startOfDay, now);

        Map<Long, Long> runningByEquipment = new LinkedHashMap<>();
        Map<Long, Long> totalByEquipment = new LinkedHashMap<>();

        for (EquipmentStatusLog log : logs) {
            Instant start = log.getStartedAt().isBefore(startOfDay) ? startOfDay : log.getStartedAt();
            Instant end = (log.getEndedAt() == null || log.getEndedAt().isAfter(now)) ? now : log.getEndedAt();
            if (end.isBefore(start)) continue;

            long seconds = Duration.between(start, end).getSeconds();
            Long equipmentId = log.getEquipment().getId();
            totalByEquipment.merge(equipmentId, seconds, Long::sum);
            if (log.getStatus() == EquipmentStatus.RUNNING) {
                runningByEquipment.merge(equipmentId, seconds, Long::sum);
            }
        }

        return equipmentRepository.findAll().stream()
                .map(eq -> {
                    long running = runningByEquipment.getOrDefault(eq.getId(), 0L);
                    long total = totalByEquipment.getOrDefault(eq.getId(), 0L);
                    long downtime = Math.max(0, total - running);
                    return new EquipmentStatusResponse(eq.getId(), eq.getName(), eq.getStatus().name(), running, downtime);
                })
                .toList();
    }

    public List<ProductionTrendPointResponse> getProductionTrend() {
        Instant since = Instant.now().minus(Duration.ofDays(7)).truncatedTo(ChronoUnit.DAYS);
        List<ProductionRecord> records = productionRepository.findByRecordedAtGreaterThanEqualOrderByRecordedAtAsc(since);

        Map<LocalDate, List<ProductionRecord>> byDay = new LinkedHashMap<>();
        for (ProductionRecord r : records) {
            LocalDate day = r.getRecordedAt().atZone(ZoneOffset.UTC).toLocalDate();
            byDay.computeIfAbsent(day, d -> new ArrayList<>()).add(r);
        }

        return byDay.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    BigDecimal planned = sum(entry.getValue(), ProductionRecord::getPlannedKg);
                    BigDecimal actual = sum(entry.getValue(), ProductionRecord::getActualKg);
                    BigDecimal good = sum(entry.getValue(), ProductionRecord::getGoodKg);
                    BigDecimal oeePercent = actual.compareTo(BigDecimal.ZERO) == 0
                            ? BigDecimal.ZERO
                            : good.multiply(BigDecimal.valueOf(100)).divide(actual, 2, java.math.RoundingMode.HALF_UP);
                    return new ProductionTrendPointResponse(entry.getKey(), planned, actual, oeePercent);
                })
                .toList();
    }

    public ProductionSummaryResponse getProductionSummary() {
        Instant startOfDay = Instant.now().truncatedTo(ChronoUnit.DAYS);
        List<ProductionRecord> records = productionRepository.findByRecordedAtGreaterThanEqualOrderByRecordedAtAsc(startOfDay);

        BigDecimal planned = sum(records, ProductionRecord::getPlannedKg);
        BigDecimal actual = sum(records, ProductionRecord::getActualKg);
        BigDecimal good = sum(records, ProductionRecord::getGoodKg);
        BigDecimal reject = sum(records, ProductionRecord::getRejectKg);
        BigDecimal yieldPercent = actual.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : good.multiply(BigDecimal.valueOf(100)).divide(actual, 2, java.math.RoundingMode.HALF_UP);

        return new ProductionSummaryResponse(planned, actual, good, reject, yieldPercent);
    }

    private BigDecimal sum(List<ProductionRecord> records, java.util.function.Function<ProductionRecord, BigDecimal> getter) {
        return records.stream().map(getter).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}