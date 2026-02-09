-- ============================================================================
-- V3: Dane testowe
-- ============================================================================
-- Autor: Przemek Zagorski
-- Data: 2026-02-09
-- Opis: Przykładowe dane do demonstracji funkcjonalności
-- ============================================================================

-- Statki
INSERT INTO ships (id, name, type, cannons, version) VALUES
    (1, 'Black Pearl', 'Galleon', 32, 0),
    (2, 'Flying Dutchman', 'Galleon', 48, 0),
    (3, 'Queen Anne''s Revenge', 'Frigate', 40, 0),
    (4, 'Interceptor', 'Brig', 16, 0),
    (5, 'Jolly Roger', 'Sloop', 8, 0);

-- Piraci
INSERT INTO pirates (id, name, rank, bounty, ship_id, version) VALUES
    -- Załoga Black Pearl
    (1, 'Jack Sparrow', 'Captain', 100000.00, 1, 0),
    (2, 'Joshamee Gibbs', 'First Mate', 20000.00, 1, 0),
    (3, 'Cotton', 'Sailor', 5000.00, 1, 0),
    (4, 'Marty', 'Sailor', 4000.00, 1, 0),
    
    -- Załoga Flying Dutchman
    (5, 'Davy Jones', 'Captain', 150000.00, 2, 0),
    (6, 'Bootstrap Bill', 'Sailor', 15000.00, 2, 0),
    (7, 'Maccus', 'First Mate', 25000.00, 2, 0),
    
    -- Załoga Queen Anne's Revenge
    (8, 'Blackbeard', 'Captain', 200000.00, 3, 0),
    (9, 'Angelica', 'First Mate', 30000.00, 3, 0),
    
    -- Załoga Interceptor
    (10, 'Will Turner', 'Captain', 50000.00, 4, 0),
    (11, 'Elizabeth Swann', 'First Mate', 45000.00, 4, 0),
    
    -- Piraci bez statku
    (12, 'Hector Barbossa', 'Captain', 80000.00, NULL, 0),
    (13, 'Ragetti', 'Sailor', 3000.00, NULL, 0),
    (14, 'Pintel', 'Sailor', 3000.00, NULL, 0);

-- Dodatkowi piraci dla testów paginacji (15-50)
-- Używamy prostych INSERT VALUES zamiast DUAL CONNECT BY (kompatybilność H2)
-- Musimy podać ID explicite, bo auto-increment zaczyna od 1
INSERT INTO pirates (id, name, rank, bounty, ship_id, version) VALUES
    (15, 'Pirate 15', 'First Mate', 1500.00, 1, 0),
    (16, 'Pirate 16', 'Sailor', 1600.00, NULL, 0),
    (17, 'Pirate 17', 'Sailor', 1700.00, 1, 0),
    (18, 'Pirate 18', 'Sailor', 1800.00, NULL, 0),
    (19, 'Pirate 19', 'Sailor', 1900.00, NULL, 0),
    (20, 'Pirate 20', 'Captain', 2000.00, 1, 0),
    (21, 'Pirate 21', 'Sailor', 2100.00, NULL, 0),
    (22, 'Pirate 22', 'Sailor', 2200.00, 1, 0),
    (23, 'Pirate 23', 'Sailor', 2300.00, NULL, 0),
    (24, 'Pirate 24', 'Sailor', 2400.00, 1, 0),
    (25, 'Pirate 25', 'First Mate', 2500.00, 1, 0),
    (26, 'Pirate 26', 'Sailor', 2600.00, NULL, 0),
    (27, 'Pirate 27', 'Sailor', 2700.00, 1, 0),
    (28, 'Pirate 28', 'Sailor', 2800.00, NULL, 0),
    (29, 'Pirate 29', 'Sailor', 2900.00, NULL, 0),
    (30, 'Pirate 30', 'Captain', 3000.00, 1, 0),
    (31, 'Pirate 31', 'Sailor', 3100.00, NULL, 0),
    (32, 'Pirate 32', 'Sailor', 3200.00, NULL, 0),
    (33, 'Pirate 33', 'Sailor', 3300.00, 1, 0),
    (34, 'Pirate 34', 'Sailor', 3400.00, NULL, 0),
    (35, 'Pirate 35', 'First Mate', 3500.00, 1, 0),
    (36, 'Pirate 36', 'Sailor', 3600.00, NULL, 0),
    (37, 'Pirate 37', 'Sailor', 3700.00, 1, 0),
    (38, 'Pirate 38', 'Sailor', 3800.00, NULL, 0),
    (39, 'Pirate 39', 'Sailor', 3900.00, NULL, 0),
    (40, 'Pirate 40', 'Captain', 4000.00, 1, 0),
    (41, 'Pirate 41', 'Sailor', 4100.00, NULL, 0),
    (42, 'Pirate 42', 'Sailor', 4200.00, NULL, 0),
    (43, 'Pirate 43', 'Sailor', 4300.00, 1, 0),
    (44, 'Pirate 44', 'Sailor', 4400.00, NULL, 0),
    (45, 'Pirate 45', 'First Mate', 4500.00, 1, 0),
    (46, 'Pirate 46', 'Sailor', 4600.00, 1, 0),
    (47, 'Pirate 47', 'Sailor', 4700.00, NULL, 0),
    (48, 'Pirate 48', 'Sailor', 4800.00, 1, 0),
    (49, 'Pirate 49', 'Sailor', 4900.00, NULL, 0),
    (50, 'Pirate 50', 'Captain', 5000.00, 1, 0);

