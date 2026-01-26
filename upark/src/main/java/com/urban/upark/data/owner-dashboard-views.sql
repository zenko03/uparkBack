-- =====================================================================
-- VUES DASHBOARD FRONT OFFICE - PROPRIÉTAIRE DE PARKINGS
-- =====================================================================

-- Vue 1: Revenus mensuels du propriétaire avec évolution
CREATE OR REPLACE VIEW public.v_owner_monthly_revenue AS
SELECT 
    p.id_users AS owner_id,
    
    -- Mois courant
    DATE_TRUNC('month', CURRENT_DATE) AS mois_courant,
    COALESCE(SUM(CASE 
        WHEN DATE_TRUNC('month', r.creation_date) = DATE_TRUNC('month', CURRENT_DATE)
        THEN r.total_price 
        ELSE 0 
    END), 0) AS revenus_mois_courant,
    
    -- Mois précédent
    COALESCE(SUM(CASE 
        WHEN DATE_TRUNC('month', r.creation_date) = DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month')
        THEN r.total_price 
        ELSE 0 
    END), 0) AS revenus_mois_precedent,
    
    -- Évolution en pourcentage
    CASE 
        WHEN SUM(CASE 
            WHEN DATE_TRUNC('month', r.creation_date) = DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month')
            THEN r.total_price 
            ELSE 0 
        END) > 0
        THEN ROUND(
            ((SUM(CASE 
                WHEN DATE_TRUNC('month', r.creation_date) = DATE_TRUNC('month', CURRENT_DATE)
                THEN r.total_price 
                ELSE 0 
            END) - SUM(CASE 
                WHEN DATE_TRUNC('month', r.creation_date) = DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month')
                THEN r.total_price 
                ELSE 0 
            END)) / SUM(CASE 
                WHEN DATE_TRUNC('month', r.creation_date) = DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month')
                THEN r.total_price 
                ELSE 0 
            END) * 100)::numeric, 2
        )
        ELSE 0
    END AS evolution_pourcentage,
    
    -- Nombre de transactions
    COUNT(CASE 
        WHEN DATE_TRUNC('month', r.creation_date) = DATE_TRUNC('month', CURRENT_DATE)
        THEN r.id_reservation 
    END) AS nombre_transactions_mois

FROM parking p
LEFT JOIN parking_vehicles pv ON p.id_parking = pv.id_parking
LEFT JOIN announcements_vehicles av ON pv.id_parking_vehicles = av.id_parking_vehicles
LEFT JOIN reservation_vehicles rv ON av.id_announcements_vehicles = rv.id_announcements_vehicles
LEFT JOIN reservation r ON rv.id_reservation = r.id_reservation
WHERE r.creation_date >= DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month')
GROUP BY p.id_users;


-- Vue 2: Statistiques des réservations du propriétaire
CREATE OR REPLACE VIEW public.v_owner_reservations_stats AS
SELECT 
    p.id_users AS owner_id,
    
    -- Réservations mois courant
    COUNT(CASE 
        WHEN DATE_TRUNC('month', r.creation_date) = DATE_TRUNC('month', CURRENT_DATE)
        THEN r.id_reservation 
    END) AS reservations_mois_courant,
    
    -- Réservations mois précédent
    COUNT(CASE 
        WHEN DATE_TRUNC('month', r.creation_date) = DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month')
        THEN r.id_reservation 
    END) AS reservations_mois_precedent,
    
    -- Évolution en pourcentage
    CASE 
        WHEN COUNT(CASE 
            WHEN DATE_TRUNC('month', r.creation_date) = DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month')
            THEN r.id_reservation 
        END) > 0
        THEN ROUND(
            ((COUNT(CASE 
                WHEN DATE_TRUNC('month', r.creation_date) = DATE_TRUNC('month', CURRENT_DATE)
                THEN r.id_reservation 
            END)::numeric - COUNT(CASE 
                WHEN DATE_TRUNC('month', r.creation_date) = DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month')
                THEN r.id_reservation 
            END)::numeric) / COUNT(CASE 
                WHEN DATE_TRUNC('month', r.creation_date) = DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month')
                THEN r.id_reservation 
            END)::numeric * 100)::numeric, 2
        )
        ELSE 0
    END AS evolution_pourcentage,
    
    -- Par statut
    COUNT(CASE WHEN rs.value_ = 1 THEN r.id_reservation END) AS en_attente,
    COUNT(CASE WHEN rs.value_ = 2 THEN r.id_reservation END) AS confirmees,
    COUNT(CASE WHEN rs.value_ = 3 THEN r.id_reservation END) AS annulees,
    COUNT(CASE WHEN rs.value_ = 4 THEN r.id_reservation END) AS terminees

