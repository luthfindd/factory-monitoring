package com.example.factoryBackend.equipment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EquipmentStatusLogRepository extends JpaRepository<EquipmentStatusLog, Long> {

    Optional<EquipmentStatusLog> findFirstByEquipmentIdAndEndedAtIsNull(Long equipmentId);

    List<EquipmentStatusLog> findByEquipmentIdAndStartedAtAfter(Long equipmentId, java.time.Instant after);
}