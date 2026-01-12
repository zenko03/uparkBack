CREATE TABLE Users(
   Id_Users SERIAL,
   name VARCHAR(100)  NOT NULL,
   first_name VARCHAR(100)  NOT NULL,
   user_name VARCHAR(100)  NOT NULL,
   email VARCHAR(100)  NOT NULL,
   password TEXT,
   phone_number VARCHAR(50)  NOT NULL,
   role VARCHAR(50),
   oauth_provider VARCHAR(20),
   oauth_id VARCHAR(255),
   profile_picture_url TEXT,
   email_verified BOOLEAN DEFAULT FALSE,
   PRIMARY KEY(Id_Users),
   UNIQUE(user_name),
   UNIQUE(email)
);

CREATE TABLE Parking(
   Id_Parking SERIAL,
   label VARCHAR(100)  NOT NULL,
   hourly_rate NUMERIC(15,2)   NOT NULL,
   description TEXT NOT NULL,
   localisation GEOGRAPHY NOT NULL,
   Id_Users INTEGER,
   PRIMARY KEY(Id_Parking),
   FOREIGN KEY(Id_Users) REFERENCES Users(Id_Users)
);

CREATE TABLE Vehicles(
   Id_Vehicles SERIAL,
   types VARCHAR(50)  NOT NULL,
   icon VARCHAR(50)  NOT NULL,
   PRIMARY KEY(Id_Vehicles)
);

CREATE TABLE Parking_vehicles(
   Id_Parking_vehicles SERIAL,
   numbers INTEGER NOT NULL,
   Id_Vehicles INTEGER,
   Id_Parking INTEGER,
   PRIMARY KEY(Id_Parking_vehicles),
   FOREIGN KEY(Id_Vehicles) REFERENCES Vehicles(Id_Vehicles),
   FOREIGN KEY(Id_Parking) REFERENCES Parking(Id_Parking)
);

CREATE TABLE Announcements(
   Id_Announcements SERIAL,
   description TEXT NOT NULL,
   creation_date TIMESTAMP NOT NULL,
   is_published BOOLEAN NOT NULL DEFAULT false,  
   Id_Parking INTEGER, 
   PRIMARY KEY(Id_Announcements),
   FOREIGN KEY(Id_Parking) REFERENCES Parking(Id_Parking)
);

CREATE TABLE Days_week(
   Id_Days_week SERIAL,
   day_name VARCHAR(50)  NOT NULL,
   PRIMARY KEY(Id_Days_week)
);

CREATE TABLE Reservation_status(
   Id_Reservation_status SERIAL,
   label VARCHAR(50)  NOT NULL,
   value_ INTEGER NOT NULL,
   PRIMARY KEY(Id_Reservation_status)
);

CREATE TABLE Announcements_vehicles(
   Id_Announcements_vehicles SERIAL,
   numbers INTEGER NOT NULL,
   Id_Announcements INTEGER,
   Id_Parking_vehicles INTEGER,
   PRIMARY KEY(Id_Announcements_vehicles),
   FOREIGN KEY(Id_Announcements) REFERENCES Announcements(Id_Announcements),
   FOREIGN KEY(Id_Parking_vehicles) REFERENCES Parking_vehicles(Id_Parking_vehicles)
);

CREATE TABLE Reservation_delay(
   Id_Reservation_delay SERIAL,
   delay_in_hours INTEGER NOT NULL,
   delay_in_minutes INTEGER NOT NULL,
   creation_date TIMESTAMP,
   PRIMARY KEY(Id_Reservation_delay)
);

CREATE TABLE Global_commission(
   Id_Global_commission SERIAL,
   rate DOUBLE PRECISION,
   creation_date TIMESTAMP,
   PRIMARY KEY(Id_Global_commission)
);

CREATE TABLE Commission_vehicles(
   Id_Commission_vehicles SERIAL,
   rate DOUBLE PRECISION,
   creation_date TIMESTAMP,
   Id_Vehicles INTEGER,
   PRIMARY KEY(Id_Commission_vehicles),
   FOREIGN KEY(Id_Vehicles) REFERENCES Vehicles(Id_Vehicles)
);

