package com.example.factoryBackend.production;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionRecordRepository extends JpaRepository<ProductionRecord, Long> {
}