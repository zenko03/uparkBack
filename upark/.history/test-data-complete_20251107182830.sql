-- =====================================================================
-- DONNÉES DE TEST COMPLÈTES ET RÉELLES POUR UPARK MVP
-- =====================================================================
-- Script respectant l'ordre des clés étrangères pour éviter les erreurs
-- Données réalistes pour une application de parking à Antananarivo
-- =====================================================================

-- =====================================================================
-- ÉTAPE 1: TABLES DE RÉFÉRENCE (sans dépendances)
-- =====================================================================

-- Types de véhicules (réalistes pour Madagascar)
INSERT INTO Vehicles (types, icon) VALUES
('Voiture', 'car-icon'),
('Moto', 'motorcycle-icon'),
('Utilitaire léger', 'van-icon'),
('Camionnette', 'truck-icon'),
('Citadine', 'city-car-icon'),
('SUV', 'suv-icon'),
('Vélo électrique', 'ebike-icon');

-- Jours de la semaine (en français)
INSERT INTO Days_week (day_name) VALUES
('Lundi'),
('Mardi'),
('Mercredi'),
('Jeudi'),
('Vendredi'),
('Samedi'),
('Dimanche');

-- Types de profil utilisateur
INSERT INTO Profil_types (types) VALUES
('Propriétaire'),
('Locataire'),
('Partenaire'),
('Administrateur');

-- Statuts de réservation (complets et logiques)
INSERT INTO Reservation_status (label, value_) VALUES
('En attente de confirmation', 10),
('Confirmée', 20),
('En cours', 15),
('Terminée', 30),
('Annulée', 25),
('Remboursée', 35);

-- Statuts de paiement
INSERT INTO Payment_status (label, value_) VALUES
('En attente', 0),
('Payé', 1),
('Échoué', 2),
('Remboursé', 3),
('Partiellement remboursé', 4);

-- Types de commission
INSERT INTO Commission_types (label) VALUES
('Commission globale'),
('Commission par véhicule'),
('Commission partenaire'),
('Commission spéciale');

-- =====================================================================
-- ÉTAPE 2: UTILISATEURS (base pour les autres tables)
-- =====================================================================

