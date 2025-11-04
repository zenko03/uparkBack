-- Script de données de test pour l'application Upark
-- Données en français avec ordre d'exécution respecté

-- 1. Types de véhicules
INSERT INTO Vehicles (types, icon) VALUES
('Voiture', 'car-icon'),
('Moto', 'motorcycle-icon'),
('Utilitaire', 'van-icon'),
('Camion', 'truck-icon'),
('Citadine', 'city-car-icon');

-- 2. Jours de la semaine
INSERT INTO Days_week (day_name) VALUES
('Lundi'),
('Mardi'),
('Mercredi'),
('Jeudi'),
('Vendredi'),
('Samedi'),
('Dimanche');

-- 3. Statuts de réservation
INSERT INTO Reservation_status (label, value_) VALUES
('En attente', 0),
('Confirmée', 1),
('En cours', 2),
('Terminée', 3),
('Annulée', 4);

-- 4. Statuts de paiement
INSERT INTO Payment_status (label, value_) VALUES
('En attente', 0),
('Payé', 1),
('Échoué', 2),
('Remboursé', 3);

-- 5. Types de commission
INSERT INTO Commission_types (label) VALUES
('Commission globale'),
('Commission par véhicule'),
('Commission partenaire');

-- 6. Types de profil
INSERT INTO Profil_types (types) VALUES
('Propriétaire'),
('Locataire'),
('Partenaire');

-- 7. Utilisateurs (avec un admin)
-- Mot de passe: password123 (encodé en BCrypt)
INSERT INTO Users (name, first_name, user_name, email, password, phone_number, role) VALUES
('Admin', 'Principal', 'admin', 'admin@upark.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaUKk7h.T0mUO', '0341234567', 'ADMIN'),
('Rakoto', 'Jean', 'jean_rakoto', 'jean.rakoto@gmail.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaUKk7h.T0mUO', '0342345678', 'USER'),
('Rabe', 'Marie', 'marie_rabe', 'marie.rabe@gmail.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaUKk7h.T0mUO', '0343456789', 'USER'),
('Rasoa', 'Paul', 'paul_rasoa', 'paul.rasoa@gmail.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaUKk7h.T0mUO', '0344567890', 'USER');

-- 8. Profils des utilisateurs
INSERT INTO Profils (Id_Profil_types, Id_Users) VALUES
(1, 1), -- Admin comme propriétaire
(1, 2), -- Jean comme propriétaire
(2, 3), -- Marie comme locataire
(1, 4); -- Paul comme propriétaire

-- 9. Parkings
INSERT INTO Parking (label, hourly_rate, description, localisation, Id_Users) VALUES
('Parking Centre Ville Analakely', 2.50, 'Parking sécurisé en plein centre ville avec surveillance', 'SRID=4326;POINT(47.5260 -18.9137)', 2),
('Parking Isoraka', 1.80, 'Parking couvert dans le quartier Isoraka', 'SRID=4326;POINT(47.5214 -18.9039)', 2),
('Parking Andraharo', 3.00, 'Parking moderne avec accès facile', 'SRID=4326;POINT(47.5367 -18.8945)', 4),
('Parking Behoririka', 2.20, 'Parking abordable pour résidents', 'SRID=4326;POINT(47.5189 -18.9203)', 4),
('Parking Ivandry', 3.50, 'Parking haut de gamme avec services premium', 'SRID=4326;POINT(47.5412 -18.8834)', 4);

-- 10. Associations parking-véhicules
INSERT INTO Parking_vehicles (numbers, Id_Vehicles, Id_Parking) VALUES
(10, 1, 1), -- 10 places voiture pour Analakely
(5, 2, 1),  -- 5 places moto pour Analakely
(8, 1, 2),  -- 8 places voiture pour Isoraka
(3, 2, 2),  -- 3 places moto pour Isoraka
(15, 1, 3), -- 15 places voiture pour Andraharo
(12, 1, 4), -- 12 places voiture pour Behoririka
(20, 1, 5), -- 20 places voiture pour Ivandry
(10, 3, 5); -- 10 places utilitaire pour Ivandry

