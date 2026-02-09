-- ============================================================================
-- V1: Tworzenie tabeli pirates
-- ============================================================================
-- Autor: Przemek Zagorski
-- Data: 2026-02-09
-- Opis: Podstawowa tabela piratów z walidacją i optimistic locking
-- ============================================================================

-- Sekwencja dla ID piratów (wspiera batch processing!)
CREATE SEQUENCE pirate_id_seq START WITH 1000 INCREMENT BY 50;

CREATE TABLE pirates (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    nickname VARCHAR(100),  -- Przydomek pirata (opcjonalny)
    rank VARCHAR(50),
    bounty DECIMAL(15,2),
    version BIGINT NOT NULL DEFAULT 0,  -- Optimistic locking
    ship_id BIGINT,  -- FK do ships (dodane w V2)

    CONSTRAINT chk_pirates_name_not_empty CHECK (LENGTH(TRIM(name)) > 0),
    CONSTRAINT chk_pirates_bounty_positive CHECK (bounty IS NULL OR bounty >= 0)
);

-- Indeksy dla wydajności
CREATE INDEX idx_pirates_rank ON pirates(rank);
CREATE INDEX idx_pirates_bounty ON pirates(bounty DESC);
CREATE INDEX idx_pirates_name ON pirates(name);

-- Komentarze dla dokumentacji
COMMENT ON TABLE pirates IS 'Piraci - główna tabela z danymi piratów';
COMMENT ON COLUMN pirates.version IS 'Wersja dla optimistic locking (@Version)';
COMMENT ON COLUMN pirates.bounty IS 'Nagroda za głowę pirata';

