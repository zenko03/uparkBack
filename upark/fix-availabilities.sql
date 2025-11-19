-- Script pour diagnostiquer et corriger les problèmes de disponibilité

-- 1. Vérifier les parkings
SELECT 'PARKINGS' as table_name;
SELECT * FROM parking WHERE id_parking = 1;

-- 2. Vérifier les types de véhicules
SELECT 'VEHICLES' as table_name;
SELECT * FROM vehicles;

-- 3. Vérifier parking_vehicles (capacité du parking)
SELECT 'PARKING_VEHICLES' as table_name;
SELECT pv.*, v.types 
FROM parking_vehicles pv
JOIN vehicles v ON pv.id_vehicles = v.id_vehicles
WHERE pv.id_parking = 1;

-- 4. Vérifier les annonces
SELECT 'ANNOUNCEMENTS_VEHICLES' as table_name;
SELECT av.*, pv.id_parking, v.types
FROM announcements_vehicles av
JOIN parking_vehicles pv ON av.id_parking_vehicles = pv.id_parking_vehicles
JOIN vehicles v ON pv.id_vehicles = v.id_vehicles
WHERE pv.id_parking = 1;

-- 5. Vérifier les disponibilités par fréquence
SELECT 'AVAILABILITIES_FREQUENCE' as table_name;
SELECT af.*, dw.day_name, v.types
FROM availabilities_frequence af
LEFT JOIN days_week dw ON af.id_days_week = dw.id_days_week
LEFT JOIN announcements_vehicles av ON af.id_announcements_vehicles = av.id_announcements_vehicles
LEFT JOIN parking_vehicles pv ON av.id_parking_vehicles = pv.id_parking_vehicles
LEFT JOIN vehicles v ON pv.id_vehicles = v.id_vehicles
WHERE pv.id_parking = 1
ORDER BY dw.id_days_week, v.types;

-- 6. CORRIGER : Ajouter les disponibilités manquantes pour TOUS les types de véhicules du parking 1
-- Pour chaque jour de la semaine (Lundi=1 à Dimanche=7) et chaque type de véhicule

-- D'abord, vérifier combien d'annonces_vehicles existent pour le parking 1
SELECT 'COUNT ANNOUNCEMENTS_VEHICLES FOR PARKING 1' as info;
SELECT COUNT(*) FROM announcements_vehicles av
JOIN parking_vehicles pv ON av.id_parking_vehicles = pv.id_parking_vehicles
WHERE pv.id_parking = 1;

-- Ajouter les disponibilités pour TOUS les jours et TOUS les véhicules du parking 1
-- Exemple : si announcements_vehicles.id = 1 correspond à Voiture, et id = 2 correspond à Moto

-- Pour Voiture (si announcements_vehicles id=1 est pour voiture du parking 1)
INSERT INTO availabilities_frequence (start_hour, end_hour, id_reservation_vehicles, id_announcements_vehicles, id_days_week)
SELECT '08:00', '20:00', NULL, av.id_announcements_vehicles, dw.id_days_week
FROM announcements_vehicles av
JOIN parking_vehicles pv ON av.id_parking_vehicles = pv.id_parking_vehicles
CROSS JOIN days_week dw
WHERE pv.id_parking = 1 
AND NOT EXISTS (
    SELECT 1 FROM availabilities_frequence af2
    WHERE af2.id_announcements_vehicles = av.id_announcements_vehicles
    AND af2.id_days_week = dw.id_days_week
);

-- Vérification après insertion
SELECT 'VERIFICATION APRES INSERTION' as info;
SELECT af.*, dw.day_name, v.types
FROM availabilities_frequence af
LEFT JOIN days_week dw ON af.id_days_week = dw.id_days_week
LEFT JOIN announcements_vehicles av ON af.id_announcements_vehicles = av.id_announcements_vehicles
LEFT JOIN parking_vehicles pv ON av.id_parking_vehicles = pv.id_parking_vehicles
LEFT JOIN vehicles v ON pv.id_vehicles = v.id_vehicles
WHERE pv.id_parking = 1
ORDER BY v.types, dw.id_days_week;
