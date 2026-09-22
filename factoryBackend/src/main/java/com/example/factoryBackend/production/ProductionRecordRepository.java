package com.example.factoryBackend.production;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface ProductionRecordRepository extends JpaRepository<ProductionRecord, Long> {

    List<ProductionRecord> findByRecordedAtGreaterThanEqualOrderByRecordedAtAsc(Instant since);

}