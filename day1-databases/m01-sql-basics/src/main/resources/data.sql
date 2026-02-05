-- ============================================
-- DANE TESTOWE - PIRACI Z KARAIBÓW
-- ============================================

INSERT INTO islands (name, location, has_treasure, treasure_value) VALUES
('Tortuga', 'Caribbean Sea', TRUE, 50000.00),
('Port Royal', 'Jamaica', FALSE, 0),
('Isla de Muerta', 'Unknown', TRUE, 1000000.00),
('Nassau', 'Bahamas', TRUE, 25000.00);

INSERT INTO ships (name, ship_type, cannons, crew_capacity) VALUES
('Black Pearl', 'Galleon', 32, 100),
('Flying Dutchman', 'Galleon', 46, 150),
('Queen Annes Revenge', 'Frigate', 40, 120),
('Interceptor', 'Sloop', 16, 40);

INSERT INTO pirates (name, nickname, rank, bounty, ship_id, joined_at) VALUES
('Jack Sparrow', 'Captain Jack', 'Captain', 10000.00, 1, '1720-01-15'),
('Davy Jones', 'Devil of the Sea', 'Captain', 100000.00, 2, '1650-06-06'),
('Edward Teach', 'Blackbeard', 'Captain', 50000.00, 3, '1716-11-01'),
('Joshamee Gibbs', 'Mr. Gibbs', 'First Mate', 1000.00, 1, '1720-01-20'),
('Hector Barbossa', 'Barbossa', 'Quartermaster', 8000.00, 1, '1720-02-01'),
('Bootstrap Bill', 'Bootstrap', 'First Mate', 3000.00, 2, '1700-01-01');

UPDATE ships SET captain_id = 1 WHERE id = 1;
UPDATE ships SET captain_id = 2 WHERE id = 2;
UPDATE ships SET captain_id = 3 WHERE id = 3;
UPDATE ships SET home_island_id = 1 WHERE id = 1;

INSERT INTO treasures (name, treasure_type, "value", island_id, found_by_ship_id) VALUES
('Aztec Gold Coins', 'Gold', 100000.00, 3, 1),
('Davys Chest', 'Artifacts', 500000.00, NULL, 2),
('Spanish Doubloons', 'Gold', 25000.00, 1, 3);

INSERT INTO ship_island_visits (ship_id, island_id, visit_date) VALUES
(1, 1, '1725-03-15'),
(1, 3, '1725-06-20'),
(2, 3, '1700-01-01'),
(3, 1, '1717-05-10'),
(3, 4, '1718-02-01');

