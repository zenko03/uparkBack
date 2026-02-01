package com.urban.upark.controllers;

import com.urban.upark.dto.DashboardDTO;
import com.urban.upark.services.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
// @PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/{ownerId}")
    public ResponseEntity<DashboardDTO> getDashboard(@PathVariable Integer ownerId) {
        DashboardDTO dashboard = dashboardService.getOwnerDashboard(ownerId);
        return ResponseEntity.ok(dashboard);
    }

    // STATISTIQUES DES COMMISSIONS
  
    @GetMapping("/commissions")
    public ResponseEntity<Map<String, Object>> getCommissionStatistics(
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        try {
            log.info("Requête: GET /api/dashboard/commissions avec startDate={}, endDate={}", startDate, endDate);
            
            // Valeurs par défaut si non spécifiées
            if (startDate == null) {
                startDate = LocalDate.now().minusMonths(1);
            }
            if (endDate == null) {
                endDate = LocalDate.now();
            }
            
            Map<String, Object> statistics = dashboardService.getCommissionStatistics(startDate, endDate);
            
            log.info("Statistiques commissions récupérées pour la période {} à {}", startDate, endDate);
            return ResponseEntity.ok(statistics);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des statistiques commissions: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // STATISTIQUES DES RÉSERVATIONS PAR STATUT
    
    @GetMapping("/reservations/status")
    public ResponseEntity<Map<String, Object>> getReservationsByStatus(
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        try {
            log.info("Requête: GET /api/dashboard/reservations/status avec startDate={}, endDate={}", startDate, endDate);
            
            // Valeurs par défaut
            if (startDate == null) {
                startDate = LocalDate.now().minusMonths(1);
            }
            if (endDate == null) {
                endDate = LocalDate.now();
            }
            
            Map<String, Object> reservationsByStatus = dashboardService.getReservationsByStatus(startDate, endDate);
            
            log.info("Réservations par statut récupérées pour la période {} à {}", startDate, endDate);
            return ResponseEntity.ok(reservationsByStatus);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des réservations par statut: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // CLASSEMENT DES PARKINGS
    
    @GetMapping("/parkings/top-reservations")
    public ResponseEntity<?> getTopParkingsByReservations(
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            log.info("Requête: GET /api/dashboard/parkings/top-reservations avec limit={}", limit);
            
            if (limit <= 0 || limit > 100) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "La limite doit être entre 1 et 100")
                );
            }
            
            var topParkings = dashboardService.getTopParkingsByReservations(limit);
            
            log.info("Top parkings par réservations récupéré: {} parkings", topParkings.size());
            return ResponseEntity.ok(topParkings);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du top parkings par réservations: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // UTILISATEURS ACTIFS
   
    @GetMapping("/users/active")
    public ResponseEntity<Map<String, Object>> getActiveUsersStatistics(
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        try {
            log.info("Requête: GET /api/dashboard/users/active avec startDate={}, endDate={}", startDate, endDate);
            
            // Valeurs par défaut
            if (startDate == null) {
                startDate = LocalDate.now().minusMonths(1);
            }
            if (endDate == null) {
                endDate = LocalDate.now();
            }
            
            Map<String, Object> statistics = dashboardService.getActiveUsersStatistics(startDate, endDate);
            
            log.info("Statistiques utilisateurs actifs récupérées pour la période {} à {}", startDate, endDate);
            return ResponseEntity.ok(statistics);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des statistiques utilisateurs actifs: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}