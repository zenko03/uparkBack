-- =====================================================================
-- SCRIPT DE TEST DES VIEWS SQL POUR TABLEAU DE BORD UPARK
-- =====================================================================
-- Ce script permet de tester chaque view individuellement
-- avant de créer les views définitives
-- =====================================================================

-- =====================================================================
-- TEST 1 : Vérification des données de base
-- =====================================================================

-- Vérifier les tables principales
SELECT 'Users' AS table_name, COUNT(*) AS count FROM users
UNION ALL
SELECT 'Parking', COUNT(*) FROM parking
UNION ALL
SELECT 'Reservation', COUNT(*) FROM reservation
UNION ALL
SELECT 'Commission_received', COUNT(*) FROM commission_received
UNION ALL
SELECT 'Reservation_status', COUNT(*) FROM reservation_status;

-- =====================================================================
-- TEST 2 : Requête pour VIEW 1 - Statistiques des commissions
-- =====================================================================

-- Test de la requête des commissions
SELECT 
    DATE(cr.payement_date) AS jour,
    DATE_TRUNC('month', cr.payement_date) AS mois,
    DATE_TRUNC('year', cr.payement_date) AS annee,
    SUM(cr.price) AS total_commissions,
    COUNT(*) AS nombre_transactions,
    AVG(cr.price) AS commission_moyenne,
    ct.label AS type_commission,
    ps.label AS statut_paiement
FROM commission_received cr
LEFT JOIN commission_types ct ON cr.Id_Commission_types = ct.Id_Commission_types
LEFT JOIN payment_status ps ON cr.Id_Payment_status = ps.Id_Payment_status
WHERE cr.payement_date IS NOT NULL
GROUP BY 
    DATE(cr.payement_date),
    DATE_TRUNC('month', cr.payement_date),
    DATE_TRUNC('year', cr.payement_date),
    ct.label,
    ps.label
ORDER BY jour DESC
LIMIT 10;

-- =====================================================================
-- TEST 3 : Requête pour VIEW 2 - Statistiques des réservations par statut
-- =====================================================================

-- Test de la requête des réservations par statut
SELECT 
    rs.Id_Reservation_status,
    rs.label AS statut_label,
    rs.value_ AS statut_value,
    COUNT(r.Id_Reservation) AS nombre_reservations,
    SUM(r.total_price) AS montant_total,
    AVG(r.total_price) AS montant_moyen,
    DATE(r.creation_date) AS jour,
    DATE_TRUNC('month', r.creation_date) AS mois,
    ROUND(
        COUNT(r.Id_Reservation) * 100.0 / 
        (SELECT COUNT(*) FROM reservation), 
        2
    ) AS pourcentage_total
FROM reservation_status rs
LEFT JOIN reservation r ON rs.Id_Reservation_status = r.Id_Reservation_status
GROUP BY 
    rs.Id_Reservation_status, 
    rs.label, 
    rs.value_,
    DATE(r.creation_date),
    DATE_TRUNC('month', r.creation_date)
ORDER BY jour DESC, statut_label
LIMIT 20;

-- =====================================================================
-- TEST 4 : Requête pour VIEW 3 - Classement des parkings (JOINTURE CORRIGÉE)
-- =====================================================================

-- Test de la chaîne de jointures étape par étape
-- Étape 1: Vérifier les parking_vehicles
SELECT 'Parking_vehicles' AS etape, pv.Id_Parking, pv.Id_Vehicles, pv.numbers, p.label
FROM parking_vehicles pv
JOIN parking p ON pv.Id_Parking = p.Id_Parking
LIMIT 5;

-- Étape 2: Vérifier les announcements_vehicles
SELECT 'Announcements_vehicles' AS etape, av.Id_Announcements_vehicles, av.Id_Parking_vehicles, av.numbers
FROM announcements_vehicles av
LIMIT 5;

-- Étape 3: Vérifier les reservation_vehicles
SELECT 'Reservation_vehicles' AS etape, rv.Id_Reservation_vehicles, rv.Id_Reservation, rv.Id_Announcements_vehicles
FROM reservation_vehicles rv
LIMIT 5;

-- Test complet de la requête de classement des parkings
SELECT 
    p.Id_Parking,
    p.label AS parking_label,
    p.hourly_rate,
    COUNT(DISTINCT r.Id_Reservation) AS nombre_reservations,
    SUM(COALESCE(r.total_price, 0)) AS chiffre_affaires,
    AVG(COALESCE(r.total_price, 0)) AS panier_moyen,
    AVG(COALESCE(pn.note, 0)) AS note_moyenne,
    COUNT(pn.Id_Parking_note) AS nombre_notes,
    SUM(COALESCE(pv.numbers, 0)) AS capacite_totale,
    u.name AS proprietaire_nom,
    u.first_name AS proprietaire_prenom
