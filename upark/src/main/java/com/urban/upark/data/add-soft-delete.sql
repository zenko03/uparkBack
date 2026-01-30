-- ========================================
-- MIGRATION: Soft Delete
-- ========================================

-- Parking
ALTER TABLE Parking 
ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE,
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW(),
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT NOW(),
ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP NULL,
ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN DEFAULT FALSE;

ALTER TABLE Parking ADD COLUMN IF NOT EXISTS address VARCHAR(500);

-- Announcements
ALTER TABLE Announcements 
ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP NULL,
ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN DEFAULT FALSE;

-- Reservation
ALTER TABLE Reservation 
ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP NULL,
ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN DEFAULT FALSE;

-- User_note
ALTER TABLE User_note 
ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP NULL,
ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN DEFAULT FALSE;

-- Parking_vehicles
ALTER TABLE Parking_vehicles 
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW(),
ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP NULL,
ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN DEFAULT FALSE;

-- Announcements_vehicles
ALTER TABLE Announcements_vehicles 
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW(),
ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP NULL,
ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN DEFAULT FALSE;

-- Availabilities_date
ALTER TABLE Availabilities_date 
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW(),
ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP NULL,
ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN DEFAULT FALSE;

-- Availabilities_frequence
ALTER TABLE Availabilities_frequence 
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW(),
ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP NULL,
ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN DEFAULT FALSE;

-- Parking_images
ALTER TABLE Parking_images 
ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP NULL,
ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN DEFAULT FALSE;

-- Reservation_requests
ALTER TABLE Reservation_requests 
ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP NULL,
ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN DEFAULT FALSE;

-- Disputes (jamais supprimer - audit légal)
ALTER TABLE Disputes 
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT NOW();

-- Index pour performance sur les requêtes avec soft delete
CREATE INDEX IF NOT EXISTS idx_parking_active ON Parking(is_deleted) WHERE is_deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_announcements_active ON Announcements(is_deleted) WHERE is_deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_reservation_active ON Reservation(is_deleted) WHERE is_deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_user_note_active ON User_note(is_deleted) WHERE is_deleted = FALSE;
