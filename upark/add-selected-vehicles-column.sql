-- Migration: Ajouter la colonne selected_vehicles_json à reservation_requests
-- Cette colonne stocke les véhicules sélectionnés par l'utilisateur lors de la demande

ALTER TABLE reservation_requests 
ADD COLUMN IF NOT EXISTS selected_vehicles_json TEXT;

COMMENT ON COLUMN reservation_requests.selected_vehicles_json IS 
'Véhicules sélectionnés stockés en JSON format: [{"vehicleTypeId": 1, "quantity": 2}, ...]';

-- Vérification
SELECT column_name, data_type, character_maximum_length
FROM information_schema.columns
WHERE table_name = 'reservation_requests'
  AND column_name = 'selected_vehicles_json';
