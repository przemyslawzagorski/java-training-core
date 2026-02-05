-- ============================================
-- SCHEMAT BAZY DANYCH PIRATOW
-- ============================================

CREATE TABLE islands (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(200),
    has_treasure BOOLEAN DEFAULT FALSE,
    treasure_value DECIMAL(15,2) DEFAULT 0
);

CREATE TABLE ships (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    ship_type VARCHAR(50),
    cannons INT DEFAULT 0,
    crew_capacity INT DEFAULT 50,
    captain_id BIGINT,
    home_island_id BIGINT
);

CREATE TABLE pirates (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    nickname VARCHAR(100),
    rank VARCHAR(50),
    bounty DECIMAL(15,2) DEFAULT 0,
    ship_id BIGINT,
    joined_at DATE,
    FOREIGN KEY (ship_id) REFERENCES ships(id)
);

ALTER TABLE ships ADD FOREIGN KEY (home_island_id) REFERENCES islands(id);

CREATE TABLE treasures (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    treasure_type VARCHAR(50),
    "value" DECIMAL(15,2) NOT NULL,
    island_id BIGINT,
    found_by_ship_id BIGINT,
    FOREIGN KEY (island_id) REFERENCES islands(id),
    FOREIGN KEY (found_by_ship_id) REFERENCES ships(id)
);

CREATE TABLE ship_island_visits (
    ship_id BIGINT NOT NULL,
    island_id BIGINT NOT NULL,
    visit_date DATE,
    PRIMARY KEY (ship_id, island_id),
    FOREIGN KEY (ship_id) REFERENCES ships(id),
    FOREIGN KEY (island_id) REFERENCES islands(id)
);

