-- ========================================
-- Script de création de la table password_reset_tokens
-- ========================================
-- À exécuter dans Supabase SQL Editor

CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    code VARCHAR(6) NOT NULL,
    expiration_date TIMESTAMP NOT NULL,
    is_used BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP DEFAULT NOW() NOT NULL
);

-- Index pour accélérer les recherches
CREATE INDEX IF NOT EXISTS idx_password_reset_email ON password_reset_tokens(email);
CREATE INDEX IF NOT EXISTS idx_password_reset_code ON password_reset_tokens(email, code);
CREATE INDEX IF NOT EXISTS idx_password_reset_expiration ON password_reset_tokens(expiration_date);

-- Commentaire sur la table
COMMENT ON TABLE password_reset_tokens IS 'Stocke les codes de réinitialisation de mot de passe';
