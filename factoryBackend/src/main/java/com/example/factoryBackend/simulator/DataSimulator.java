package com.example.factoryBackend.simulator;


import com.example.factoryBackend.alarm.Alarm;
import com.example.factoryBackend.alarm.AlarmLevel;
import com.example.factoryBackend.alarm.AlarmRepository;
import com.example.factoryBackend.equipment.Equipment;
import com.example.factoryBackend.equipment.EquipmentRepository;
import com.example.factoryBackend.equipment.EquipmentStatus;
import com.example.factoryBackend.equipment.EquipmentStatusLog;
import com.example.factoryBackend.equipment.EquipmentStatusLogRepository;
import com.example.factoryBackend.oee.OeeSnapshot;
import com.example.factoryBackend.oee.OeeSnapshotRepository;
import com.example.factoryBackend.production.ProductionRecord;
import com.example.factoryBackend.production.ProductionRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataSimulator {

    private static final long SLOT_SECONDS = 15 * 60;
    private static final int HISTORY_DAYS = 7;
    private static final double PLANNED_KG_PER_DAY = 10_000;
    private static final double SLOTS_PER_DAY = 24 * 3600.0 / SLOT_SECONDS;
    private static final String[] FAULT_MESSAGES = {
            "Motor Overload", "High Temperature", "Low Pressure", "Sensor Fault", "Power Failure"
    };

    private final OeeSnapshotRepository oeeRepository;
    private final ProductionRecordRepository productionRepository;
    private final Random random = new Random();

    private final EquipmentRepository equipmentRepository;
    private final EquipmentStatusLogRepository statusLogRepository;
    private final AlarmRepository alarmRepository;

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void catchUp() {
        Instant now = floorToSlot(Instant.now());
        Instant earliest = now.minus(Duration.ofDays(HISTORY_DAYS));

        Instant next = oeeRepository.findTopByOrderByRecordedAtDesc()
                .map(s -> s.getRecordedAt().plusSeconds(SLOT_SECONDS))
                .orElse(earliest);
        if (next.isBefore(earliest)) {
            next = earliest;
        }

        List<OeeSnapshot> snapshots = new ArrayList<>();
        List<ProductionRecord> records = new ArrayList<>();

        for (Instant slot = next; !slot.isAfter(now); slot = slot.plusSeconds(SLOT_SECONDS)) {
            double t = slot.getEpochSecond() / 3600.0; // jam sejak epoch, untuk gelombang sinus

            double availability = clamp(85 + 6 * Math.sin(t / 3.0) + 3 * Math.sin(t / 11.0) + noise(1.0), 55, 99.5);
            double performance = clamp(91 + 4 * Math.sin(t / 2.0 + 1) + noise(1.0), 60, 99.5);
            double quality = clamp(96 + 1.5 * Math.sin(t / 5.0 + 2) + noise(0.5), 85, 99.9);
            double oee = availability * performance * quality / 10_000;

            double planned = PLANNED_KG_PER_DAY / SLOTS_PER_DAY;
            double actual = planned * availability / 100 * performance / 100;
            double good = actual * quality / 100;

            OeeSnapshot s = new OeeSnapshot();
            s.setRecordedAt(slot);
            s.setAvailability(bd(availability));
            s.setPerformance(bd(performance));
            s.setQuality(bd(quality));
            s.setOee(bd(oee));
            snapshots.add(s);

            ProductionRecord r = new ProductionRecord();
            r.setRecordedAt(slot);
            r.setPlannedKg(bd(planned));
            r.setActualKg(bd(actual));
            r.setGoodKg(bd(good));
            r.setRejectKg(bd(actual - good));
            records.add(r);
        }

        if (snapshots.isEmpty()) {
            return;
        }
        oeeRepository.saveAll(snapshots);
        productionRepository.saveAll(records);
        log.info("Simulator: {} slot baru diisi sampai {}", snapshots.size(), now);
    }

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void tickEquipment() {
        Instant now = Instant.now();
        List<Equipment> allEquipment = equipmentRepository.findAll();

        for (Equipment eq : allEquipment) {
            // peluang 3% tiap menit untuk berubah status
            if (random.nextDouble() > 0.03) {
                continue;
            }

            EquipmentStatus newStatus = pickNextStatus(eq.getStatus());
            if (newStatus == eq.getStatus()) {
                continue;
            }

            statusLogRepository.findFirstByEquipmentIdAndEndedAtIsNull(eq.getId())
                    .ifPresent(log -> log.setEndedAt(now));

            EquipmentStatusLog newLog = new EquipmentStatusLog();
            newLog.setEquipment(eq);
            newLog.setStatus(newStatus);
            newLog.setStartedAt(now);
            statusLogRepository.save(newLog);

            eq.setStatus(newStatus);
            eq.setUpdatedAt(now);
            equipmentRepository.save(eq);

            if (newStatus == EquipmentStatus.FAULT) {
                Alarm alarm = new Alarm();
                alarm.setEquipment(eq);
                alarm.setArea(eq.getName());
                alarm.setMessage(FAULT_MESSAGES[random.nextInt(FAULT_MESSAGES.length)]);
                alarm.setLevel(randomLevel());
                alarm.setRaisedAt(now);
                alarmRepository.save(alarm);
            }
        }
    }

    private EquipmentStatus pickNextStatus(EquipmentStatus current) {
        // dari RUNNING: sebagian besar balik RUNNING, kadang IDLE/FAULT/STOPPED
        double r = random.nextDouble();
        return switch (current) {
            case RUNNING -> r < 0.5 ? EquipmentStatus.IDLE : r < 0.85 ? EquipmentStatus.FAULT : EquipmentStatus.STOPPED;
            case IDLE, FAULT, STOPPED -> EquipmentStatus.RUNNING;
        };
    }

    private AlarmLevel randomLevel() {
        double r = random.nextDouble();
        if (r < 0.15) return AlarmLevel.CRITICAL;
        if (r < 0.45) return AlarmLevel.HIGH;
        if (r < 0.75) return AlarmLevel.MEDIUM;
        return AlarmLevel.LOW;
    }

    private Instant floorToSlot(Instant t) {
        return Instant.ofEpochSecond(t.getEpochSecond() / SLOT_SECONDS * SLOT_SECONDS);
    }

    private double noise(double sd) {
        return random.nextGaussian() * sd;
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private BigDecimal bd(double v) {
        return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP);
    }
}