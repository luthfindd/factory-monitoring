package com.example.factoryBackend.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;

    @GetMapping("/kpi")
    public DashboardKpiResponse kpi() {
        return service.getKpi();
    }

    @GetMapping("/oee-trend")
    public List<OeeTrendPointResponse> oeeTrend() {
        return service.getOeeTrend();
    }

    @GetMapping("/equipment-status")
    public List<EquipmentStatusResponse> equipmentStatus() {
        return service.getEquipmentStatus();
    }

    @GetMapping("/production-trend")
    public List<ProductionTrendPointResponse> productionTrend() {
        return service.getProductionTrend();
    }

    @GetMapping("/production-summary")
    public ProductionSummaryResponse productionSummary() {
        return service.getProductionSummary();
    }
}