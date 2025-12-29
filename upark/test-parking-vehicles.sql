-- Script de test pour vérifier l'enregistrement dans Parking_vehicles
-- À exécuter après avoir créé un parking via l'application

-- 1. Vérifier les parkings créés
SELECT 
    p.id_parking,
    p.label,
    p.hourly_rate,
    p.is_active,
    p.created_at,
    u.user_name as proprietaire
FROM parking p
LEFT JOIN users u ON p.id_users = u.id_users
ORDER BY p.created_at DESC
LIMIT 5;

-- 2. Vérifier les véhicules associés à un parking spécifique
-- Remplacer <PARKING_ID> par l'ID du parking créé
SELECT 
    pv.id_parking_vehicles,
    p.label as parking_name,
    v.types as vehicle_type,
    pv.numbers as nb_places,
    v.icon
FROM parking_vehicles pv
JOIN parking p ON pv.id_parking = p.id_parking
JOIN vehicles v ON pv.id_vehicles = v.id_vehicles
WHERE pv.id_parking = <PARKING_ID>;

-- 3. Voir tous les parkings avec leurs véhicules (vue complète)
SELECT 
    p.id_parking,
    p.label as parking_name,
    STRING_AGG(v.types || ' (' || pv.numbers || ' places)', ', ') as vehicules_acceptes
FROM parking p
LEFT JOIN parking_vehicles pv ON p.id_parking = pv.id_parking
LEFT JOIN vehicles v ON pv.id_vehicles = v.id_vehicles
GROUP BY p.id_parking, p.label
ORDER BY p.created_at DESC;

-- 4. Compter le total de places par parking
SELECT 
    p.id_parking,
    p.label,
    COUNT(DISTINCT pv.id_vehicles) as types_vehicules,
    SUM(pv.numbers) as total_places
FROM parking p
LEFT JOIN parking_vehicles pv ON p.id_parking = pv.id_parking
GROUP BY p.id_parking, p.label
HAVING SUM(pv.numbers) > 0
ORDER BY total_places DESC;
