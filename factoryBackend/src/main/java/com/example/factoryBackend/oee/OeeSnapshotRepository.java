package com.example.factoryBackend.oee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OeeSnapshotRepository extends JpaRepository<OeeSnapshot, Long> {

    Optional<OeeSnapshot> findTopByOrderByRecordedAtDesc();
    List<OeeSnapshot> findByRecordedAtGreaterThanEqualOrderByRecordedAtAsc(Instant since);
}