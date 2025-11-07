package com.urban.upark.controllers;

import com.urban.upark.services.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller pour le tableau de bord administrateur
 * Implémente tous les endpoints requis par le CDC pour les statistiques
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    // =====================================================================
    // TABLEAU DE BORD PRINCIPAL
    // CDC : "Tableau de bord - Statistiques globales"
    // =====================================================================

    /**
     * Aperçu complet du tableau de bord
     * GET /api/dashboard/overview
     */
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getDashboardOverview() {
        try {
            log.info("Requête: GET /api/dashboard/overview");
            
            Map<String, Object> overview = dashboardService.getDashboardOverview();
            
            if (overview.isEmpty()) {
                log.warn("Aperçu du tableau de bord vide - vérifier les vues SQL");
                return ResponseEntity.noContent().build();
            }
            
            log.info("Aperçu du tableau de bord récupéré avec succès");
            return ResponseEntity.ok(overview);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de l'aperçu du tableau de bord: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Vérification de la disponibilité des vues du tableau de bord
     * GET /api/dashboard/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> checkDashboardHealth() {
        try {
            log.info("Requête: GET /api/dashboard/health");
            
            boolean isAvailable = dashboardService.checkDashboardViewsAvailability();
            
            Map<String, Object> health = Map.of(
                "status", isAvailable ? "healthy" : "unhealthy",
                "viewsAvailable", isAvailable,
                "timestamp", LocalDate.now()
            );
            
            return ResponseEntity.ok(health);
            
        } catch (Exception e) {
            log.error("Erreur lors du check santé du tableau de bord: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // =====================================================================
    // STATISTIQUES DES COMMISSIONS
    // CDC : "Total des commissions générées (jour / mois / global)"
    // =====================================================================

    /**
     * Statistiques complètes des commissions
     * GET /api/dashboard/commissions?startDate=2024-01-01&endDate=2024-01-31
     */
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

    /**
     * Évolution des commissions sur N jours
     * GET /api/dashboard/commissions/evolution?days=30
     */
    @GetMapping("/commissions/evolution")
    public ResponseEntity<?> getCommissionsEvolution(
            @RequestParam(defaultValue = "30") int days) {
        
        try {
            log.info("Requête: GET /api/dashboard/commissions/evolution avec days={}", days);
            
            if (days <= 0 || days > 365) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "Le nombre de jours doit être entre 1 et 365")
                );
            }
            
            var evolution = dashboardService.getCommissionsEvolution(days);
            
            log.info("Évolution des commissions récupérée: {} entrées", evolution.size());
            return ResponseEntity.ok(evolution);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de l'évolution des commissions: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // =====================================================================
    // STATISTIQUES DES RÉSERVATIONS PAR STATUT
    // CDC : "Nombre de réservations par statut"
    // =====================================================================

    /**
     * Répartition des réservations par statut
     * GET /api/dashboard/reservations/status?startDate=2024-01-01&endDate=2024-01-31
     */
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

    /**
     * Évolution des réservations sur N jours
     * GET /api/dashboard/reservations/evolution?days=30
     */
    @GetMapping("/reservations/evolution")
    public ResponseEntity<?> getReservationsEvolution(
            @RequestParam(defaultValue = "30") int days) {
        
        try {
            log.info("Requête: GET /api/dashboard/reservations/evolution avec days={}", days);
            
            if (days <= 0 || days > 365) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "Le nombre de jours doit être entre 1 et 365")
                );
            }
            
            var evolution = dashboardService.getReservationsEvolution(days);
            
            log.info("Évolution des réservations récupérée: {} entrées", evolution.size());
            return ResponseEntity.ok(evolution);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de l'évolution des réservations: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // =====================================================================
    // CLASSEMENT DES PARKINGS
    // CDC : "Classement des parkings les plus loués"
    // =====================================================================

    /**
     * Top des parkings les plus réservés
     * GET /api/dashboard/parkings/top-reservations?limit=10
     */
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

    /**
     * Top des parkings par chiffre d'affaires
     * GET /api/dashboard/parkings/top-revenue?limit=10
     */
    @GetMapping("/parkings/top-revenue")
    public ResponseEntity<?> getTopParkingsByRevenue(
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            log.info("Requête: GET /api/dashboard/parkings/top-revenue avec limit={}", limit);
            
            if (limit <= 0 || limit > 100) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "La limite doit être entre 1 et 100")
                );
            }
            
            var topParkings = dashboardService.getTopParkingsByRevenue(limit);
            
            log.info("Top parkings par CA récupéré: {} parkings", topParkings.size());
            return ResponseEntity.ok(topParkings);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du top parkings par CA: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Taux de remplissage des parkings
     * GET /api/dashboard/parkings/occupancy
     */
    @GetMapping("/parkings/occupancy")
    public ResponseEntity<?> getParkingOccupancyRates() {
        try {
            log.info("Requête: GET /api/dashboard/parkings/occupancy");
            
            var occupancyRates = dashboardService.getParkingOccupancyRates();
            
            log.info("Taux de remplissage récupérés: {} parkings", occupancyRates.size());
            return ResponseEntity.ok(occupancyRates);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des taux de remplissage: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // =====================================================================
    // UTILISATEURS ACTIFS
    // CDC : "Nombre d'utilisateurs actifs"
    // =====================================================================

    /**
     * Statistiques des utilisateurs actifs
     * GET /api/dashboard/users/active?startDate=2024-01-01&endDate=2024-01-31
     */
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

    // =====================================================================
    // STATISTIQUES COMPARATIVES
    // =====================================================================

    /**
     * Comparaison mensuelle des performances
     * GET /api/dashboard/comparison/monthly?months=6
     */
    @GetMapping("/comparison/monthly")
    public ResponseEntity<?> getMonthlyComparison(
            @RequestParam(defaultValue = "6") int months) {
        
        try {
            log.info("Requête: GET /api/dashboard/comparison/monthly avec months={}", months);
            
            if (months <= 0 || months > 24) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "Le nombre de mois doit être entre 1 et 24")
                );
            }
            
            var comparison = dashboardService.getMonthlyComparison(months);
            
            log.info("Comparaison mensuelle récupérée: {} mois", comparison.size());
            return ResponseEntity.ok(comparison);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de la comparaison mensuelle: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // =====================================================================
    // ENDPOINTS UTILITAIRES
    // =====================================================================

    /**
     * Statistiques rapides pour le widget d'accueil
     * GET /api/dashboard/widgets/quick-stats
     */
    @GetMapping("/widgets/quick-stats")
    public ResponseEntity<Map<String, Object>> getQuickStats() {
        try {
            log.info("Requête: GET /api/dashboard/widgets/quick-stats");
            
            Map<String, Object> overview = dashboardService.getDashboardOverview();
            
            // Extraire uniquement les statistiques rapides
            Map<String, Object> quickStats = Map.of(
                "commissionsJour", overview.getOrDefault("commissions_jour", 0),
                "commissionsMois", overview.getOrDefault("commissions_mois", 0),
                "reservationsJour", overview.getOrDefault("reservations_jour", 0),
                "reservationsMois", overview.getOrDefault("reservations_mois", 0),
                "utilisateursActifsJour", overview.getOrDefault("utilisateurs_actifs_jour", 0),
                "totalParkings", overview.getOrDefault("total_parkings", 0),
                "topParkingMois", overview.getOrDefault("top_parking_mois", "N/A")
            );
            
            log.info("Statistiques rapides récupérées avec succès");
            return ResponseEntity.ok(quickStats);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des statistiques rapides: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Documentation des endpoints du tableau de bord
     * GET /api/dashboard/docs
     */
    @GetMapping("/docs")
    public ResponseEntity<Map<String, Object>> getApiDocumentation() {
        try {
            log.info("Requête: GET /api/dashboard/docs");
            
            Map<String, Object> endpoints = new HashMap<>();
            endpoints.put("overview", "GET /api/dashboard/overview - Aperçu complet du tableau de bord");
            endpoints.put("health", "GET /api/dashboard/health - Vérification de disponibilité");
            endpoints.put("commissions", "GET /api/dashboard/commissions - Statistiques commissions");
            endpoints.put("commissionsEvolution", "GET /api/dashboard/commissions/evolution?days=30 - Évolution commissions");
            endpoints.put("reservationsStatus", "GET /api/dashboard/reservations/status - Réservations par statut");
            endpoints.put("reservationsEvolution", "GET /api/dashboard/reservations/evolution?days=30 - Évolution réservations");
            endpoints.put("topParkingsReservations", "GET /api/dashboard/parkings/top-reservations?limit=10 - Top parkings par réservations");
            endpoints.put("topParkingsRevenue", "GET /api/dashboard/parkings/top-revenue?limit=10 - Top parkings par CA");
            endpoints.put("parkingOccupancy", "GET /api/dashboard/parkings/occupancy - Taux de remplissage");
            endpoints.put("activeUsers", "GET /api/dashboard/users/active - Utilisateurs actifs");
            endpoints.put("monthlyComparison", "GET /api/dashboard/comparison/monthly?months=6 - Comparaison mensuelle");
            endpoints.put("quickStats", "GET /api/dashboard/widgets/quick-stats - Statistiques rapides");

            Map<String, Object> cdcRequirements = new HashMap<>();
            cdcRequirements.put("commissions", "Total des commissions générées (jour / mois / global)");
            cdcRequirements.put("reservationsStatus", "Nombre de réservations par statut");
            cdcRequirements.put("parkingRanking", "Classement des parkings les plus loués");
            cdcRequirements.put("activeUsers", "Nombre d'utilisateurs actifs");

            Map<String, Object> docs = new HashMap<>();
            docs.put("title", "API Tableau de Bord Upark");
            docs.put("version", "1.0.0");
            docs.put("description", "Endpoints pour les statistiques du back-office selon le CDC");
            docs.put("endpoints", endpoints);
            docs.put("cdcRequirements", cdcRequirements);
            
            return ResponseEntity.ok(docs);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de la documentation: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}