-- =====================================================================
-- SCRIPT DE NETTOYAGE: Supprimer les doublons
-- =====================================================================
-- Ce script nettoie les doublons dans les tables de référence
-- qui peuvent causer des erreurs "Query did not return a unique result"
-- =====================================================================

-- 1. Nettoyer les doublons dans reservation_status
-- Garder uniquement le premier enregistrement de chaque valeur
DELETE FROM reservation_status
WHERE id_reservation_status NOT IN (
    SELECT MIN(id_reservation_status)
    FROM reservation_status
    GROUP BY value_
);

-- 2. Nettoyer les doublons dans payment_status
DELETE FROM payment_status
WHERE id_payment_status NOT IN (
    SELECT MIN(id_payment_status)
    FROM payment_status
    GROUP BY value_
);

-- 3. Nettoyer les doublons dans days_week (si présents)
DELETE FROM days_week
WHERE id_days_week NOT IN (
    SELECT MIN(id_days_week)
    FROM days_week
    GROUP BY day_name
);

-- 4. Nettoyer les doublons dans vehicles
DELETE FROM vehicles
WHERE id_vehicles NOT IN (
    SELECT MIN(id_vehicles)
    FROM vehicles
    GROUP BY types
);

-- 5. Nettoyer les doublons dans commission_types
DELETE FROM commission_types
WHERE id_commission_types NOT IN (
    SELECT MIN(id_commission_types)
    FROM commission_types
    GROUP BY label
);

-- 6. Nettoyer les doublons dans profil_types
DELETE FROM profil_types
WHERE id_profil_types NOT IN (
    SELECT MIN(id_profil_types)
    FROM profil_types
    GROUP BY types
);

-- Afficher le résumé après nettoyage
DO $$
DECLARE
    rs_count INTEGER;
    ps_count INTEGER;
    dw_count INTEGER;
    v_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO rs_count FROM reservation_status;
    SELECT COUNT(*) INTO ps_count FROM payment_status;
    SELECT COUNT(*) INTO dw_count FROM days_week;
    SELECT COUNT(*) INTO v_count FROM vehicles;
    
    RAISE NOTICE '====================================================================';
    RAISE NOTICE 'NETTOYAGE DES DOUBLONS TERMINÉ';
    RAISE NOTICE '====================================================================';
    RAISE NOTICE 'Statuts de réservation: % enregistrements', rs_count;
    RAISE NOTICE 'Statuts de paiement: % enregistrements', ps_count;
    RAISE NOTICE 'Jours de la semaine: % enregistrements', dw_count;
    RAISE NOTICE 'Types de véhicules: % enregistrements', v_count;
    RAISE NOTICE '====================================================================';
END $$;

-- Vérifier qu'il n'y a plus de doublons
SELECT 'VERIFICATION - Doublons restants:' as info;
SELECT 
    'reservation_status' as table_name, 
    value_, 
    COUNT(*) as nb_duplicates 
FROM reservation_status 
GROUP BY value_ 
HAVING COUNT(*) > 1
UNION ALL
SELECT 
    'payment_status', 
    value_::text, 
    COUNT(*) 
FROM payment_status 
GROUP BY value_ 
HAVING COUNT(*) > 1
UNION ALL
SELECT 
    'days_week', 
    day_name, 
    COUNT(*) 
FROM days_week 
GROUP BY day_name 
HAVING COUNT(*) > 1
UNION ALL
SELECT 
    'vehicles', 
    types, 
    COUNT(*) 
FROM vehicles 
GROUP BY types 
HAVING COUNT(*) > 1;
