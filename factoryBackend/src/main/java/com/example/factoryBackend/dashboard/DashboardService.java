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

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

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
}