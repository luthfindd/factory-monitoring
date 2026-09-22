package com.example.factoryBackend.equipment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EquipmentStatusLogRepository extends JpaRepository<EquipmentStatusLog, Long> {

    Optional<EquipmentStatusLog> findFirstByEquipmentIdAndEndedAtIsNull(Long equipmentId);

    List<EquipmentStatusLog> findByEquipmentIdAndStartedAtAfter(Long equipmentId, java.time.Instant after);

    @Query("""
    select l from EquipmentStatusLog l
    where l.startedAt < :end and (l.endedAt is null or l.endedAt > :start)
    """)
    List<EquipmentStatusLog> findOverlapping(@Param("start") Instant start, @Param("end") Instant end);
}