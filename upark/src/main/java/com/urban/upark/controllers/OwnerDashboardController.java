package com.urban.upark.controllers;

import com.urban.upark.dto.DashboardDTO;
import com.urban.upark.services.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/owner-dashboard")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class OwnerDashboardController {

    private final DashboardService dashboardService;

    
    @GetMapping("/{ownerId}")
    public ResponseEntity<DashboardDTO> getOwnerDashboard(@PathVariable Integer ownerId) {
        try {
            log.info(" Requête dashboard owner mobile pour user: {}", ownerId);
            DashboardDTO dashboard = dashboardService.getOwnerDashboard(ownerId);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            log.error(" Erreur dashboard owner: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