FROM parking p
LEFT JOIN parking_vehicles pv ON p.Id_Parking = pv.Id_Parking
LEFT JOIN announcements_vehicles av ON pv.Id_Parking_vehicles = av.Id_Parking_vehicles
LEFT JOIN reservation_vehicles rv ON av.Id_Announcements_vehicles = rv.Id_Announcements_vehicles
LEFT JOIN reservation r ON rv.Id_Reservation = r.Id_Reservation
LEFT JOIN parking_note pn ON p.Id_Parking = pn.Id_Parking
LEFT JOIN users u ON p.Id_Users = u.Id_Users
GROUP BY 
    p.Id_Parking, p.label, p.hourly_rate, 
    u.name, u.first_name
ORDER BY nombre_reservations DESC, p.label;

-- =====================================================================
-- TEST 5 : Requête pour VIEW 4 - Utilisateurs actifs
-- =====================================================================

-- Test de la requête des utilisateurs actifs
SELECT 
    DATE(r.creation_date) AS jour,
    DATE_TRUNC('week', r.creation_date) AS semaine,
    DATE_TRUNC('month', r.creation_date) AS mois,
    COUNT(DISTINCT r.Id_Users) AS utilisateurs_actifs,
    COUNT(DISTINCT CASE WHEN r.creation_date >= CURRENT_DATE - INTERVAL '7 days' THEN r.Id_Users END) AS utilisateurs_actifs_7j,
    COUNT(DISTINCT CASE WHEN r.creation_date >= CURRENT_DATE - INTERVAL '30 days' THEN r.Id_Users END) AS utilisateurs_actifs_30j,
    COUNT(r.Id_Reservation) AS nombre_reservations,
    SUM(r.total_price) AS montant_total_reservations,
    COUNT(DISTINCT CASE 
        WHEN r.creation_date = (
            SELECT MIN(creation_date) 
            FROM reservation r2 
            WHERE r2.Id_Users = r.Id_Users
        ) THEN r.Id_Users 
    END) AS nouveaux_utilisateurs
FROM reservation r
WHERE r.creation_date >= CURRENT_DATE - INTERVAL '90 days'
GROUP BY 
    DATE(r.creation_date),
    DATE_TRUNC('week', r.creation_date),
    DATE_TRUNC('month', r.creation_date)
ORDER BY jour DESC
LIMIT 30;

-- =====================================================================
-- TEST 6 : Requête pour VIEW 5 - Synthèse globale
-- =====================================================================

-- Test de la requête de synthèse
SELECT 
    CURRENT_DATE AS date_jour,
    DATE_TRUNC('month', CURRENT_DATE) AS mois_courant,
    DATE_TRUNC('year', CURRENT_DATE) AS annee_courante,
    (SELECT COALESCE(SUM(price), 0) FROM commission_received 
     WHERE DATE(payement_date) = CURRENT_DATE) AS commissions_jour,
    (SELECT COALESCE(SUM(price), 0) FROM commission_received 
     WHERE DATE_TRUNC('month', payement_date) = DATE_TRUNC('month', CURRENT_DATE)) AS commissions_mois,
    (SELECT COUNT(*) FROM reservation 
     WHERE DATE(creation_date) = CURRENT_DATE) AS reservations_jour,
    (SELECT COUNT(*) FROM reservation 
     WHERE DATE_TRUNC('month', creation_date) = DATE_TRUNC('month', CURRENT_DATE)) AS reservations_mois,
    (SELECT COUNT(DISTINCT Id_Users) FROM reservation 
     WHERE DATE(creation_date) = CURRENT_DATE) AS utilisateurs_actifs_jour,
    (SELECT COUNT(*) FROM parking) AS total_parkings;

-- =====================================================================
-- TEST 7 : Vérification des performances et des données
-- =====================================================================

-- Vérifier les temps d'exécution estimés
EXPLAIN ANALYZE 
SELECT p.label, COUNT(DISTINCT r.Id_Reservation) AS nb_reservations
FROM parking p
LEFT JOIN parking_vehicles pv ON p.Id_Parking = pv.Id_Parking
LEFT JOIN announcements_vehicles av ON pv.Id_Parking_vehicles = av.Id_Parking_vehicles
LEFT JOIN reservation_vehicles rv ON av.Id_Announcements_vehicles = rv.Id_Announcements_vehicles
LEFT JOIN reservation r ON rv.Id_Reservation = r.Id_Reservation
GROUP BY p.Id_Parking, p.label
ORDER BY nb_reservations DESC;

-- =====================================================================
-- INSTRUCTIONS POUR L'EXÉCUTION
-- =====================================================================

/*
1. Exécuter ce script dans PostgreSQL ou via l'application Spring Boot
2. Vérifier que toutes les requêtes retournent des résultats cohérents
3. Corriger les erreurs éventuelles (noms de tables, colonnes, etc.)
4. Une fois validé, créer le fichier final dashboard-views.sql

Points à vérifier :
- Les jointures ne retournent pas d'erreurs
- Les données sont cohérentes
- Les performances sont acceptables
- Tous les cas d'usage du CDC sont couverts
*/