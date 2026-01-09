-- Vérifier si le parking ID 12 existe
SELECT * FROM parking WHERE id_parking = 12;

-- Voir tous les parkings
SELECT id_parking, label, hourly_rate, id_users, is_active 
FROM parking 
ORDER BY id_parking DESC 
LIMIT 10;

-- Vérifier les images de parking
SELECT * FROM parking_images 
ORDER BY created_at DESC 
LIMIT 10;