CREATE TABLE Commission_partners(
   Id_Commission_partners SERIAL,
   rate DOUBLE PRECISION NOT NULL,
   creation_date TIMESTAMP,
   Id_Users INTEGER,
   PRIMARY KEY(Id_Commission_partners),
   FOREIGN KEY(Id_Users) REFERENCES Users(Id_Users)
);

CREATE TABLE Profil_types(
   Id_Profil_types SERIAL,
   types VARCHAR(50)  NOT NULL,
   PRIMARY KEY(Id_Profil_types)
);

CREATE TABLE Parking_note(
   Id_Parking_note SERIAL,
   note NUMERIC(6,2)   NOT NULL,
   Id_Users INTEGER,
   Id_Parking INTEGER,
   PRIMARY KEY(Id_Parking_note),
   FOREIGN KEY(Id_Users) REFERENCES Users(Id_Users),
   FOREIGN KEY(Id_Parking) REFERENCES Parking(Id_Parking)
);

CREATE TABLE Payment_status(
   Id_Payment_status SERIAL,
   label VARCHAR(50)  NOT NULL,
   value_ INTEGER NOT NULL,
   PRIMARY KEY(Id_Payment_status)
);

CREATE TABLE Commission_types(
   Id_Commission_types SERIAL,
   label VARCHAR(50) ,
   PRIMARY KEY(Id_Commission_types)
);

CREATE TABLE Profils(
   Id_Profils SERIAL,
   Id_Profil_types INTEGER,
   Id_Users INTEGER,
   PRIMARY KEY(Id_Profils),
   FOREIGN KEY(Id_Profil_types) REFERENCES Profil_types(Id_Profil_types),
   FOREIGN KEY(Id_Users) REFERENCES Users(Id_Users)
);

CREATE TABLE Reservation(
   Id_Reservation SERIAL,
   total_price NUMERIC(15,2)   NOT NULL,
   creation_date TIMESTAMP NOT NULL,
   payement_date TIMESTAMP,
   start_datetime TIMESTAMP NOT NULL,
   end_datetime TIMESTAMP NOT NULL,
   payment_method VARCHAR(50),
   Id_Users INTEGER,
   Id_Reservation_status INTEGER,
   PRIMARY KEY(Id_Reservation),
   FOREIGN KEY(Id_Users) REFERENCES Users(Id_Users),
   FOREIGN KEY(Id_Reservation_status) REFERENCES Reservation_status(Id_Reservation_status)
);

CREATE TABLE Reservation_vehicles(
   Id_Reservation_vehicles SERIAL,
   numbers INTEGER NOT NULL,
   Id_Reservation INTEGER,
   Id_Announcements_vehicles INTEGER,
   PRIMARY KEY(Id_Reservation_vehicles),
   FOREIGN KEY(Id_Reservation) REFERENCES Reservation(Id_Reservation),
   FOREIGN KEY(Id_Announcements_vehicles) REFERENCES Announcements_vehicles(Id_Announcements_vehicles)
);

CREATE TABLE Commission_received(
   Id_Commission_received SERIAL,
   price NUMERIC(15,2)  ,
   payement_date TIMESTAMP,
   Id_Commission_types INTEGER,
   Id_Reservation INTEGER,
   Id_Payment_status INTEGER,
   PRIMARY KEY(Id_Commission_received),
   FOREIGN KEY(Id_Commission_types) REFERENCES Commission_types(Id_Commission_types),
   FOREIGN KEY(Id_Reservation) REFERENCES Reservation(Id_Reservation),
   FOREIGN KEY(Id_Payment_status) REFERENCES Payment_status(Id_Payment_status)
);

CREATE TABLE Availabilities_date(
   Id_Availabilities_date SERIAL,
   start_hour TIME NOT NULL,
   start_date DATE NOT NULL,
   end_date DATE NOT NULL,
   end_hour TIME NOT NULL,
   Id_Reservation_vehicles INTEGER,
   Id_Announcements_vehicles INTEGER,
   PRIMARY KEY(Id_Availabilities_date),
   FOREIGN KEY(Id_Reservation_vehicles) REFERENCES Reservation_vehicles(Id_Reservation_vehicles),
   FOREIGN KEY(Id_Announcements_vehicles) REFERENCES Announcements_vehicles(Id_Announcements_vehicles)
);

