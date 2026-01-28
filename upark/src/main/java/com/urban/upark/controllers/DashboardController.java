package com.urban.upark.controllers;

import com.urban.upark.dto.DashboardDTO;
import com.urban.upark.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/{ownerId}")
    public ResponseEntity<DashboardDTO> getDashboard(@PathVariable Integer ownerId) {
        DashboardDTO dashboard = dashboardService.getOwnerDashboard(ownerId);
        return ResponseEntity.ok(dashboard);
    }
}