-- =====================================================================
-- SCRIPT RAPIDE: Ajouter les disponibilités manquantes
-- =====================================================================
-- Ce script ajoute automatiquement des disponibilités pour TOUS les
-- parkings qui n'en ont pas encore.
-- 
-- Utilisation: Exécuter ce script après avoir inséré des parkings
-- et des annonces pour les rendre immédiatement réservables.
-- =====================================================================

-- Supprimer toutes les anciennes disponibilités par fréquence (optionnel)
-- TRUNCATE TABLE availabilities_frequence CASCADE;

-- Ajouter les disponibilités pour TOUS les véhicules annoncés
-- Pour TOUS les jours de la semaine
-- Horaires: 06:00 - 22:00
INSERT INTO availabilities_frequence (
    start_hour, 
    end_hour, 
    id_reservation_vehicles, 
    id_announcements_vehicles, 
    id_days_week
)
SELECT 
    '06:00:00'::time as start_hour,
    '22:00:00'::time as end_hour,
    NULL as id_reservation_vehicles,
    av.id_announcements_vehicles,
    dw.id_days_week
FROM announcements_vehicles av
CROSS JOIN days_week dw
WHERE NOT EXISTS (
    SELECT 1 
    FROM availabilities_frequence af2 
    WHERE af2.id_announcements_vehicles = av.id_announcements_vehicles 
    AND af2.id_days_week = dw.id_days_week
);

-- Afficher le résumé
SELECT 
    p.id_parking,
    p.label as parking_name,
    v.types as vehicle_type,
    COUNT(DISTINCT dw.id_days_week) as jours_disponibles,
    MIN(af.start_hour) as ouverture,
    MAX(af.end_hour) as fermeture
FROM parking p
JOIN parking_vehicles pv ON p.id_parking = pv.id_parking
JOIN vehicles v ON pv.id_vehicles = v.id_vehicles
JOIN announcements_vehicles av ON pv.id_parking_vehicles = av.id_parking_vehicles
LEFT JOIN availabilities_frequence af ON av.id_announcements_vehicles = af.id_announcements_vehicles
LEFT JOIN days_week dw ON af.id_days_week = dw.id_days_week
GROUP BY p.id_parking, p.label, v.types
ORDER BY p.id_parking, v.id_vehicles;

-- Message de confirmation
DO $$
DECLARE
    total_availabilities INTEGER;
    total_parkings INTEGER;
BEGIN
    SELECT COUNT(*) INTO total_availabilities FROM availabilities_frequence;
    SELECT COUNT(DISTINCT p.id_parking) INTO total_parkings 
    FROM parking p
    JOIN parking_vehicles pv ON p.id_parking = pv.id_parking
    JOIN announcements_vehicles av ON pv.id_parking_vehicles = av.id_parking_vehicles
    JOIN availabilities_frequence af ON av.id_announcements_vehicles = af.id_announcements_vehicles;
    
    RAISE NOTICE '====================================================================';
    RAISE NOTICE 'DISPONIBILITÉS CONFIGURÉES AVEC SUCCÈS';
    RAISE NOTICE '====================================================================';
    RAISE NOTICE 'Total de créneaux disponibles: %', total_availabilities;
    RAISE NOTICE 'Nombre de parkings avec disponibilités: %', total_parkings;
    RAISE NOTICE 'Horaires configurés: 06:00 - 22:00';
    RAISE NOTICE 'Fréquence: 7 jours sur 7';
    RAISE NOTICE '====================================================================';
END $$;