CREATE TABLE Availabilities_frequence(
   Id_Availabilities_frequence SERIAL,
   start_hour TIME NOT NULL,
   end_hour TIME NOT NULL,
   Id_Reservation_vehicles INTEGER,
   Id_Announcements_vehicles INTEGER,
   Id_Days_week INTEGER,
   PRIMARY KEY(Id_Availabilities_frequence),
   FOREIGN KEY(Id_Reservation_vehicles) REFERENCES Reservation_vehicles(Id_Reservation_vehicles),
   FOREIGN KEY(Id_Announcements_vehicles) REFERENCES Announcements_vehicles(Id_Announcements_vehicles),
   FOREIGN KEY(Id_Days_week) REFERENCES Days_week(Id_Days_week)
);

create table public.reservation_requests (
  id bigint generated by default as identity not null,
  created_at timestamp with time zone not null default now(),
  start_datetime timestamp with time zone null,
  end_datetime timestamp with time zone null,
  total_gain real null,
  updated_at timestamp with time zone null default now(),
  state smallint null,
  accepted_at timestamp with time zone null,
  expires_at timestamp with time zone null,
  id_announcement integer null,
  id_requester integer null,
  constraint reservation_requests_pkey primary key (id),
  constraint reservation_requests_id_announcement_fkey foreign KEY (id_announcement) references announcements (id_announcements),
  constraint reservation_requests_id_requester_fkey foreign KEY (id_requester) references users (id_users)
) TABLESPACE pg_default;

-- Table de liaison pour les véhicules sélectionnés dans les demandes de réservation
CREATE TABLE reservation_request_vehicles (
   Id_Reservation_request_vehicles SERIAL,
   numbers INTEGER NOT NULL,
   id_reservation_request BIGINT NOT NULL,
   Id_Announcements_vehicles INTEGER NOT NULL,
   PRIMARY KEY(Id_Reservation_request_vehicles),
   FOREIGN KEY(id_reservation_request) REFERENCES reservation_requests(id) ON DELETE CASCADE,
   FOREIGN KEY(Id_Announcements_vehicles) REFERENCES Announcements_vehicles(Id_Announcements_vehicles) ON DELETE CASCADE,
   UNIQUE(id_reservation_request, Id_Announcements_vehicles),
   CHECK (numbers > 0)
);

create table public.user_note (
  id bigint generated by default as identity not null,
  created_at timestamp with time zone not null default now(),
  note smallint null,
  cleanliness boolean null,
  precision boolean null,
  communication boolean null,
  security boolean null,
  id_parking integer null,
  description text null,
  id_user integer null,
  constraint user_note_pkey primary key (id),
  constraint user_note_id_parking_fkey foreign KEY (id_parking) references parking (id_parking) on update CASCADE on delete CASCADE,
  constraint user_note_id_user_fkey foreign KEY (id_user) references users (id_users)
) TABLESPACE pg_default;

CREATE VIEW v_global_parking_note 
AS SELECT parking.id_parking, parking.label, AVG(user_note.note) 
AS average FROM user_note INNER JOIN parking 
ON user_note.id_parking=parking.id_parking GROUP BY parking.id_parking;

CREATE VIEW v_global_user_note AS 
SELECT id_users, AVG(average) FROM v_global_parking_note 
GROUP BY id_users;

-- Vue pour récupérer les réservations avec les informations du parking
-- Modifiée pour gérer le cas où reservation_vehicles n'existe pas encore
CREATE OR REPLACE VIEW v_user_reservations AS
SELECT 
    r.id_reservation,
    r.total_price,
    r.creation_date,
    r.payement_date AS payment_date,
    r.start_datetime,
    r.end_datetime,
    r.payment_method,
    r.id_users,
    
    -- Informations parking (via reservation_vehicles si disponible, sinon null)
    MIN(p.id_parking) AS parking_id,
    MIN(p.label) AS parking_name,
    MIN(CAST(p.localisation AS TEXT)) AS parking_address,
    
    -- Statut
    COALESCE(MIN(rs.label), 'En attente') AS status_label,
    COALESCE(MIN(rs.value_), 10) AS status_value
    