-- Mot de passe: password123 (encodé en BCrypt: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaUKk7h.T0mUO)
INSERT INTO Users (name, first_name, user_name, email, password, phone_number, role) VALUES
-- Administrateurs
('Rakoto', 'Jean Claude', 'admin_rakoto', 'admin@upark.mg', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaUKk7h.T0mUO', '+261341234567', 'ADMIN'),
('Rabe', 'Marie Claire', 'admin_marie', 'marie.admin@upark.mg', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaUKk7h.T0mUO', '+261322345678', 'ADMIN'),

-- Propriétaires de parkings
('Randrianarisoa', 'Andry', 'andry_parking', 'andry.parking@gmail.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaUKk7h.T0mUO', '+261331112223', 'USER'),
('Razafindrabe', 'Nirina', 'nirina_owner', 'nirina.owner@yahoo.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaUKk7h.T0mUO', '+261332233344', 'USER'),
('Rakotondrasoa', 'Mialy', 'mialy_park', 'mialy.parking@hotmail.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaUKk7h.T0mUO', '+261333444555', 'USER'),
('Andriamialisoa', 'Tiana', 'tiana_parking', 'tiana.parking@outlook.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaUKk7h.T0mUO', '+261334555666', 'USER'),

-- Locataires/clients
('Rasoa', 'Sitraka', 'sitraka_client', 'sitraka.client@gmail.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaUKk7h.T0mUO', '+261335666777', 'USER'),
('Rakotozafy', 'Feno', 'feno_user', 'feno.user@yahoo.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaUKk7h.T0mUO', '+261336777888', 'USER'),
('Razafimahatratra', 'Liva', 'liva_client', 'liva.client@hotmail.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaUKk7h.T0mUO', '+261337888999', 'USER'),
('Randrianomenjanahary', 'Njaka', 'njaka_user', 'njaka.user@outlook.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaUKk7h.T0mUO', '+261338999000', 'USER'),

-- Partenaires
('Société Parking Mada', 'Contact', 'partner_mada', 'contact@parking-mada.mg', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaUKk7h.T0mUO', '+261320001111', 'PARTNER');

-- Profils des utilisateurs
INSERT INTO Profils (Id_Profil_types, Id_Users) VALUES
(4, 1), -- Admin principal comme administrateur
(4, 2), -- Admin secondaire comme administrateur
(1, 3), -- Andry comme propriétaire
(1, 4), -- Nirina comme propriétaire
(1, 5), -- Mialy comme propriétaire
(1, 6), -- Tiana comme propriétaire
(2, 7), -- Sitraka comme locataire
(2, 8), -- Feno comme locataire
(2, 9), -- Liva comme locataire
(2, 10), -- Njaka comme locataire
(3, 11); -- Partenaire Mada

-- =====================================================================
-- ÉTAPE 3: PARKINGS (dépendent des utilisateurs)
-- =====================================================================

INSERT INTO Parking (label, hourly_rate, description, localisation, Id_Users) VALUES
-- Parkings d'Andry (ID 3)
('Parking Centre Ville Analakely', 2500.00, 'Parking sécurisé en plein centre ville avec surveillance 24/7. Idéal pour shopping et bureaux. Accès facile depuis l''avenue de l''Indépendance.', 'SRID=4326;POINT(47.5060 -18.9137)', 3),
('Parking Isoraka Business', 3000.00, 'Parking moderne dans le quartier des affaires. Services premium: valet, lavage, recharge électrique. Proche des ambassades et banques.', 'SRID=4326;POINT(47.5214 -18.9039)', 3),

-- Parkings de Nirina (ID 4)
('Parking Andraharo Résidentiel', 1800.00, 'Parking abordable pour résidents. Quartier calme et sécurisé. Surveillance nocturne et espaces couverts.', 'SRID=4326;POINT(47.5367 -18.8945)', 4),
('Parking Behoririka Marché', 2000.00, 'Parking stratégique près du grand marché. Accès facile pour commerçants et clients. Tarifs dégressifs à la journée.', 'SRID=4326;POINT(47.5189 -18.9203)', 4),

-- Parkings de Mialy (ID 5)
('Parking Ivandry Premium', 4000.00, 'Parking haut de gamme avec services premium: climatisation, gardiennage, assurance. Quartier résidentiel chic.', 'SRID=4326;POINT(47.5412 -18.8834)', 5),
('Parking Antaninarenina Commercial', 3500.00, 'Parking commercial en plein centre. Idéal pour commerces et bureaux. Accès direct rue commerçante.', 'SRID=4326;POINT(47.5080 -18.9100)', 5),

-- Parkings de Tiana (ID 6)
('Parking Ampefiloha Résidentiel', 1500.00, 'Parking résidentiel économique. Quartier populaire mais sécurisé. Idéal pour habitants du quartier.', 'SRID=4326;POINT(47.5250 -18.9000)', 6),
('Parking 67Ha Shopping', 2800.00, 'Parking du centre commercial 67Ha. Accès direct aux magasins. Tarifs horaires et journaliers.', 'SRID=4326;POINT(47.5150 -18.9050)', 6);

-- =====================================================================
-- ÉTAPE 4: ASSOCIATIONS PARKING-VÉHICULES
-- =====================================================================

INSERT INTO Parking_vehicles (numbers, Id_Vehicles, Id_Parking) VALUES
-- Parking Analakely (ID 1) - Mixte
(15, 1, 1), -- 15 places voiture
(8, 2, 1),  -- 8 places moto
(3, 3, 1),  -- 3 places utilitaire
(2, 6, 1),  -- 2 places SUV

-- Parking Isoraka (ID 2) - Premium
(20, 1, 2), -- 20 places voiture
(10, 2, 2), -- 10 places moto
(5, 6, 2),  -- 5 places SUV
(3, 7, 2),  -- 3 places vélo électrique

-- Parking Andraharo (ID 3) - Résidentiel
(25, 1, 3), -- 25 places voiture
(12, 2, 3), -- 12 places moto
(5, 5, 3),  -- 5 places citadine

-- Parking Behoririka (ID 4) - Commercial
(30, 1, 4), -- 30 places voiture
(15, 2, 4), -- 15 places moto
(8, 3, 4),  -- 8 places utilitaire
(5, 4, 4),  -- 5 places camionnette

-- Parking Ivandry (ID 5) - Luxury
(18, 1, 5), -- 18 places voiture
(8, 2, 5),  -- 8 places moto
(10, 6, 5), -- 10 places SUV
(5, 7, 5),  -- 5 places vélo électrique

-- Parking Antaninarenina (ID 6) - Business
(22, 1, 6), -- 22 places voiture
(10, 2, 6), -- 10 places moto
(6, 3, 6),  -- 6 places utilitaire

-- Parking Ampefiloha (ID 7) - Économique
(20, 1, 7), -- 20 places voiture
(15, 2, 7), -- 15 places moto
(8, 5, 7),  -- 8 places citadine

-- Parking 67Ha (ID 8) - Shopping
(35, 1, 8), -- 35 places voiture
(20, 2, 8), -- 20 places moto
(10, 3, 8), -- 10 places utilitaire
(5, 6, 8);  -- 5 places SUV

-- =====================================================================
-- ÉTAPE 5: CONFIGURATION COMMISSIONS ET DÉLAIS
-- =====================================================================

-- Commission globale
INSERT INTO Global_commission (rate, creation_date) VALUES
(10.0, CURRENT_TIMESTAMP - INTERVAL '30 days');

-- Commissions par véhicule
INSERT INTO Commission_vehicles (rate, creation_date, Id_Vehicles) VALUES
(8.0, CURRENT_TIMESTAMP - INTERVAL '30 days', 1), -- Voiture
(6.0, CURRENT_TIMESTAMP - INTERVAL '30 days', 2), -- Moto
(12.0, CURRENT_TIMESTAMP - INTERVAL '30 days', 3), -- Utilitaire
(15.0, CURRENT_TIMESTAMP - INTERVAL '30 days', 4), -- Camionnette
(7.0, CURRENT_TIMESTAMP - INTERVAL '30 days', 5), -- Citadine
(10.0, CURRENT_TIMESTAMP - INTERVAL '30 days', 6), -- SUV
(5.0, CURRENT_TIMESTAMP - INTERVAL '30 days', 7); -- Vélo électrique

-- Commissions partenaires
INSERT INTO Commission_partners (rate, creation_date, Id_Users) VALUES
(5.0, CURRENT_TIMESTAMP - INTERVAL '30 days', 11); -- Partenaire Mada

-- Délai de réservation
INSERT INTO Reservation_delay (delay_in_hours, delay_in_minutes, creation_date) VALUES
(2, 0, CURRENT_TIMESTAMP - INTERVAL '30 days'); -- 2 heures minimum

-- =====================================================================
-- ÉTAPE 6: ANNONCES (dépendent des parking_vehicles)
-- =====================================================================

INSERT INTO Announcements (description, creation_date) VALUES
('Parking disponible en centre ville Analakely - idéal pour professionnels et shopping', CURRENT_TIMESTAMP - INTERVAL '25 days'),
('Offre spéciale parking Isoraka Business - services premium inclus', CURRENT_TIMESTAMP - INTERVAL '20 days'),
('Nouveau parking Andraharo - tarif résidentiel avantageux', CURRENT_TIMESTAMP - INTERVAL '15 days'),
('Parking Behoririka près du marché - accès facile commerçants', CURRENT_TIMESTAMP - INTERVAL '10 days'),
('Parking Ivandry Premium - services haut de gamme', CURRENT_TIMESTAMP - INTERVAL '5 days'),
('Parking Antaninarenina commercial - emplacement stratégique', CURRENT_TIMESTAMP - INTERVAL '3 days'),
('Parking Ampefiloha résidentiel - tarif économique', CURRENT_TIMESTAMP - INTERVAL '2 days'),
('Parking 67Ha Shopping - direct centre commercial', CURRENT_TIMESTAMP - INTERVAL '1 day');

-- Associations annonces-véhicules
INSERT INTO Announcements_vehicles (numbers, Id_Announcements, Id_Parking_vehicles) VALUES
-- Annonce 1 (Analakely)
(10, 1, 1), -- 10 places voiture annoncées
(5, 1, 2),  -- 5 places moto annoncées
(2, 1, 3),  -- 2 places utilitaire annoncées

-- Annonce 2 (Isoraka)
(15, 2, 5), -- 15 places voiture Isoraka
(8, 2, 6),  -- 8 places moto Isoraka
(3, 2, 8),  -- 3 places SUV Isoraka

-- Annonce 3 (Andraharo)
(20, 3, 9), -- 20 places voiture Andraharo
(10, 3, 10), -- 10 places moto Andraharo

-- Annonce 4 (Behoririka)
(25, 4, 13), -- 25 places voiture Behoririka
(12, 4, 14), -- 12 places moto Behoririka
(6, 4, 15),  -- 6 places utilitaire Behoririka

-- Annonce 5 (Ivandry)
(15, 5, 17), -- 15 places voiture Ivandry
(6, 5, 18),  -- 6 places moto Ivandry
(8, 5, 19),  -- 8 places SUV Ivandry

-- Annonce 6 (Antaninarenina)
(18, 6, 21), -- 18 places voiture Antaninarenina
(8, 6, 22),  -- 8 places moto Antaninarenina

-- Annonce 7 (Ampefiloha)
(15, 7, 25), -- 15 places voiture Ampefiloha
(10, 7, 26), -- 10 places moto Ampefiloha

-- Annonce 8 (67Ha)
(30, 8, 29), -- 30 places voiture 67Ha
(15, 8, 30); -- 15 places moto 67Ha

-- =====================================================================
-- ÉTAPE 7: NOTES DE PARKING (évaluations)
-- =====================================================================

INSERT INTO Parking_note (note, Id_Users, Id_Parking) VALUES
-- Notes pour Analakely (ID 1)
(4.5, 7, 1), -- Sitraka note Analakely
(4.0, 8, 1), -- Feno note Analakely
(5.0, 9, 1), -- Liva note Analakely

-- Notes pour Isoraka (ID 2)
(4.8, 7, 2), -- Sitraka note Isoraka
(4.2, 10, 2), -- Njaka note Isoraka

-- Notes pour Andraharo (ID 3)
(3.8, 8, 3), -- Feno note Andraharo
(4.0, 9, 3), -- Liva note Andraharo

-- Notes pour Behoririka (ID 4)
(4.1, 10, 4), -- Njaka note Behoririka
(3.9, 7, 4), -- Sitraka note Behoririka

-- Notes pour Ivandry (ID 5)
(4.9, 8, 5), -- Feno note Ivandry
(5.0, 9, 5), -- Liva note Ivandry

-- Notes pour Antaninarenina (ID 6)
(4.3, 10, 6), -- Njaka note Antaninarenina
(4.4, 7, 6); -- Sitraka note Antaninarenina

-- =====================================================================
-- ÉTAPE 8: RÉSERVATIONS (données de test variées)
-- =====================================================================

INSERT INTO Reservation (
    total_price, creation_date, payement_date, 
    start_datetime, end_datetime, payment_method, 
    Id_Users, Id_Reservation_status
) VALUES
-- Réservations terminées
(15000.00, CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP - INTERVAL '10 days', 
 CURRENT_TIMESTAMP - INTERVAL '8 days', CURRENT_TIMESTAMP - INTERVAL '7 days', 'espèces', 7, 4), -- Sitraka - Terminée
(25000.00, CURRENT_TIMESTAMP - INTERVAL '7 days', CURRENT_TIMESTAMP - INTERVAL '7 days', 
 CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '4 days', 'mobile money', 8, 4), -- Feno - Terminée
(18000.00, CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '5 days', 
 CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '2 days', 'carte bancaire', 9, 4), -- Liva - Terminée

-- Réservations en cours
(30000.00, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days', 
 CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP + INTERVAL '1 day', 'espèces', 10, 3), -- Njaka - En cours
(20000.00, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days', 
 CURRENT_TIMESTAMP - INTERVAL '12 hours', CURRENT_TIMESTAMP + INTERVAL '12 hours', 'mobile money', 7, 3), -- Sitraka - En cours

-- Réservations confirmées futures
(35000.00, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', 
 CURRENT_TIMESTAMP + INTERVAL '1 day', CURRENT_TIMESTAMP + INTERVAL '2 days', 'carte bancaire', 8, 2), -- Feno - Confirmée
(22000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
 CURRENT_TIMESTAMP + INTERVAL '2 days', CURRENT_TIMESTAMP + INTERVAL '3 days', 'espèces', 9, 2), -- Liva - Confirmée
(28000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
 CURRENT_TIMESTAMP + INTERVAL '3 days', CURRENT_TIMESTAMP + INTERVAL '4 days', 'mobile money', 10, 2), -- Njaka - Confirmée

-- Réservations en attente
(15000.00, CURRENT_TIMESTAMP - INTERVAL '12 hours', NULL, 
 CURRENT_TIMESTAMP + INTERVAL '4 days', CURRENT_TIMESTAMP + INTERVAL '5 days', NULL, 7, 1), -- Sitraka - En attente
(20000.00, CURRENT_TIMESTAMP - INTERVAL '6 hours', NULL, 
 CURRENT_TIMESTAMP + INTERVAL '5 days', CURRENT_TIMESTAMP + INTERVAL '6 days', NULL, 8, 1); -- Feno - En attente

-- =====================================================================
-- ÉTAPE 9: ASSOCIATIONS RÉSERVATION-VÉHICULES
-- =====================================================================

INSERT INTO Reservation_vehicles (numbers, Id_Reservation, Id_Announcements_vehicles) VALUES
-- Réservations terminées
(1, 1, 1), -- Sitraka - 1 voiture Analakely
(1, 2, 5), -- Feno - 1 voiture Isoraka
(1, 3, 9), -- Liva - 1 voiture Andraharo

-- Réservations en cours
(1, 4, 13), -- Njaka - 1 voiture Behoririka
(2, 5, 17), -- Sitraka - 2 places Ivandry

-- Réservations confirmées
(1, 6, 21), -- Feno - 1 voiture Antaninarenina
(1, 7, 25), -- Liva - 1 voiture Ampefiloha
(1, 8, 29), -- Njaka - 1 voiture 67Ha

-- Réservations en attente
(1, 9, 2), -- Sitraka - 1 moto Analakely
(1, 10, 6); -- Feno - 1 moto Isoraka

-- =====================================================================
-- ÉTAPE 10: COMMISSIONS REÇUES (résultats des réservations payées)
-- =====================================================================

INSERT INTO Commission_received (price, payement_date, Id_Commission_types, Id_Reservation, Id_Payment_status) VALUES
-- Commissions des réservations terminées
(1500.00, CURRENT_TIMESTAMP - INTERVAL '10 days', 1, 1, 1), -- 10% de 15000 - Sitraka
(2500.00, CURRENT_TIMESTAMP - INTERVAL '7 days', 1, 2, 1), -- 10% de 25000 - Feno
(1800.00, CURRENT_TIMESTAMP - INTERVAL '5 days', 1, 3, 1), -- 10% de 18000 - Liva

-- Commissions des réservations en cours
(3000.00, CURRENT_TIMESTAMP - INTERVAL '3 days', 1, 4, 1), -- 10% de 30000 - Njaka
(2000.00, CURRENT_TIMESTAMP - INTERVAL '2 days', 1, 5, 1), -- 10% de 20000 - Sitraka

-- Commissions des réservations confirmées
(3500.00, CURRENT_TIMESTAMP - INTERVAL '1 day', 1, 6, 1), -- 10% de 35000 - Feno
(2200.00, CURRENT_TIMESTAMP, 1, 7, 1), -- 10% de 22000 - Liva
(2800.00, CURRENT_TIMESTAMP, 1, 8, 1); -- 10% de 28000 - Njaka

-- =====================================================================
-- ÉTAPE 11: DISPONIBILITÉS (pour tester les fonctionnalités)
-- =====================================================================

-- Disponibilités par date
INSERT INTO Availabilities_date (start_hour, start_date, end_date, end_hour, Id_Reservation_vehicles, Id_Announcements_vehicles) VALUES
('08:00:00', CURRENT_DATE, CURRENT_DATE + INTERVAL '7 days', '18:00:00', 1, 3), -- Voiture Analakely réservée
('09:00:00', CURRENT_DATE + INTERVAL '1 day', CURRENT_DATE + INTERVAL '8 days', '17:00:00', 2, 5), -- Voiture Isoraka réservée
('08:30:00', CURRENT_DATE + INTERVAL '2 days', CURRENT_DATE + INTERVAL '9 days', '18:30:00', 3, 9), -- Voiture Andraharo réservée

-- Disponibilités restantes (non réservées)
('07:00:00', CURRENT_DATE, CURRENT_DATE + INTERVAL '30 days', '19:00:00', NULL, 7), -- Places libres Behoririka
('08:00:00', CURRENT_DATE, CURRENT_DATE + INTERVAL '30 days', '18:00:00', NULL, 11), -- Places libres Ivandry
('07:30:00', CURRENT_DATE, CURRENT_DATE + INTERVAL '30 days', '18:30:00', NULL, 15), -- Places libres Antaninarenina
('06:00:00', CURRENT_DATE, CURRENT_DATE + INTERVAL '30 days', '20:00:00', NULL, 19), -- Places libres Ampefiloha
('08:00:00', CURRENT_DATE, CURRENT_DATE + INTERVAL '30 days', '19:00:00', NULL, 23); -- Places libres 67Ha

-- Disponibilités par fréquence (récurrentes)
INSERT INTO Availabilities_frequence (start_hour, end_hour, Id_Reservation_vehicles, Id_Announcements_vehicles, Id_Days_week) VALUES
-- Parking Analakely - Lundi au Vendredi
('08:00:00', '18:00:00', NULL, 4, 1), -- Lundi
('08:00:00', '18:00:00', NULL, 4, 2), -- Mardi
('08:00:00', '18:00:00', NULL, 4, 3), -- Mercredi
('08:00:00', '18:00:00', NULL, 4, 4), -- Jeudi
('08:00:00', '18:00:00', NULL, 4, 5), -- Vendredi

-- Parking Isoraka - Lundi au Samedi
('07:30:00', '19:30:00', NULL, 8, 1), -- Lundi
('07:30:00', '19:30:00', NULL, 8, 2), -- Mardi
('07:30:00', '19:30:00', NULL, 8, 3), -- Mercredi
('07:30:00', '19:30:00', NULL, 8, 4), -- Jeudi
('07:30:00', '19:30:00', NULL, 8, 5), -- Vendredi
('09:00:00', '17:00:00', NULL, 8, 6), -- Samedi

-- Parking 67Ha - Tous les jours
('08:00:00', '20:00:00', NULL, 24, 1), -- Lundi
('08:00:00', '20:00:00', NULL, 24, 2), -- Mardi
('08:00:00', '20:00:00', NULL, 24, 3), -- Mercredi
('08:00:00', '20:00:00', NULL, 24, 4), -- Jeudi
('08:00:00', '20:00:00', NULL, 24, 5), -- Vendredi
('09:00:00', '21:00:00', NULL, 24, 6), -- Samedi
('10:00:00', '19:00:00', NULL, 24, 7); -- Dimanche

-- =====================================================================
-- CONFIRMATION D'INSERTION
-- =====================================================================

DO $$
BEGIN
    RAISE NOTICE '====================================================================';
    RAISE NOTICE 'DONNÉES DE TEST COMPLÈTES INSÉRÉES AVEC SUCCÈS';
    RAISE NOTICE '====================================================================';
    RAISE NOTICE '- Utilisateurs: 11 (2 admins, 4 propriétaires, 4 locataires, 1 partenaire)';
    RAISE NOTICE '- Parkings: 8 (répartis sur Antananarivo)';
    RAISE NOTICE '- Places de parking: 200+ (tous types de véhicules)';
    RAISE NOTICE '- Réservations: 10 (statuts variés pour tests)';
    RAISE NOTICE '- Commissions: 8 (pour tests dashboard)';
    RAISE NOTICE '- Disponibilités: configurées pour tests';
    RAISE NOTICE '';
    RAISE NOTICE 'La base est prête pour les tests complets MVP.';
    RAISE NOTICE '====================================================================';
END $$;

-- =====================================================================
-- RÉCAPITULATIF DES DONNÉES CRÉÉES
-- =====================================================================

/*
RÉSUMÉ DES DONNÉES DE TEST:

👥 UTILISATEURS (11):
- 2 Administrateurs: admin_rakoto, admin_marie
- 4 Propriétaires: andry_parking, nirina_owner, mialy_park, tiana_parking
- 4 Locataires: sitraka_client, feno_user, liva_client, njaka_user
- 1 Partenaire: partner_mada

🅿️ PARKINGS (8):
1. Analakely Centre Ville - 2500 Ar/h
2. Isoraka Business - 3000 Ar/h
3. Andraharo Résidentiel - 1800 Ar/h
4. Behoririka Marché - 2000 Ar/h
5. Ivandry Premium - 4000 Ar/h
6. Antaninarenina Commercial - 3500 Ar/h
7. Ampefiloha Résidentiel - 1500 Ar/h
8. 67Ha Shopping - 2800 Ar/h

 VÉHICULES (7 types):
Voiture, Moto, Utilitaire léger, Camionnette, Citadine, SUV, Vélo électrique

📋 RÉSERVATIONS (10):
- 3 Terminées
- 2 En cours
- 3 Confirmées
- 2 En attente

💰 COMMISSIONS:
- Globale: 10%
- Par véhicule: 5% à 15%
- Partenaire: 5%

⏰ DÉLAIS:
- Réservation minimum: 2 heures

📊 STATISTIQUES POUR DASHBOARD:
- Commissions générées: 18 300 Ar
- Réservations variées pour tests
- Notes parkings pour évaluations
- Disponibilités configurées
*/