
SET session_replication_role = replica;


DELETE FROM commission_received;
DELETE FROM reservation_vehicles;
DELETE FROM availabilities_date;
DELETE FROM availabilities_frequence;
DELETE FROM announcements_vehicles;

-- Tables principales dépendantes
DELETE FROM reservation;
DELETE FROM parking_note;
DELETE FROM parking_vehicles;
DELETE FROM announcements;

-- Tables de configuration et statuts
DELETE FROM commission_partners;
DELETE FROM commission_vehicles;
DELETE FROM global_commission;
DELETE FROM reservation_delay;
DELETE FROM commission_types;
DELETE FROM payment_status;
DELETE FROM reservation_status;

-- Tables de profils et utilisateurs
DELETE FROM profils;
DELETE FROM users;

-- Tables de référence (sans dépendances)
DELETE FROM profil_types;
DELETE FROM days_week;
DELETE FROM vehicles;
DELETE FROM parking;

-- =====================================================================
-- Réinitialisation des séquences (primary keys)
-- =====================================================================

-- Réinitialiser toutes les séquences à 1
ALTER SEQUENCE users_id_users_seq RESTART WITH 1;
ALTER SEQUENCE parking_id_parking_seq RESTART WITH 1;
ALTER SEQUENCE vehicles_id_vehicles_seq RESTART WITH 1;
ALTER SEQUENCE parking_vehicles_id_parking_vehicles_seq RESTART WITH 1;
ALTER SEQUENCE announcements_id_announcements_seq RESTART WITH 1;
ALTER SEQUENCE days_week_id_days_week_seq RESTART WITH 1;
ALTER SEQUENCE reservation_status_id_reservation_status_seq RESTART WITH 1;
ALTER SEQUENCE announcements_vehicles_id_announcements_vehicles_seq RESTART WITH 1;
ALTER SEQUENCE reservation_delay_id_reservation_delay_seq RESTART WITH 1;
ALTER SEQUENCE global_commission_id_global_commission_seq RESTART WITH 1;
ALTER SEQUENCE commission_vehicles_id_commission_vehicles_seq RESTART WITH 1;
ALTER SEQUENCE commission_partners_id_commission_partners_seq RESTART WITH 1;
ALTER SEQUENCE profil_types_id_profil_types_seq RESTART WITH 1;
ALTER SEQUENCE parking_note_id_parking_note_seq RESTART WITH 1;
ALTER SEQUENCE payment_status_id_payment_status_seq RESTART WITH 1;
ALTER SEQUENCE commission_types_id_commission_types_seq RESTART WITH 1;
ALTER SEQUENCE profils_id_profils_seq RESTART WITH 1;
ALTER SEQUENCE reservation_id_reservation_seq RESTART WITH 1;
ALTER SEQUENCE reservation_vehicles_id_reservation_vehicles_seq RESTART WITH 1;
ALTER SEQUENCE commission_received_id_commission_received_seq RESTART WITH 1;
ALTER SEQUENCE availabilities_date_id_availabilities_date_seq RESTART WITH 1;
ALTER SEQUENCE availabilities_frequence_id_availabilities_frequence_seq RESTART WITH 1;

-- =====================================================================
-- ÉTAPE 3: Réactivation des contraintes
-- =====================================================================

SET session_replication_role = DEFAULT;

-- =====================================================================
-- ÉTAPE 4: Vérification du nettoyage
-- =====================================================================

-- Vérifier que toutes les tables sont vides
SELECT 
    schemaname,
    tablename,
    n_tup_ins as total_inserts,
    n_tup_upd as total_updates,
    n_tup_del as total_deletes,
    n_live_tup as current_rows
FROM pg_stat_user_tables 
WHERE schemaname = 'public'
ORDER BY tablename;

-- Afficher le statut des séquences
SELECT 
    sequence_name,
    last_value,
    start_value,
    increment_by,
    max_value,
    min_value,
    cache_value,
    log_cnt,
    is_cycled,
    is_called
FROM information_schema.sequences 
WHERE sequence_schema = 'public'
ORDER BY sequence_name;

-- =====================================================================
-- ÉTAPE 5: Confirmation
-- =====================================================================

DO $$
BEGIN
    RAISE NOTICE '====================================================================';
    RAISE NOTICE 'BASE DE DONNÉES UPARK NETTOYÉE AVEC SUCCÈS';
    RAISE NOTICE '====================================================================';
    RAISE NOTICE '- Toutes les données ont été supprimées';
    RAISE NOTICE '- Toutes les séquences ont été réinitialisées à 1';
    RAISE NOTICE '- Les contraintes ont été réactivées';
    RAISE NOTICE '';
    RAISE NOTICE 'La base est prête pour l''insertion des nouvelles données de test.';
    RAISE NOTICE '====================================================================';
END $$;

-- =====================================================================
-- INSTRUCTIONS D'UTILISATION
-- =====================================================================

/*
1. Exécuter ce script dans PostgreSQL:
   psql -d nom_base -f cleanup-database.sql

2. Ou via l'interface SQL de votre choix (pgAdmin, DBeaver, etc.)

3. Attendre la confirmation du nettoyage complet

4. Exécuter ensuite le script des nouvelles données de test

ATTENTION:
- Ce script supprime IRRÉVERSIBLEMENT toutes les données
- À utiliser uniquement en environnement de développement/test
- Vérifier que vous êtes sur la bonne base de données avant exécution
*/