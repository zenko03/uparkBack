package com.urban.upark.services;

import com.urban.upark.repositories.DashboardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Service pour la gestion des statistiques du tableau de bord
 * Implémente la logique métier pour toutes les fonctionnalités du CDC
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final DashboardRepository dashboardRepository;

    // STATISTIQUES GLOBALES 
    public Map<String, Object> getDashboardOverview() {
        try {
            log.info("Récupération de l'aperçu du tableau de bord");
            
            Map<String, Object> overview = dashboardRepository.getDashboardOverview();
            
            // Ajouter des informations supplémentaires
            if (overview != null) {
                overview.put("timestamp", LocalDate.now());
                overview.put("statut", "actif");
            }
            
            log.info("Aperçu du tableau de bord récupéré avec succès");
            return overview != null ? overview : new HashMap<>();
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de l'aperçu du tableau de bord: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    // STATISTIQUES DES COMMISSIONS
    public Map<String, Object> getCommissionStatistics(LocalDate startDate, LocalDate endDate) {
        try {
            log.info("Récupération des statistiques commissions du {} au {}", startDate, endDate);
            
            Map<String, Object> stats = new HashMap<>();
            
            // Commissions par période
            List<Map<String, Object>> commissionsByPeriod = dashboardRepository.getCommissionsByPeriod(startDate, endDate);
            stats.put("commissionsParPeriode", commissionsByPeriod);
            
            // Totaux
            BigDecimal totalMois = dashboardRepository.getTotalCommissionsCurrentMonth();
            BigDecimal totalJour = dashboardRepository.getTotalCommissionsToday();
            
            stats.put("totalMoisCourant", totalMois != null ? totalMois : BigDecimal.ZERO);
            stats.put("totalJour", totalJour != null ? totalJour : BigDecimal.ZERO);
            
            // Calculs supplémentaires
            BigDecimal moyenneJournaliere = totalJour != null && totalJour.compareTo(BigDecimal.ZERO) > 0 ? 
                totalJour : BigDecimal.ZERO;
            stats.put("moyenneJournaliere", moyenneJournaliere);
            
            log.info("Statistiques commissions récupérées avec succès");
            return stats;
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des statistiques commissions: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    // STATISTIQUES DES RÉSERVATIONS PAR STATUT
    public Map<String, Object> getReservationsByStatus(LocalDate startDate, LocalDate endDate) {
        try {
            log.info("Récupération des réservations par statut du {} au {}", startDate, endDate);
            
            Map<String, Object> result = new HashMap<>();
            
            // Réservations par statut sur la période
            List<Map<String, Object>> reservationsByStatus = dashboardRepository.getReservationsByStatus(startDate, endDate);
            result.put("reservationsParStatut", reservationsByStatus);
            
            // Distribution en pourcentage (mois courant)
            List<Map<String, Object>> distribution = dashboardRepository.getReservationsStatusDistribution();
            result.put("distributionPourcentage", distribution);
            
            // Calcul du total
            int totalReservations = reservationsByStatus.stream()
                .mapToInt(item -> {
                    Object total = item.get("total_reservations");
                    return total instanceof Number ? ((Number) total).intValue() : 0;
                })
                .sum();
            result.put("totalReservations", totalReservations);
            
            log.info("Réservations par statut récupérées: {} statuts, {} réservations totales", 
                    reservationsByStatus.size(), totalReservations);
            
            return result;
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des réservations par statut: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    

    // CLASSEMENT DES PARKINGS
    public List<Map<String, Object>> getTopParkingsByReservations(int limit) {
        try {
            log.info("Récupération du top {} des parkings par réservations", limit);
            
            List<Map<String, Object>> topParkings = dashboardRepository.getTopParkingsByReservations(limit);
            
            // Ajouter des informations formatées
            topParkings.forEach(parking -> {
                // Formatter le chiffre d'affaires
                Object caObj = parking.get("chiffre_affaires");
                if (caObj instanceof Number) {
                    BigDecimal ca = new BigDecimal(caObj.toString());
                    parking.put("chiffreAffairesFormate", formatCurrency(ca));
                }
                
                // Formatter la note
                Object noteObj = parking.get("note_moyenne");
                if (noteObj instanceof Number) {
                    double note = ((Number) noteObj).doubleValue();
                    parking.put("noteFormatee", String.format("%.1f/5", note));
                }
                
                // Ajouter le rang
                int index = topParkings.indexOf(parking);
                parking.put("rang", index + 1);
            });
            
            log.info("Top parkings par réservations récupéré: {} parkings", topParkings.size());
            return topParkings;
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du top parkings par réservations: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Top des parkings par chiffre d'affaires
     */
    public List<Map<String, Object>> getTopParkingsByRevenue(int limit) {
        try {
            log.info("Récupération du top {} des parkings par chiffre d'affaires", limit);
            
            List<Map<String, Object>> topParkings = dashboardRepository.getTopParkingsByRevenue(limit);
            
            // Formatter les informations
            topParkings.forEach(parking -> {
                Object caObj = parking.get("chiffre_affaires");
                if (caObj instanceof Number) {
                    BigDecimal ca = new BigDecimal(caObj.toString());
                    parking.put("chiffreAffairesFormate", formatCurrency(ca));
                }
                
                int index = topParkings.indexOf(parking);
                parking.put("rang", index + 1);
            });
            
            log.info("Top parkings par CA récupéré: {} parkings", topParkings.size());
            return topParkings;
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du top parkings par CA: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Taux de remplissage des parkings
     */
    public List<Map<String, Object>> getParkingOccupancyRates() {
        try {
            log.info("Récupération des taux de remplissage des parkings");
            
            List<Map<String, Object>> occupancyRates = dashboardRepository.getParkingOccupancyRates();
            
            // Ajouter des indicateurs visuels
            occupancyRates.forEach(parking -> {
                Object tauxObj = parking.get("taux_remplissage");
                if (tauxObj instanceof Number) {
                    double taux = ((Number) tauxObj).doubleValue();
                    
                    // Indicateur de niveau
                    if (taux >= 80) {
                        parking.put("niveau", "élevé");
                        parking.put("couleur", "#ff4444"); // Rouge
                    } else if (taux >= 60) {
                        parking.put("niveau", "moyen");
                        parking.put("couleur", "#ffaa00"); // Orange
                    } else {
                        parking.put("niveau", "faible");
                        parking.put("couleur", "#00C851"); // Vert
                    }
                    
                    parking.put("tauxFormate", String.format("%.1f%%", taux));
                }
            });
            
            log.info("Taux de remplissage récupérés: {} parkings", occupancyRates.size());
            return occupancyRates;
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des taux de remplissage: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    // =====================================================================
    // UTILISATEURS ACTIFS
    // CDC : "Nombre d'utilisateurs actifs"
    // =====================================================================

    /**
     * Statistiques complètes des utilisateurs actifs
     */
    public Map<String, Object> getActiveUsersStatistics(LocalDate startDate, LocalDate endDate) {
        try {
            log.info("Récupération des statistiques utilisateurs actifs du {} au {}", startDate, endDate);
            
            Map<String, Object> stats = new HashMap<>();
            
            // Utilisateurs par période
            List<Map<String, Object>> activeUsersByPeriod = dashboardRepository.getActiveUsersByPeriod(startDate, endDate);
            stats.put("utilisateursParPeriode", activeUsersByPeriod);
            
            // Statistiques rapides
            Long activeToday = dashboardRepository.getActiveUsersToday();
            Long active7Days = dashboardRepository.getActiveUsersLast7Days();
            Long active30Days = dashboardRepository.getActiveUsersLast30Days();
            
            stats.put("actifsAujourdhui", activeToday != null ? activeToday : 0);
            stats.put("actifs7DerniersJours", active7Days != null ? active7Days : 0);
            stats.put("actifs30DerniersJours", active30Days != null ? active30Days : 0);
            
            // Calculs de tendance
            if (active7Days != null && active30Days != null && active30Days > 0) {
                double tendance = ((double) active7Days / active30Days) * 100;
                stats.put("tendanceHebdomadaire", String.format("%.1f%%", tendance));
            }
            
            log.info("Statistiques utilisateurs actifs récupérées: {} périodes analysées", activeUsersByPeriod.size());
            return stats;
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des statistiques utilisateurs actifs: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    // =====================================================================
    // STATISTIQUES COMPARATIVES
    // =====================================================================

    /**
     * Comparaison mensuelle des performances
     */
    public List<Map<String, Object>> getMonthlyComparison(int months) {
        try {
            log.info("Récupération de la comparaison mensuelle sur {} mois", months);
            
            List<Map<String, Object>> comparison = dashboardRepository.getMonthlyComparison(months);
            
            // Formatter les mois et ajouter des calculs
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH);
            comparison.forEach(item -> {
                // Formatter le mois
                if (item.containsKey("mois")) {
                    Object moisObj = item.get("mois");
                    if (moisObj instanceof java.sql.Timestamp) {
                        java.sql.Timestamp timestamp = (java.sql.Timestamp) moisObj;
                        item.put("moisFormate", timestamp.toLocalDateTime().format(formatter));
                    }
                }
                
                // Formatter les montants
                Object commissionsObj = item.get("commissions_mois");
                if (commissionsObj instanceof Number) {
                    BigDecimal commissions = new BigDecimal(commissionsObj.toString());
                    item.put("commissionsFormatees", formatCurrency(commissions));
                }
            });
            
            log.info("Comparaison mensuelle récupérée: {} mois", comparison.size());
            return comparison;
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de la comparaison mensuelle: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    // =====================================================================
    // UTILITAIRES
    // =====================================================================

    /**
     * Formate un montant en devise
     */
    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0,00 €";
        return String.format("%,.2f €", amount);
    }

    /**
     * Vérifie la disponibilité des vues du tableau de bord
     */
    public boolean checkDashboardViewsAvailability() {
        try {
            Map<String, Object> overview = dashboardRepository.getDashboardOverview();
            boolean available = overview != null && !overview.isEmpty();
            
            if (available) {
                log.info("Vues du tableau de bord disponibles");
            } else {
                log.warn("Vues du tableau de bord non disponibles - vérifier l'installation");
            }
            
            return available;
            
        } catch (Exception e) {
            log.error("Erreur lors de la vérification des vues du tableau de bord: {}", e.getMessage());
            return false;
        }
    }
}