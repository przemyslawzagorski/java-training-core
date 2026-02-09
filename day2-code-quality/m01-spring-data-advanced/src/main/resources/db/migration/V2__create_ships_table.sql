-- ============================================================================
-- V2: Tworzenie tabeli ships i relacji z pirates
-- ============================================================================
-- Autor: Przemek Zagorski
-- Data: 2026-02-09
-- Opis: Tabela statków i foreign key do pirates
-- ============================================================================

-- Sekwencja dla ID statków (wspiera batch processing!)
CREATE SEQUENCE ship_id_seq START WITH 100 INCREMENT BY 50;

CREATE TABLE ships (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50),
    cannons INT,
    version BIGINT NOT NULL DEFAULT 0,  -- Optimistic locking

    CONSTRAINT chk_ships_name_not_empty CHECK (LENGTH(TRIM(name)) > 0),
    CONSTRAINT chk_ships_cannons_range CHECK (cannons IS NULL OR (cannons >= 0 AND cannons <= 200))
);

-- Indeksy
CREATE INDEX idx_ships_type ON ships(type);
CREATE INDEX idx_ships_name ON ships(name);

-- Foreign Key: pirates.ship_id -> ships.id
ALTER TABLE pirates
    ADD CONSTRAINT fk_pirates_ship
    FOREIGN KEY (ship_id) REFERENCES ships(id)
    ON DELETE SET NULL;  -- Jeśli statek zostanie usunięty, pirat zostaje bez statku

-- Indeks na FK
CREATE INDEX idx_pirates_ship_id ON pirates(ship_id);

-- Komentarze
COMMENT ON TABLE ships IS 'Statki pirackie';
COMMENT ON COLUMN ships.cannons IS 'Liczba dział (0-200)';