FROM parking p
LEFT JOIN parking_vehicles pv ON p.id_parking = pv.id_parking
LEFT JOIN announcements_vehicles av ON pv.id_parking_vehicles = av.id_parking_vehicles
LEFT JOIN reservation_vehicles rv ON av.id_announcements_vehicles = rv.id_announcements_vehicles
LEFT JOIN reservation r ON rv.id_reservation = r.id_reservation
LEFT JOIN reservation_status rs ON r.id_reservation_status = rs.id_reservation_status
WHERE r.creation_date >= DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month')
GROUP BY p.id_users;


-- Vue 3: Disponibilité des parkings du propriétaire
CREATE OR REPLACE VIEW public.v_owner_parking_availability AS
SELECT 
    p.id_users AS owner_id,
    
    -- Nombre de parkings
    COUNT(DISTINCT p.id_parking) AS nombre_parkings,
    
    -- Capacité totale
    COALESCE(SUM(pv.numbers), 0) AS capacite_totale,
    
    -- Places occupées actuellement (réservations en cours)
    COALESCE(SUM(CASE 
        WHEN r.start_datetime <= NOW() 
        AND r.end_datetime >= NOW() 
        AND rs.value_ = 2  -- Confirmées
        THEN rv.numbers 
        ELSE 0 
    END), 0) AS places_occupees,
    
    -- Places disponibles
    COALESCE(SUM(pv.numbers), 0) - COALESCE(SUM(CASE 
        WHEN r.start_datetime <= NOW() 
        AND r.end_datetime >= NOW() 
        AND rs.value_ = 2
        THEN rv.numbers 
        ELSE 0 
    END), 0) AS places_disponibles,
    
    -- Taux d'occupation en pourcentage
    CASE 
        WHEN SUM(pv.numbers) > 0 
        THEN ROUND(
            (SUM(CASE 
                WHEN r.start_datetime <= NOW() 
                AND r.end_datetime >= NOW() 
                AND rs.value_ = 2
                THEN rv.numbers 
                ELSE 0 
            END)::numeric / SUM(pv.numbers)::numeric * 100)::numeric, 2
        )
        ELSE 0 
    END AS taux_occupation

FROM parking p
LEFT JOIN parking_vehicles pv ON p.id_parking = pv.id_parking
LEFT JOIN announcements_vehicles av ON pv.id_parking_vehicles = av.id_parking_vehicles
LEFT JOIN reservation_vehicles rv ON av.id_announcements_vehicles = rv.id_announcements_vehicles
LEFT JOIN reservation r ON rv.id_reservation = r.id_reservation
LEFT JOIN reservation_status rs ON r.id_reservation_status = rs.id_reservation_status
GROUP BY p.id_users;


-- Vue 4: Revenus hebdomadaires du propriétaire (7 derniers jours)
CREATE OR REPLACE VIEW public.v_owner_weekly_revenue AS
SELECT 
    p.id_users AS owner_id,
    DATE(r.creation_date) AS jour,
    EXTRACT(DOW FROM r.creation_date) AS jour_semaine,  -- 0=Dimanche, 6=Samedi
    
    -- Revenus du jour
    COALESCE(SUM(r.total_price), 0) AS revenus_jour,
    
    -- Nombre de réservations
    COUNT(r.id_reservation) AS nombre_reservations,
    
    -- Revenus semaine courante
    SUM(COALESCE(r.total_price, 0)) OVER (
        PARTITION BY p.id_users, DATE_TRUNC('week', r.creation_date)
    ) AS revenus_semaine,
    
    -- Revenus semaine précédente
    (SELECT COALESCE(SUM(r2.total_price), 0)
     FROM parking p2
     LEFT JOIN parking_vehicles pv2 ON p2.id_parking = pv2.id_parking
     LEFT JOIN announcements_vehicles av2 ON pv2.id_parking_vehicles = av2.id_parking_vehicles
     LEFT JOIN reservation_vehicles rv2 ON av2.id_announcements_vehicles = rv2.id_announcements_vehicles
     LEFT JOIN reservation r2 ON rv2.id_reservation = r2.id_reservation
     WHERE p2.id_users = p.id_users
     AND DATE_TRUNC('week', r2.creation_date) = DATE_TRUNC('week', CURRENT_DATE - INTERVAL '1 week')
    ) AS revenus_semaine_precedente

