-- ========================================
-- SCRIPT D'INITIALISATION NOTIFICATIONS PUSH
-- À exécuter dans Supabase SQL Editor
-- ========================================

-- 1. Créer la table Device_tokens
CREATE TABLE IF NOT EXISTS Device_tokens (
   Id_Device_token SERIAL PRIMARY KEY,
   token TEXT NOT NULL,
   platform VARCHAR(10) NOT NULL CHECK (platform IN ('android', 'ios')),
   device_info TEXT,
   is_active BOOLEAN DEFAULT TRUE,
   created_at TIMESTAMP DEFAULT NOW(),
   updated_at TIMESTAMP DEFAULT NOW(),
   Id_Users INTEGER NOT NULL REFERENCES Users(Id_Users) ON DELETE CASCADE,
   UNIQUE(Id_Users, token)
);

-- 2. Créer la table Notifications
CREATE TABLE IF NOT EXISTS Notifications (
   Id_Notification SERIAL PRIMARY KEY,
   title VARCHAR(255) NOT NULL,
   message TEXT NOT NULL,
   type VARCHAR(50) NOT NULL CHECK (type IN ('reservation_accepted', 'reservation_rejected', 'reservation_cancelled', 'payment_confirmed', 'payment_failed', 'new_request', 'reminder', 'system')),
   data JSONB,
   read BOOLEAN DEFAULT FALSE,
   sent_at TIMESTAMP DEFAULT NOW(),
   read_at TIMESTAMP,
   Id_Users INTEGER NOT NULL REFERENCES Users(Id_Users) ON DELETE CASCADE,
   Id_Reservation INTEGER REFERENCES Reservation(Id_Reservation) ON DELETE SET NULL,
   id_reservation_request BIGINT REFERENCES reservation_requests(id) ON DELETE SET NULL
);

-- 3. Créer les index pour performance
CREATE INDEX IF NOT EXISTS idx_device_tokens_user 
ON Device_tokens(Id_Users, is_active) 
WHERE is_active = TRUE;

CREATE INDEX IF NOT EXISTS idx_device_tokens_platform 
ON Device_tokens(platform, is_active) 
WHERE is_active = TRUE;

CREATE INDEX IF NOT EXISTS idx_notifications_user_read_sent 
ON Notifications(Id_Users, read, sent_at DESC);

CREATE INDEX IF NOT EXISTS idx_notifications_type 
ON Notifications(type, sent_at DESC);

CREATE INDEX IF NOT EXISTS idx_notifications_reservation 
ON Notifications(Id_Reservation) 
WHERE Id_Reservation IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_notifications_request 
ON Notifications(id_reservation_request) 
WHERE id_reservation_request IS NOT NULL;

-- 4. Fonction pour nettoyer les anciennes notifications (> 90 jours)
CREATE OR REPLACE FUNCTION delete_old_notifications()
RETURNS void AS $$
BEGIN
    DELETE FROM Notifications 
    WHERE sent_at < NOW() - INTERVAL '90 days';
END;
$$ LANGUAGE plpgsql;

-- 5. Trigger pour mettre à jour updated_at automatiquement
CREATE OR REPLACE FUNCTION update_device_token_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_device_token_timestamp
BEFORE UPDATE ON Device_tokens
FOR EACH ROW
EXECUTE FUNCTION update_device_token_timestamp();

-- 6. Vue pour statistiques notifications par utilisateur
CREATE OR REPLACE VIEW v_user_notification_stats AS
SELECT 
    Id_Users,
    COUNT(*) AS total_notifications,
    COUNT(*) FILTER (WHERE read = FALSE) AS unread_count,
    COUNT(*) FILTER (WHERE type = 'reservation_accepted') AS accepted_count,
    COUNT(*) FILTER (WHERE type = 'new_request') AS new_request_count,
    MAX(sent_at) AS last_notification_at
FROM Notifications
GROUP BY Id_Users;

-- 7. Activer Realtime sur la table Notifications (pour synchronisation temps réel)
ALTER PUBLICATION supabase_realtime ADD TABLE Notifications;

--  SCRIPT TERMINÉ
-- Les tables sont créées et prêtes à l'emploi !
