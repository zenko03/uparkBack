-- =====================================================================
-- VIEW 1 : STATISTIQUES DES COMMISSIONS
-- =====================================================================

CREATE OR REPLACE VIEW v_dashboard_commissions AS
SELECT 
    -- Période jour
    DATE(cr.payement_date) AS jour,
    -- Période mois
    DATE_TRUNC('month', cr.payement_date) AS mois,
    -- Période année
    DATE_TRUNC('year', cr.payement_date) AS annee,
    
    -- Statistiques commissions
    SUM(cr.price) AS total_commissions,
    COUNT(*) AS nombre_transactions,
    AVG(cr.price) AS commission_moyenne,
    
    -- Informations complémentaires
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
    ps.label;

-- =====================================================================
-- VIEW 2 : STATISTIQUES DES RÉSERVATIONS PAR STATUT
-- =====================================================================

CREATE OR REPLACE VIEW v_dashboard_reservations_stats AS
SELECT 
    rs.Id_Reservation_status,
    rs.label AS statut_label,
    rs.value_ AS statut_value,
    
    -- Compteurs
    COUNT(r.Id_Reservation) AS nombre_reservations,
    SUM(r.total_price) AS montant_total,
    AVG(r.total_price) AS montant_moyen,
    
    -- Période
    DATE(r.creation_date) AS jour,
    DATE_TRUNC('month', r.creation_date) AS mois,
    
    -- Taux (pourcentage du total)
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
    DATE_TRUNC('month', r.creation_date);

-- =====================================================================
-- VIEW 3 : CLASSEMENT DES PARKINGS LES PLUS LOUÉS
-- =====================================================================

CREATE OR REPLACE VIEW v_dashboard_parking_ranking AS
SELECT 
    -- Informations parking
    p.Id_Parking,
    p.label AS parking_label,
    p.hourly_rate,
    p.description,
    
    -- Statistiques réservations
    COUNT(DISTINCT r.Id_Reservation) AS nombre_reservations,
    SUM(COALESCE(r.total_price, 0)) AS chiffre_affaires,
    AVG(COALESCE(r.total_price, 0)) AS panier_moyen,
    
    -- Notes et satisfaction (✅ CORRIGÉ: user_note au lieu de parking_note)
    AVG(COALESCE(un.note, 0)) AS note_moyenne,
    COUNT(un.id) AS nombre_notes,
    
    -- Détail des critères de notation
    AVG(CASE WHEN un.cleanliness THEN 1 ELSE 0 END) * 100 AS taux_proprete,
    AVG(CASE WHEN un.precision THEN 1 ELSE 0 END) * 100 AS taux_precision,
    AVG(CASE WHEN un.communication THEN 1 ELSE 0 END) * 100 AS taux_communication,
    AVG(CASE WHEN un.security THEN 1 ELSE 0 END) * 100 AS taux_securite,
    
    -- Capacité et disponibilité
    SUM(COALESCE(pv.numbers, 0)) AS capacite_totale,
    
    -- Classement
    ROW_NUMBER() OVER (ORDER BY COUNT(DISTINCT r.Id_Reservation) DESC) AS classement_reservations,
    ROW_NUMBER() OVER (ORDER BY SUM(COALESCE(r.total_price, 0)) DESC) AS classement_ca,
    ROW_NUMBER() OVER (ORDER BY AVG(COALESCE(un.note, 0)) DESC) AS classement_notes,
    
    -- Propriétaire
    u.name AS proprietaire_nom,
    u.first_name AS proprietaire_prenom

FROM parking p
-- Jointure réservations (via chaîne complète)
LEFT JOIN parking_vehicles pv ON p.Id_Parking = pv.Id_Parking
LEFT JOIN announcements_vehicles av ON pv.Id_Parking_vehicles = av.Id_Parking_vehicles
LEFT JOIN reservation_vehicles rv ON av.Id_Announcements_vehicles = rv.Id_Announcements_vehicles
LEFT JOIN reservation r ON rv.Id_Reservation = r.Id_Reservation
-- user_note au lieu de parking_note
LEFT JOIN user_note un ON p.Id_Parking = un.id_parking
-- Propriétaire
LEFT JOIN users u ON p.Id_Users = u.Id_Users
GROUP BY 
    p.Id_Parking, p.label, p.hourly_rate, p.description, 
    u.name, u.first_name
ORDER BY nombre_reservations DESC;
-- =====================================================================
-- VIEW 4 : UTILISATEURS ACTIFS
-- =====================================================================