FROM parking p
LEFT JOIN parking_vehicles pv ON p.id_parking = pv.id_parking
LEFT JOIN announcements_vehicles av ON pv.id_parking_vehicles = av.id_parking_vehicles
LEFT JOIN reservation_vehicles rv ON av.id_announcements_vehicles = rv.id_announcements_vehicles
LEFT JOIN reservation r ON rv.id_reservation = r.id_reservation
WHERE r.creation_date >= CURRENT_DATE - INTERVAL '14 days'
GROUP BY 
    p.id_users,
    DATE(r.creation_date),
    EXTRACT(DOW FROM r.creation_date),
    DATE_TRUNC('week', r.creation_date)
ORDER BY jour DESC;


-- Vue 5: Notifications récentes du propriétaire (filtrée côté app avec WHERE id_users = ?)
CREATE OR REPLACE VIEW public.v_owner_notifications AS
SELECT 
    n.id_notification,
    n.id_users AS owner_id,
    n.title,
    n.message,
    n.type,
    n.read,
    n.sent_at,
    n.read_at,
    
    -- Temps écoulé relatif
    CASE 
        WHEN NOW() - n.sent_at < INTERVAL '1 hour' 
        THEN EXTRACT(MINUTE FROM NOW() - n.sent_at)::integer || ' min'
        WHEN NOW() - n.sent_at < INTERVAL '24 hours' 
        THEN EXTRACT(HOUR FROM NOW() - n.sent_at)::integer || ' h'
        WHEN NOW() - n.sent_at < INTERVAL '7 days' 
        THEN EXTRACT(DAY FROM NOW() - n.sent_at)::integer || ' j'
        ELSE TO_CHAR(n.sent_at, 'DD/MM/YYYY')
    END AS temps_relatif,
    
    -- Informations complémentaires
    n.id_reservation,
    n.id_reservation_request,
    r.total_price AS montant_reservation,
    p.label AS parking_label

FROM notifications n
LEFT JOIN reservation r ON n.id_reservation = r.id_reservation
LEFT JOIN reservation_vehicles rv ON r.id_reservation = rv.id_reservation
LEFT JOIN announcements_vehicles av ON rv.id_announcements_vehicles = av.id_announcements_vehicles
LEFT JOIN parking_vehicles pv ON av.id_parking_vehicles = pv.id_parking_vehicles
LEFT JOIN parking p ON pv.id_parking = p.id_parking
ORDER BY n.sent_at DESC;


-- Vue 6: Dashboard synthèse propriétaire (toutes métriques en une requête)
CREATE OR REPLACE VIEW public.v_owner_dashboard_summary AS
SELECT 
    p.id_users AS owner_id,
    
    -- Revenus mois
    rev.revenus_mois_courant,
    rev.revenus_mois_precedent,
    rev.evolution_pourcentage AS evolution_revenus,
    
    -- Réservations mois
    res.reservations_mois_courant,
    res.reservations_mois_precedent,
    res.evolution_pourcentage AS evolution_reservations,
    
    -- Disponibilité
    avail.nombre_parkings,
    avail.capacite_totale,
    avail.places_disponibles,
    avail.places_occupees,
    avail.taux_occupation,
    
    -- Notifications non lues
    (SELECT COUNT(*) FROM notifications n WHERE n.id_users = p.id_users AND n.read = false) AS notifications_non_lues

FROM parking p
LEFT JOIN v_owner_monthly_revenue rev ON p.id_users = rev.owner_id
LEFT JOIN v_owner_reservations_stats res ON p.id_users = res.owner_id
LEFT JOIN v_owner_parking_availability avail ON p.id_users = avail.owner_id
GROUP BY 
    p.id_users,
    rev.revenus_mois_courant,
    rev.revenus_mois_precedent,
    rev.evolution_pourcentage,
    res.reservations_mois_courant,
    res.reservations_mois_precedent,
    res.evolution_pourcentage,
    avail.nombre_parkings,
    avail.capacite_totale,
    avail.places_disponibles,
    avail.places_occupees,
    avail.taux_occupation;


-- =====================================================================
-- INDEX OPTIMISATION
-- =====================================================================

CREATE INDEX IF NOT EXISTS idx_parking_owner ON parking(id_users);
CREATE INDEX IF NOT EXISTS idx_reservation_dates ON reservation(creation_date, start_datetime, end_datetime);
CREATE INDEX IF NOT EXISTS idx_notifications_owner ON notifications(id_users, read, sent_at);