-- 11. Commission globale
INSERT INTO Global_commission (rate, creation_date) VALUES
(10.0, CURRENT_TIMESTAMP);

-- 12. Commissions par véhicule
INSERT INTO Commission_vehicles (rate, creation_date, Id_Vehicles) VALUES
(8.0, CURRENT_TIMESTAMP, 1), -- Voiture
(6.0, CURRENT_TIMESTAMP, 2), -- Moto
(12.0, CURRENT_TIMESTAMP, 3); -- Utilitaire

-- 13. Annonces
INSERT INTO Announcements (description, creation_date) VALUES
('Parking disponible en centre ville', CURRENT_TIMESTAMP),
('Offre spéciale parking Isoraka', CURRENT_TIMESTAMP),
('Nouveau parking Andraharo', CURRENT_TIMESTAMP);

-- 14. Associations annonces-véhicules
INSERT INTO Announcements_vehicles (numbers, Id_Announcements, Id_Parking_vehicles) VALUES
(5, 1, 1), -- 5 places voiture annoncées
(3, 1, 2), -- 3 places moto annoncées
(4, 2, 3), -- 4 places voiture Isoraka
(2, 2, 4), -- 2 places moto Isoraka
(8, 3, 5); -- 8 places voiture Andraharo

-- 15. Délai de réservation
INSERT INTO Reservation_delay (delay_in_hours, delay_in_minutes, creation_date) VALUES
(24, 0, CURRENT_TIMESTAMP);

-- 16. Réservations
INSERT INTO Reservation (total_price, creation_date, payement_date, Id_Users, Id_Reservation_status) VALUES
(25.00, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days', 3, 3), -- Terminée
(45.50, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', 3, 2), -- En cours
(30.00, CURRENT_TIMESTAMP, NULL, 3, 1); -- Confirmée

-- 17. Associations réservation-véhicules
INSERT INTO Reservation_vehicles (numbers, Id_Reservation, Id_Announcements_vehicles) VALUES
(1, 1, 1), -- 1 place voiture réservée
(1, 2, 3), -- 1 place voiture Isoraka
(1, 3, 5); -- 1 place voiture Andraharo

-- 18. Notes de parking
INSERT INTO Parking_note (note, Id_Users, Id_Parking) VALUES
(4.5, 3, 1), -- Marie note Analakely
(4.0, 3, 2), -- Marie note Isoraka
(5.0, 3, 3); -- Marie note Andraharo

-- 19. Commissions reçues
INSERT INTO Commission_received (price, payement_date, Id_Commission_types, Id_Reservation, Id_Payment_status) VALUES
(2.50, CURRENT_TIMESTAMP - INTERVAL '2 days', 1, 1, 1), -- Commission payée
(4.55, CURRENT_TIMESTAMP - INTERVAL '1 day', 1, 2, 1); -- Commission payée

-- 20. Disponibilités par date
INSERT INTO Availabilities_date (start_hour, start_date, end_date, end_hour, Id_Reservation_vehicles, Id_Announcements_vehicles) VALUES
('08:00', CURRENT_DATE, CURRENT_DATE + 1, '18:00', 1, 1),
('09:00', CURRENT_DATE + 1, CURRENT_DATE + 2, '17:00', 2, 3);

-- 21. Disponibilités par fréquence
INSERT INTO Availabilities_frequence (start_hour, end_hour, Id_Reservation_vehicles, Id_Announcements_vehicles, Id_Days_week) VALUES
('08:00', '18:00', NULL, 1, 1), -- Lundi
('08:00', '18:00', NULL, 1, 2), -- Mardi
('08:00', '18:00', NULL, 1, 3), -- Mercredi
('08:00', '18:00', NULL, 1, 4), -- Jeudi
('08:00', '18:00', NULL, 1, 5); -- Vendredi