FROM reservation r
LEFT JOIN reservation_vehicles rv ON r.id_reservation = rv.id_reservation
LEFT JOIN announcements_vehicles av ON rv.id_announcements_vehicles = av.id_announcements_vehicles
LEFT JOIN parking_vehicles pv ON av.id_parking_vehicles = pv.id_parking_vehicles
LEFT JOIN parking p ON pv.id_parking = p.id_parking
LEFT JOIN reservation_status rs ON r.id_reservation_status = rs.id_reservation_status
GROUP BY 
    r.id_reservation,
    r.total_price,
    r.creation_date,
    r.payement_date,
    r.start_datetime,
    r.end_datetime,
    r.payment_method,
    r.id_users;


ALTER TABLE reservation_requests 
ADD COLUMN IF NOT EXISTS id_requester INTEGER;

-- 2. Ajouter accepted_at (date d'acceptation par propriétaire)
ALTER TABLE reservation_requests 
ADD COLUMN IF NOT EXISTS accepted_at TIMESTAMP WITH TIME ZONE;

-- 3. Ajouter expires_at (date limite de paiement - 24h après acceptation)
ALTER TABLE reservation_requests 
ADD COLUMN IF NOT EXISTS expires_at TIMESTAMP WITH TIME ZONE;

-- 4. Ajouter la contrainte de clé étrangère pour id_requester
ALTER TABLE reservation_requests 
ADD CONSTRAINT  reservation_requests_id_requester_fkey 
FOREIGN KEY (id_requester) REFERENCES users (id_users);

-- ========================================
-- MIGRATION: Authentification Sociale OAuth2
-- ========================================
-- À exécuter dans Supabase SQL Editor pour ajouter le support OAuth2

-- 1. Ajouter la colonne oauth_provider (google, facebook, apple)
ALTER TABLE Users 
ADD COLUMN IF NOT EXISTS oauth_provider VARCHAR(20);

-- 2. Ajouter la colonne oauth_id (ID unique du provider)
ALTER TABLE Users 
ADD COLUMN IF NOT EXISTS oauth_id VARCHAR(255);

-- 3. Ajouter la colonne profile_picture_url (photo de profil)
ALTER TABLE Users 
ADD COLUMN IF NOT EXISTS profile_picture_url TEXT;

-- 4. Ajouter la colonne email_verified (email vérifié automatiquement avec OAuth)
ALTER TABLE Users 
ADD COLUMN IF NOT EXISTS email_verified BOOLEAN DEFAULT FALSE;

-- 5. Modifier la colonne password pour autoriser NULL (users OAuth n'ont pas de password)
ALTER TABLE Users 
ALTER COLUMN password DROP NOT NULL;

-- 6. Ajouter contrainte UNIQUE sur email (éviter duplicata)
ALTER TABLE Users 
ADD CONSTRAINT users_email_unique UNIQUE (email);

-- 7. Ajouter contrainte UNIQUE composite pour oauth_provider + oauth_id
ALTER TABLE Users 
ADD CONSTRAINT users_oauth_unique UNIQUE (oauth_provider, oauth_id);

-- 8. Créer un index pour recherche rapide par oauth
CREATE INDEX IF NOT EXISTS idx_users_oauth 
ON Users(oauth_provider, oauth_id) 
WHERE oauth_provider IS NOT NULL;

-- 9. Créer un index pour recherche par email
CREATE INDEX IF NOT EXISTS idx_users_email 
ON Users(email);

-- 10. Mettre à jour les utilisateurs existants (email vérifié pour comptes classiques)
UPDATE Users 
SET email_verified = TRUE 
WHERE oauth_provider IS NULL AND password IS NOT NULL;

create table public.disputes (
  id bigint generated by default as identity not null,
  created_at timestamp with time zone not null default now(),
  motif text not null,
  description text null,
  constraint disputes_pkey primary key (id)
) TABLESPACE pg_default;

create table public.disputes_proofs (
  id bigint generated by default as identity not null,
  proof_url text null,
  id_dispute bigint null,
  constraint disputes_proofs_pkey primary key (id),
  constraint disputes_proofs_id_dispute_fkey foreign KEY (id_dispute) references disputes (id)
) TABLESPACE pg_default;