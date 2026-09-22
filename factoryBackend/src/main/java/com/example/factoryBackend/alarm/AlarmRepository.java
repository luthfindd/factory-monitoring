package com.example.factoryBackend.alarm;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    @EntityGraph(attributePaths = "equipment")
    List<Alarm> findAllByOrderByRaisedAtDesc(Pageable pageable);

    @EntityGraph(attributePaths = "equipment")
    List<Alarm> findByLevelOrderByRaisedAtDesc(AlarmLevel level, Pageable pageable);
}