package com.urban.upark.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Repository pour accéder aux vues du tableau de bord
 * Utilise JdbcTemplate pour éviter les problèmes avec JPA
 */
@Repository
public class DashboardRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DashboardRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // =====================================================================
    // STATISTIQUES DES COMMISSIONS
    // =====================================================================

    /**
     * Statistiques des commissions par période
     */
    public List<Map<String, Object>> getCommissionsByPeriod(LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT * FROM v_dashboard_commissions 
            WHERE jour BETWEEN ? AND ? 
            ORDER BY jour DESC
            """;
        return jdbcTemplate.queryForList(sql, startDate, endDate);
    }

    /**
     * Total des commissions du mois courant
     */
    public BigDecimal getTotalCommissionsCurrentMonth() {
        String sql = """
            SELECT COALESCE(SUM(total_commissions), 0) as total_mois
            FROM v_dashboard_commissions 
            WHERE mois = DATE_TRUNC('month', CURRENT_DATE)
            """;
        return jdbcTemplate.queryForObject(sql, BigDecimal.class);
    }

    /**
     * Total des commissions du jour
     */
    public BigDecimal getTotalCommissionsToday() {
        String sql = """
            SELECT COALESCE(SUM(total_commissions), 0) as total_jour
            FROM v_dashboard_commissions 
            WHERE jour = CURRENT_DATE
            """;
        return jdbcTemplate.queryForObject(sql, BigDecimal.class);
    }

    // =====================================================================
    // STATISTIQUES DES RÉSERVATIONS PAR STATUT
    // =====================================================================

    /**
     * Nombre de réservations par statut pour une période
     */
    public List<Map<String, Object>> getReservationsByStatus(LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT statut_label, SUM(nombre_reservations) as total_reservations,
                   SUM(montant_total) as montant_total
            FROM v_dashboard_reservations_stats 
            WHERE jour BETWEEN ? AND ? 
            GROUP BY statut_label
            ORDER BY total_reservations DESC
            """;
        return jdbcTemplate.queryForList(sql, startDate, endDate);
    }

    /**
     * Répartition des réservations par statut (mois courant)
     */
    public List<Map<String, Object>> getReservationsStatusDistribution() {
        String sql = """
            SELECT statut_label, AVG(pourcentage_total) as pourcentage_moyen
            FROM v_dashboard_reservations_stats 
            WHERE mois = DATE_TRUNC('month', CURRENT_DATE)
            GROUP BY statut_label
            ORDER BY pourcentage_moyen DESC
            """;
        return jdbcTemplate.queryForList(sql);
    }

    // =====================================================================
    // CLASSEMENT DES PARKINGS
    // =====================================================================

    /**
     * Top N des parkings les plus réservés
     */
    public List<Map<String, Object>> getTopParkingsByReservations(int limit) {
        String sql = """
            SELECT * FROM v_dashboard_parking_ranking 
            WHERE nombre_reservations > 0
            ORDER BY nombre_reservations DESC
            LIMIT ?
            """;
        return jdbcTemplate.queryForList(sql, limit);
    }

    /**
     * Top N des parkings par chiffre d'affaires
     */
    public List<Map<String, Object>> getTopParkingsByRevenue(int limit) {
        String sql = """
            SELECT * FROM v_dashboard_parking_ranking 
            WHERE chiffre_affaires > 0
            ORDER BY chiffre_affaires DESC
            LIMIT ?
            """;
        return jdbcTemplate.queryForList(sql, limit);
    }

    /**
     * Statistiques complètes d'un parking spécifique
     */
    public Map<String, Object> getParkingStatistics(Long parkingId) {
        String sql = """
            SELECT * FROM v_dashboard_parking_ranking 
            WHERE Id_Parking = ?
            """;
        try {
            return jdbcTemplate.queryForMap(sql, parkingId);
        } catch (Exception e) {
            return null;
        }
    }

    // =====================================================================
    // UTILISATEURS ACTIFS
    // =====================================================================

    /**
     * Statistiques des utilisateurs actifs sur une période
     */
    public List<Map<String, Object>> getActiveUsersByPeriod(LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT * FROM v_dashboard_active_users 
            WHERE jour BETWEEN ? AND ? 
            ORDER BY jour DESC
            """;
        return jdbcTemplate.queryForList(sql, startDate, endDate);
    }

    /**
     * Nombre d'utilisateurs actifs aujourd'hui
     */
    public Long getActiveUsersToday() {
        String sql = """
            SELECT COUNT(DISTINCT Id_Users) as utilisateurs_actifs_jour
            FROM reservation 
            WHERE DATE(creation_date) = CURRENT_DATE
            """;
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    /**
     * Nombre d'utilisateurs actifs les 7 derniers jours
     */
    public Long getActiveUsersLast7Days() {
        String sql = """
            SELECT COUNT(DISTINCT Id_Users) as utilisateurs_actifs_semaine
            FROM reservation 
            WHERE creation_date >= CURRENT_DATE - INTERVAL '7 days'
            """;
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    /**
     * Nombre d'utilisateurs actifs les 30 derniers jours
     */
    public Long getActiveUsersLast30Days() {
        String sql = """
            SELECT COUNT(DISTINCT Id_Users) as utilisateurs_actifs_mois
            FROM reservation 
            WHERE creation_date >= CURRENT_DATE - INTERVAL '30 days'
            """;
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    // =====================================================================
    // SYNTHÈSE GLOBALE
    // =====================================================================

    /**
     * Aperçu rapide du tableau de bord
     */
    public Map<String, Object> getDashboardOverview() {
        String sql = "SELECT * FROM v_dashboard_synthese";
        try {
            return jdbcTemplate.queryForMap(sql);
        } catch (Exception e) {
            return null;
        }
    }

    // =====================================================================
    // STATISTIQUES COMPLÉMENTAIRES
    // =====================================================================

    /**
     * Évolution des réservations sur N derniers jours
     */
    public List<Map<String, Object>> getReservationsEvolution(int days) {
        String sql = """
            SELECT jour, SUM(nombre_reservations) as reservations_jour,
                   SUM(montant_total) as ca_jour
            FROM v_dashboard_reservations_stats 
            WHERE jour >= CURRENT_DATE - INTERVAL '? days'
            GROUP BY jour
            ORDER BY jour DESC
            """;
        return jdbcTemplate.queryForList(sql, days);
    }

    /**
     * Évolution des commissions sur N derniers jours
     */
    public List<Map<String, Object>> getCommissionsEvolution(int days) {
        String sql = """
            SELECT jour, SUM(total_commissions) as commissions_jour,
                   COUNT(*) as transactions_jour
            FROM v_dashboard_commissions 
            WHERE jour >= CURRENT_DATE - INTERVAL '? days'
            GROUP BY jour
            ORDER BY jour DESC
            """;
        return jdbcTemplate.queryForList(sql, days);
    }

    /**
     * Taux de remplissage des parkings
     */
    public List<Map<String, Object>> getParkingOccupancyRates() {
        String sql = """
            SELECT parking_label, capacite_totale,
                   nombre_reservations as reservations_totales,
                   ROUND((nombre_reservations::float / NULLIF(capacite_totale, 0)) * 100, 2) as taux_remplissage
            FROM v_dashboard_parking_ranking 
            WHERE capacite_totale > 0
            ORDER BY taux_remplissage DESC
            """;
        return jdbcTemplate.queryForList(sql);
    }

    /**
     * Statistiques mensuelles comparatives
     */
    public List<Map<String, Object>> getMonthlyComparison(int months) {
        String sql = """
            SELECT mois, 
                   SUM(total_commissions) as commissions_mois,
                   COUNT(*) as transactions_mois,
                   AVG(commission_moyenne) as commission_moyenne_mois
            FROM v_dashboard_commissions 
            WHERE mois >= DATE_TRUNC('month', CURRENT_DATE - INTERVAL '? months')
            GROUP BY mois
            ORDER BY mois DESC
            """;
        return jdbcTemplate.queryForList(sql, months);
    }
}