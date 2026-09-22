package com.example.factoryBackend.equipment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentRepository repository;

    @GetMapping
    public List<EquipmentResponse> findAll() {
        return repository.findAll().stream().map(EquipmentResponse::from).toList();
    }

    @GetMapping("/{id}")
    public EquipmentResponse findById(@PathVariable Long id) {
        return repository.findById(id)
                .map(EquipmentResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipment not found"));
    }
}