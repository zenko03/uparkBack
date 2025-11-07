package com.urban.upark.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Repository pour accéder aux vues du tableau de bord
 * Toutes les requêtes utilisent les vues SQL créées dans dashboard-views.sql
 */
@Repository
public interface DashboardRepository extends JpaRepository<Object, Long> {

    // =====================================================================
    // STATISTIQUES DES COMMISSIONS
    // =====================================================================

    /**
     * Statistiques des commissions par période
     * @param startDate Date de début
     * @param endDate Date de fin
     * @return Liste des statistiques commissions
     */
    @Query(value = """
        SELECT * FROM v_dashboard_commissions 
        WHERE jour BETWEEN :startDate AND :endDate 
        ORDER BY jour DESC
        """, nativeQuery = true)
    List<Map<String, Object>> getCommissionsByPeriod(@Param("startDate") LocalDate startDate, 
                                                    @Param("endDate") LocalDate endDate);

    /**
     * Total des commissions du mois courant
     * @return Montant total des commissions du mois
     */
    @Query(value = """
        SELECT COALESCE(SUM(total_commissions), 0) as total_mois
        FROM v_dashboard_commissions 
        WHERE mois = DATE_TRUNC('month', CURRENT_DATE)
        """, nativeQuery = true)
    BigDecimal getTotalCommissionsCurrentMonth();

    /**
     * Total des commissions du jour
     * @return Montant total des commissions du jour
     */
    @Query(value = """
        SELECT COALESCE(SUM(total_commissions), 0) as total_jour
        FROM v_dashboard_commissions 
        WHERE jour = CURRENT_DATE
        """, nativeQuery = true)
    BigDecimal getTotalCommissionsToday();

    // =====================================================================
    // STATISTIQUES DES RÉSERVATIONS PAR STATUT
    // =====================================================================

    /**
     * Nombre de réservations par statut pour une période
     * @param startDate Date de début
     * @param endDate Date de fin
     * @return Liste des réservations par statut
     */
    @Query(value = """
        SELECT statut_label, SUM(nombre_reservations) as total_reservations,
               SUM(montant_total) as montant_total
        FROM v_dashboard_reservations_stats 
        WHERE jour BETWEEN :startDate AND :endDate 
        GROUP BY statut_label
        ORDER BY total_reservations DESC
        """, nativeQuery = true)
    List<Map<String, Object>> getReservationsByStatus(@Param("startDate") LocalDate startDate, 
                                                     @Param("endDate") LocalDate endDate);

    /**
     * Répartition des réservations par statut (mois courant)
     * @return Répartition en pourcentage
     */
    @Query(value = """
        SELECT statut_label, AVG(pourcentage_total) as pourcentage_moyen
        FROM v_dashboard_reservations_stats 
        WHERE mois = DATE_TRUNC('month', CURRENT_DATE)
        GROUP BY statut_label
        ORDER BY pourcentage_moyen DESC
        """, nativeQuery = true)
    List<Map<String, Object>> getReservationsStatusDistribution();

    // =====================================================================
    // CLASSEMENT DES PARKINGS
    // =====================================================================