CREATE OR REPLACE VIEW v_dashboard_active_users AS
SELECT 
    -- Période
    DATE(r.creation_date) AS jour,
    DATE_TRUNC('week', r.creation_date) AS semaine,
    DATE_TRUNC('month', r.creation_date) AS mois,
    
    -- Statistiques utilisateurs
    COUNT(DISTINCT r.Id_Users) AS utilisateurs_actifs,
    COUNT(DISTINCT CASE WHEN r.creation_date >= CURRENT_DATE - INTERVAL '7 days' THEN r.Id_Users END) AS utilisateurs_actifs_7j,
    COUNT(DISTINCT CASE WHEN r.creation_date >= CURRENT_DATE - INTERVAL '30 days' THEN r.Id_Users END) AS utilisateurs_actifs_30j,
    
    -- Statistiques réservations
    COUNT(r.Id_Reservation) AS nombre_reservations,
    SUM(r.total_price) AS montant_total_reservations,
    
    -- Nouveaux utilisateurs (ceux qui font leur première réservation)
    COUNT(DISTINCT CASE 
        WHEN r.creation_date = (
            SELECT MIN(creation_date) 
            FROM reservation r2 
            WHERE r2.Id_Users = r.Id_Users
        ) THEN r.Id_Users 
    END) AS nouveaux_utilisateurs

FROM reservation r
WHERE r.creation_date >= CURRENT_DATE - INTERVAL '90 days'  -- Derniers 90 jours
GROUP BY 
    DATE(r.creation_date),
    DATE_TRUNC('week', r.creation_date),
    DATE_TRUNC('month', r.creation_date)
ORDER BY jour DESC;

-- =====================================================================
-- VIEW 5 : SYNTHÈSE GLOBALE 
-- =====================================================================

CREATE OR REPLACE VIEW v_dashboard_synthese AS
SELECT 
    -- Période
    CURRENT_DATE AS date_jour,
    DATE_TRUNC('month', CURRENT_DATE) AS mois_courant,
    DATE_TRUNC('year', CURRENT_DATE) AS annee_courante,
    
    -- Commissions du jour
    (SELECT COALESCE(SUM(price), 0) FROM commission_received 
     WHERE DATE(payement_date) = CURRENT_DATE) AS commissions_jour,
    
    -- Commissions du mois
    (SELECT COALESCE(SUM(price), 0) FROM commission_received 
     WHERE DATE_TRUNC('month', payement_date) = DATE_TRUNC('month', CURRENT_DATE)) AS commissions_mois,
    
    -- Réservations du jour
    (SELECT COUNT(*) FROM reservation 
     WHERE DATE(creation_date) = CURRENT_DATE) AS reservations_jour,
    
    -- Réservations du mois
    (SELECT COUNT(*) FROM reservation 
     WHERE DATE_TRUNC('month', creation_date) = DATE_TRUNC('month', CURRENT_DATE)) AS reservations_mois,
    
    -- Utilisateurs actifs aujourd'hui
    (SELECT COUNT(DISTINCT Id_Users) FROM reservation 
     WHERE DATE(creation_date) = CURRENT_DATE) AS utilisateurs_actifs_jour,
    
    -- Total parkings actifs
    (SELECT COUNT(*) FROM parking) AS total_parkings,
    
    -- Top parking du mois
    (SELECT p.label FROM parking p
     LEFT JOIN parking_vehicles pv ON p.Id_Parking = pv.Id_Parking
     LEFT JOIN announcements_vehicles av ON pv.Id_Parking_vehicles = av.Id_Parking_vehicles
     LEFT JOIN reservation_vehicles rv ON av.Id_Announcements_vehicles = rv.Id_Announcements_vehicles
     LEFT JOIN reservation r ON rv.Id_Reservation = r.Id_Reservation
     WHERE DATE_TRUNC('month', COALESCE(r.creation_date, CURRENT_DATE)) = DATE_TRUNC('month', CURRENT_DATE)
     GROUP BY p.Id_Parking, p.label
     ORDER BY COUNT(r.Id_Reservation) DESC
     LIMIT 1) AS top_parking_mois;


-- INDEX

-- Index pour les commissions
CREATE INDEX IF NOT EXISTS idx_commission_received_payement_date ON commission_received(payement_date);
CREATE INDEX IF NOT EXISTS idx_commission_received_types ON commission_received(Id_Commission_types);

-- Index pour les réservations
CREATE INDEX IF NOT EXISTS idx_reservation_creation_date ON reservation(creation_date);
CREATE INDEX IF NOT EXISTS idx_reservation_status ON reservation(Id_Reservation_status);
CREATE INDEX IF NOT EXISTS idx_reservation_users ON reservation(Id_Users);

-- Index pour les parkings
CREATE INDEX IF NOT EXISTS idx_parking_users ON parking(Id_Users);
CREATE INDEX IF NOT EXISTS idx_user_note_parking ON user_note(id_parking);
CREATE INDEX IF NOT EXISTS idx_user_note_user ON user_note(id_user);
-- Index pour les tables de liaison
CREATE INDEX IF NOT EXISTS idx_parking_vehicles_parking ON parking_vehicles(Id_Parking);
CREATE INDEX IF NOT EXISTS idx_announcements_vehicles_parking_vehicles ON announcements_vehicles(Id_Parking_vehicles);
CREATE INDEX IF NOT EXISTS idx_reservation_vehicles_reservation ON reservation_vehicles(Id_Reservation);
CREATE INDEX IF NOT EXISTS idx_reservation_vehicles_announcements ON reservation_vehicles(Id_Announcements_vehicles);
