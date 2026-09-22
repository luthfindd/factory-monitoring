package com.example.factoryBackend.alarm;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alarms")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmRepository repository;

    @GetMapping
    public List<AlarmResponse> findRecent(
            @RequestParam(required = false) AlarmLevel level,
            @RequestParam(defaultValue = "10") int limit) {

        Pageable page = PageRequest.of(0, Math.max(1, Math.min(limit, 100)));
        List<Alarm> alarms = (level == null)
                ? repository.findAllByOrderByRaisedAtDesc(page)
                : repository.findByLevelOrderByRaisedAtDesc(level, page);

        return alarms.stream().map(AlarmResponse::from).toList();
    }
}