    /**
     * Top N des parkings les plus réservés
     * @param limit Nombre de parkings à retourner
     * @return Liste des parkings classés
     */
    @Query(value = """
        SELECT * FROM v_dashboard_parking_ranking 
        WHERE nombre_reservations > 0
        ORDER BY nombre_reservations DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Map<String, Object>> getTopParkingsByReservations(@Param("limit") int limit);

    /**
     * Top N des parkings par chiffre d'affaires
     * @param limit Nombre de parkings à retourner
     * @return Liste des parkings classés par CA
     */
    @Query(value = """
        SELECT * FROM v_dashboard_parking_ranking 
        WHERE chiffre_affaires > 0
        ORDER BY chiffre_affaires DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Map<String, Object>> getTopParkingsByRevenue(@Param("limit") int limit);

    /**
     * Statistiques complètes d'un parking spécifique
     * @param parkingId ID du parking
     * @return Statistiques détaillées du parking
     */
    @Query(value = """
        SELECT * FROM v_dashboard_parking_ranking 
        WHERE Id_Parking = :parkingId
        """, nativeQuery = true)
    Map<String, Object> getParkingStatistics(@Param("parkingId") Long parkingId);

    // =====================================================================
    // UTILISATEURS ACTIFS
    // =====================================================================

    /**
     * Statistiques des utilisateurs actifs sur une période
     * @param startDate Date de début
     * @param endDate Date de fin
     * @return Liste des statistiques utilisateurs
     */
    @Query(value = """
        SELECT * FROM v_dashboard_active_users 
        WHERE jour BETWEEN :startDate AND :endDate 
        ORDER BY jour DESC
        """, nativeQuery = true)
    List<Map<String, Object>> getActiveUsersByPeriod(@Param("startDate") LocalDate startDate, 
                                                    @Param("endDate") LocalDate endDate);

    /**
     * Nombre d'utilisateurs actifs aujourd'hui
     * @return Nombre d'utilisateurs actifs du jour
     */
    @Query(value = """
        SELECT COUNT(DISTINCT Id_Users) as utilisateurs_actifs_jour
        FROM reservation 
        WHERE DATE(creation_date) = CURRENT_DATE
        """, nativeQuery = true)
    Long getActiveUsersToday();

    /**
     * Nombre d'utilisateurs actifs les 7 derniers jours
     * @return Nombre d'utilisateurs actifs semaine
     */
    @Query(value = """
        SELECT COUNT(DISTINCT Id_Users) as utilisateurs_actifs_semaine
        FROM reservation 
        WHERE creation_date >= CURRENT_DATE - INTERVAL '7 days'
        """, nativeQuery = true)
    Long getActiveUsersLast7Days();

    /**
     * Nombre d'utilisateurs actifs les 30 derniers jours
     * @return Nombre d'utilisateurs actifs mois
     */
    @Query(value = """
        SELECT COUNT(DISTINCT Id_Users) as utilisateurs_actifs_mois
        FROM reservation 
        WHERE creation_date >= CURRENT_DATE - INTERVAL '30 days'
        """, nativeQuery = true)
    Long getActiveUsersLast30Days();

    // =====================================================================
    // SYNTHÈSE GLOBALE
    // =====================================================================

    /**
     * Aperçu rapide du tableau de bord
     * @return Statistiques globales synthétisées
     */
    @Query(value = "SELECT * FROM v_dashboard_synthese", nativeQuery = true)
    Map<String, Object> getDashboardOverview();

    // =====================================================================
    // STATISTIQUES COMPLÉMENTAIRES
    // =====================================================================

    /**
     * Évolution des réservations sur N derniers jours
     * @param days Nombre de jours à considérer
     * @return Évolution quotidienne
     */
    @Query(value = """
        SELECT jour, SUM(nombre_reservations) as reservations_jour,
               SUM(montant_total) as ca_jour
        FROM v_dashboard_reservations_stats 
        WHERE jour >= CURRENT_DATE - INTERVAL ':days days'
        GROUP BY jour
        ORDER BY jour DESC
        """, nativeQuery = true)
    List<Map<String, Object>> getReservationsEvolution(@Param("days") int days);

    /**
     * Évolution des commissions sur N derniers jours
     * @param days Nombre de jours à considérer
     * @return Évolution quotidienne des commissions
     */
    @Query(value = """
        SELECT jour, SUM(total_commissions) as commissions_jour,
               COUNT(*) as transactions_jour
        FROM v_dashboard_commissions 
        WHERE jour >= CURRENT_DATE - INTERVAL ':days days'
        GROUP BY jour
        ORDER BY jour DESC
        """, nativeQuery = true)
    List<Map<String, Object>> getCommissionsEvolution(@Param("days") int days);

    /**
     * Taux de remplissage des parkings
     * @return Statistiques de remplissage par parking
     */
    @Query(value = """
        SELECT parking_label, capacite_totale,
               nombre_reservations as reservations_totales,
               ROUND((nombre_reservations::float / NULLIF(capacite_totale, 0)) * 100, 2) as taux_remplissage
        FROM v_dashboard_parking_ranking 
        WHERE capacite_totale > 0
        ORDER BY taux_remplissage DESC
        """, nativeQuery = true)
    List<Map<String, Object>> getParkingOccupancyRates();

    /**
     * Statistiques mensuelles comparatives
     * @param months Nombre de mois à comparer
     * @return Comparaison mois par mois
     */
    @Query(value = """
        SELECT mois, 
               SUM(total_commissions) as commissions_mois,
               COUNT(*) as transactions_mois,
               AVG(commission_moyenne) as commission_moyenne_mois
        FROM v_dashboard_commissions 
        WHERE mois >= DATE_TRUNC('month', CURRENT_DATE - INTERVAL ':months months')
        GROUP BY mois
        ORDER BY mois DESC
        """, nativeQuery = true)
    List<Map<String, Object>> getMonthlyComparison(@Param("months") int months);
}