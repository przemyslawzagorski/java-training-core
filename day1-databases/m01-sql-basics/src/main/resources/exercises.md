# 🏴‍☠️ Ćwiczenia SQL - Baza Danych Piratów

## Diagram Bazy Danych

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                           DIAGRAM BAZY DANYCH PIRATÓW                               │
└─────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────┐       ┌─────────────────────┐       ┌─────────────────────┐
│      ISLANDS        │       │       SHIPS         │       │      PIRATES        │
├─────────────────────┤       ├─────────────────────┤       ├─────────────────────┤
│ PK id               │◄──────│ FK home_island_id   │       │ PK id               │
│    name             │       │ PK id               │◄──────│ FK ship_id          │
│    location         │       │    name             │       │    name             │
│    has_treasure     │       │    ship_type        │       │    nickname         │
│    treasure_value   │       │    cannons          │       │    rank             │
└─────────────────────┘       │    crew_capacity    │       │    bounty           │
         ▲                    │ FK captain_id ──────┼──────►│    joined_at        │
         │                    └─────────────────────┘       └─────────────────────┘
         │                              │
         │                              │
         │                              ▼
┌─────────────────────┐       ┌─────────────────────┐
│     TREASURES       │       │  SHIP_ISLAND_VISITS │
├─────────────────────┤       ├─────────────────────┤
│ PK id               │       │ PK,FK ship_id       │
│    name             │       │ PK,FK island_id     │
│    treasure_type    │       │     visit_date      │
│    value            │       └─────────────────────┘
│ FK island_id ───────┼───────────────▲
│ FK found_by_ship_id │               │
└─────────────────────┘               │
                                      │
         Relacje:                     │
         ─────────                    │
         PK = Primary Key             │
         FK = Foreign Key             │
         ◄──── = One-to-Many          │
```

### Relacje między tabelami

| Relacja | Typ | Opis |
|---------|-----|------|
| `ships.home_island_id → islands.id` | Many-to-One | Każdy statek ma macierzystą wyspę |
| `ships.captain_id → pirates.id` | One-to-One | Każdy statek ma jednego kapitana |
| `pirates.ship_id → ships.id` | Many-to-One | Wielu piratów może należeć do jednego statku |
| `treasures.island_id → islands.id` | Many-to-One | Skarb znajduje się na wyspie |
| `treasures.found_by_ship_id → ships.id` | Many-to-One | Skarb znaleziony przez statek |
| `ship_island_visits` | Many-to-Many | Statki odwiedzają wiele wysp |

---

## 📋 Zadania

### Poziom 1: SELECT - Podstawy

#### Zadanie 1.1
Wyświetl wszystkie kolumny z tabeli `pirates`.

#### Zadanie 1.2
Wyświetl tylko nazwy (`name`) i pseudonimy (`nickname`) wszystkich piratów.

#### Zadanie 1.3
Wyświetl wszystkie wyspy, które mają skarb (`has_treasure = TRUE`).

#### Zadanie 1.4
Wyświetl statki posortowane malejąco według liczby armat (`cannons`).

#### Zadanie 1.5
Wyświetl piratów, którzy mają nagrodę (`bounty`) większą niż 5000.

---

### Poziom 2: WHERE, ORDER BY, LIMIT

#### Zadanie 2.1
Znajdź wszystkich piratów o randze 'Captain'.

#### Zadanie 2.2
Wyświetl 3 piratów z najwyższą nagrodą za głowę.

#### Zadanie 2.3
Znajdź statki typu 'Galleon' z więcej niż 30 armatami.

#### Zadanie 2.4
Wyświetl wyspy, których wartość skarbu (`treasure_value`) jest większa niż 20000.

#### Zadanie 2.5
Znajdź piratów, którzy dołączyli po roku 1700 (użyj `joined_at`).

---

### Poziom 3: Funkcje agregujące

#### Zadanie 3.1
Oblicz sumę wszystkich nagród za głowy piratów.

#### Zadanie 3.2
Znajdź średnią liczbę armat na statkach.

#### Zadanie 3.3
Policz ilu jest piratów w bazie danych.

#### Zadanie 3.4
Znajdź maksymalną i minimalną wartość skarbu na wyspach.

#### Zadanie 3.5
Oblicz całkowitą wartość wszystkich skarbów w tabeli `treasures`.

---

### Poziom 4: GROUP BY i HAVING

#### Zadanie 4.1
Policz ilu piratów jest na każdym statku (grupuj po `ship_id`).

#### Zadanie 4.2
Wyświetl rangi piratów i średnią nagrodę dla każdej rangi.

#### Zadanie 4.3
Znajdź typy statków (`ship_type`) i ich średnią liczbę armat, ale tylko dla typów ze średnią > 30.

#### Zadanie 4.4
Policz ile skarbów każdego typu (`treasure_type`) znajduje się w bazie.

#### Zadanie 4.5
Wyświetl statki, które mają więcej niż 1 pirata na pokładzie.

---

### Poziom 5: JOIN

#### Zadanie 5.1
Wyświetl imiona piratów wraz z nazwami ich statków.

#### Zadanie 5.2
Wyświetl nazwy statków i nazwy ich macierzystych wysp.

#### Zadanie 5.3
Wyświetl skarby wraz z nazwami wysp, na których się znajdują.

#### Zadanie 5.4
Wyświetl wszystkie wizyty statków na wyspach (nazwa statku, nazwa wyspy, data wizyty).

#### Zadanie 5.5
Wyświetl kapitanów (imię pirata) i nazwy statków, którymi dowodzą.

---

### Poziom 6: Zaawansowane JOIN i podzapytania

#### Zadanie 6.1
Znajdź piratów, którzy są na statku 'Black Pearl' (użyj podzapytania).

#### Zadanie 6.2
Wyświetl statki, które nigdy nie odwiedziły żadnej wyspy (użyj LEFT JOIN).

#### Zadanie 6.3
Znajdź wyspę z najcenniejszym skarbem i wyświetl jej nazwę.

#### Zadanie 6.4
Wyświetl piratów wraz z ich statkami, ale pokaż też piratów bez statku (LEFT JOIN).

#### Zadanie 6.5
Znajdź statek, który odwiedził najwięcej wysp.

---

### Poziom 7: UPDATE i DELETE

#### Zadanie 7.1
Zwiększ nagrodę za głowę Jacka Sparrowa o 5000.

#### Zadanie 7.2
Zmień lokalizację wyspy 'Tortuga' na 'Secret Location'.

#### Zadanie 7.3
Ustaw `has_treasure` na FALSE dla wszystkich wysp z `treasure_value` = 0.

#### Zadanie 7.4
Dodaj nowego pirata: 'Anne Bonny', pseudonim 'Red Anne', ranga 'Gunner', nagroda 2000.

#### Zadanie 7.5
Usuń wszystkie wizyty sprzed roku 1710.

---

### Poziom 8: Zadania praktyczne

#### Zadanie 8.1
Stwórz raport pokazujący: nazwę statku, liczbę piratów na pokładzie, łączną sumę nagród załogi.

#### Zadanie 8.2
Znajdź "najbogatszą" wyspę - taką, która ma skarb o największej wartości w tabeli `treasures`.

#### Zadanie 8.3
Wyświetl ranking piratów według nagrody za głowę (pozycja, imię, nagroda).

#### Zadanie 8.4
Znajdź statki, które odwiedziły wyspy ze skarbami.

#### Zadanie 8.5
Stwórz widok (VIEW) pokazujący pełne informacje o piratach z nazwami ich statków.

---

## ✅ Odpowiedzi

<details>
<summary><b>Kliknij, aby rozwinąć odpowiedzi</b></summary>

### Poziom 1: SELECT - Podstawy

#### Odpowiedź 1.1
```sql
SELECT * FROM pirates;
```

#### Odpowiedź 1.2
```sql
SELECT name, nickname FROM pirates;
```

#### Odpowiedź 1.3
```sql
SELECT * FROM islands WHERE has_treasure = TRUE;
```

#### Odpowiedź 1.4
```sql
SELECT * FROM ships ORDER BY cannons DESC;
```

#### Odpowiedź 1.5
```sql
SELECT * FROM pirates WHERE bounty > 5000;
```

---

### Poziom 2: WHERE, ORDER BY, LIMIT

#### Odpowiedź 2.1
```sql
SELECT * FROM pirates WHERE rank = 'Captain';
```

#### Odpowiedź 2.2
```sql
SELECT * FROM pirates ORDER BY bounty DESC LIMIT 3;
```

#### Odpowiedź 2.3
```sql
SELECT * FROM ships WHERE ship_type = 'Galleon' AND cannons > 30;
```

#### Odpowiedź 2.4
```sql
SELECT * FROM islands WHERE treasure_value > 20000;
```

#### Odpowiedź 2.5
```sql
SELECT * FROM pirates WHERE joined_at > '1700-12-31';
-- lub (składnia H2)
SELECT * FROM pirates WHERE EXTRACT(YEAR FROM joined_at) > 1700;
```

---

### Poziom 3: Funkcje agregujące

#### Odpowiedź 3.1
```sql
SELECT SUM(bounty) AS total_bounty FROM pirates;
```

#### Odpowiedź 3.2
```sql
SELECT AVG(cannons) AS avg_cannons FROM ships;
```

#### Odpowiedź 3.3
```sql
SELECT COUNT(*) AS pirates_count FROM pirates;
```

#### Odpowiedź 3.4
```sql
SELECT MAX(treasure_value) AS max_treasure, 
       MIN(treasure_value) AS min_treasure 
FROM islands;
```

#### Odpowiedź 3.5
```sql
SELECT SUM("value") AS total_treasure_value FROM treasures;
```

---

### Poziom 4: GROUP BY i HAVING

#### Odpowiedź 4.1
```sql
SELECT ship_id, COUNT(*) AS crew_count 
FROM pirates 
GROUP BY ship_id;
```

#### Odpowiedź 4.2
```sql
SELECT rank, AVG(bounty) AS avg_bounty 
FROM pirates 
GROUP BY rank;
```

#### Odpowiedź 4.3
```sql
SELECT ship_type, AVG(cannons) AS avg_cannons 
FROM ships 
GROUP BY ship_type 
HAVING AVG(cannons) > 30;
```

#### Odpowiedź 4.4
```sql
SELECT treasure_type, COUNT(*) AS count 
FROM treasures 
GROUP BY treasure_type;
```

#### Odpowiedź 4.5
```sql
SELECT ship_id, COUNT(*) AS crew_count 
FROM pirates 
GROUP BY ship_id 
HAVING COUNT(*) > 1;
```

---

### Poziom 5: JOIN

#### Odpowiedź 5.1
```sql
SELECT p.name AS pirate_name, s.name AS ship_name 
FROM pirates p 
JOIN ships s ON p.ship_id = s.id;
```

#### Odpowiedź 5.2
```sql
SELECT s.name AS ship_name, i.name AS island_name 
FROM ships s 
JOIN islands i ON s.home_island_id = i.id;
```

#### Odpowiedź 5.3
```sql
SELECT t.name AS treasure_name, i.name AS island_name 
FROM treasures t 
JOIN islands i ON t.island_id = i.id;
```

#### Odpowiedź 5.4
```sql
SELECT s.name AS ship_name, i.name AS island_name, v.visit_date 
FROM ship_island_visits v 
JOIN ships s ON v.ship_id = s.id 
JOIN islands i ON v.island_id = i.id;
```

#### Odpowiedź 5.5
```sql
SELECT p.name AS captain_name, s.name AS ship_name 
FROM ships s 
JOIN pirates p ON s.captain_id = p.id;
```

---

### Poziom 6: Zaawansowane JOIN i podzapytania

#### Odpowiedź 6.1
```sql
SELECT * FROM pirates 
WHERE ship_id = (SELECT id FROM ships WHERE name = 'Black Pearl');
```

#### Odpowiedź 6.2
```sql
SELECT s.* 
FROM ships s 
LEFT JOIN ship_island_visits v ON s.id = v.ship_id 
WHERE v.ship_id IS NULL;
```

#### Odpowiedź 6.3
```sql
SELECT i.name 
FROM islands i 
JOIN treasures t ON i.id = t.island_id 
WHERE t."value" = (SELECT MAX("value") FROM treasures);
```

#### Odpowiedź 6.4
```sql
SELECT p.name AS pirate_name, s.name AS ship_name 
FROM pirates p 
LEFT JOIN ships s ON p.ship_id = s.id;
```

#### Odpowiedź 6.5
```sql
SELECT s.name, COUNT(v.island_id) AS visits_count 
FROM ships s 
JOIN ship_island_visits v ON s.id = v.ship_id 
GROUP BY s.id, s.name 
ORDER BY visits_count DESC 
LIMIT 1;
```

---

### Poziom 7: UPDATE i DELETE

#### Odpowiedź 7.1
```sql
UPDATE pirates 
SET bounty = bounty + 5000 
WHERE name = 'Jack Sparrow';
```

#### Odpowiedź 7.2
```sql
UPDATE islands 
SET location = 'Secret Location' 
WHERE name = 'Tortuga';
```

#### Odpowiedź 7.3
```sql
UPDATE islands 
SET has_treasure = FALSE 
WHERE treasure_value = 0;
```

#### Odpowiedź 7.4
```sql
INSERT INTO pirates (name, nickname, rank, bounty) 
VALUES ('Anne Bonny', 'Red Anne', 'Gunner', 2000);
```

#### Odpowiedź 7.5
```sql
DELETE FROM ship_island_visits 
WHERE visit_date < '1710-01-01';
```

---

### Poziom 8: Zadania praktyczne

#### Odpowiedź 8.1
```sql
SELECT 
    s.name AS ship_name, 
    COUNT(p.id) AS crew_count, 
    COALESCE(SUM(p.bounty), 0) AS total_bounty 
FROM ships s 
LEFT JOIN pirates p ON s.id = p.ship_id 
GROUP BY s.id, s.name;
```

#### Odpowiedź 8.2
```sql
SELECT i.name AS island_name, t."value" AS treasure_value 
FROM islands i 
JOIN treasures t ON i.id = t.island_id 
ORDER BY t."value" DESC 
LIMIT 1;
```

#### Odpowiedź 8.3
```sql
SELECT 
    ROW_NUMBER() OVER (ORDER BY bounty DESC) AS position, 
    name, 
    bounty 
FROM pirates;
```

#### Odpowiedź 8.4
```sql
SELECT DISTINCT s.name 
FROM ships s 
JOIN ship_island_visits v ON s.id = v.ship_id 
JOIN islands i ON v.island_id = i.id 
WHERE i.has_treasure = TRUE;
```

#### Odpowiedź 8.5
```sql
CREATE VIEW pirate_details AS 
SELECT 
    p.id, 
    p.name, 
    p.nickname, 
    p.rank, 
    p.bounty, 
    s.name AS ship_name, 
    s.ship_type 
FROM pirates p 
LEFT JOIN ships s ON p.ship_id = s.id;
```

</details>

---

## 📊 Dane testowe - przegląd

### Wyspy (islands)
| id | name | location | has_treasure | treasure_value |
|----|------|----------|--------------|----------------|
| 1 | Tortuga | Caribbean Sea | TRUE | 50000.00 |
| 2 | Port Royal | Jamaica | FALSE | 0 |
| 3 | Isla de Muerta | Unknown | TRUE | 1000000.00 |
| 4 | Nassau | Bahamas | TRUE | 25000.00 |

### Statki (ships)
| id | name | ship_type | cannons | crew_capacity |
|----|------|-----------|---------|---------------|
| 1 | Black Pearl | Galleon | 32 | 100 |
| 2 | Flying Dutchman | Galleon | 46 | 150 |
| 3 | Queen Annes Revenge | Frigate | 40 | 120 |
| 4 | Interceptor | Sloop | 16 | 40 |

### Piraci (pirates)
| id | name | nickname | rank | bounty |
|----|------|----------|------|--------|
| 1 | Jack Sparrow | Captain Jack | Captain | 10000.00 |
| 2 | Davy Jones | Devil of the Sea | Captain | 100000.00 |
| 3 | Edward Teach | Blackbeard | Captain | 50000.00 |
| 4 | Joshamee Gibbs | Mr. Gibbs | First Mate | 1000.00 |
| 5 | Hector Barbossa | Barbossa | Quartermaster | 8000.00 |
| 6 | Bootstrap Bill | Bootstrap | First Mate | 3000.00 |

---

## 🎯 Wskazówki

1. **Używaj aliasów** - ułatwiają czytanie zapytań z JOIN
2. **Pamiętaj o cudzysłowach** - kolumna `"value"` jest słowem zastrzeżonym
3. **Testuj krok po kroku** - buduj złożone zapytania etapami
4. **Sprawdzaj NULL** - niektóre kolumny mogą mieć wartości NULL

---

## 🔧 Specyfika bazy H2

### Różnice względem innych baz danych

| Operacja | MySQL/PostgreSQL | H2 |
|----------|------------------|-----|
| Struktura tabeli | `DESCRIBE table` | `SHOW COLUMNS FROM table` |
| Lista tabel | `SHOW TABLES` | `SHOW TABLES` ✅ |
| Rok z daty | `YEAR(date)` | `EXTRACT(YEAR FROM date)` |
| Słowa zastrzeżone | różne | `value`, `user`, `order` itd. wymagają `"cudzysłowów"` |

### Przydatne polecenia H2

```sql
-- Wyświetl wszystkie tabele
SHOW TABLES;

-- Wyświetl kolumny tabeli
SHOW COLUMNS FROM pirates;

-- Wyświetl wszystkie klucze obce
SELECT TC.TABLE_NAME, TC.CONSTRAINT_NAME
FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS TC
WHERE TC.CONSTRAINT_TYPE = 'FOREIGN KEY';

-- Wyświetl schemat tabeli z kluczami
SELECT 
    COLUMN_NAME, 
    DATA_TYPE, 
    IS_NULLABLE,
    COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'PIRATES';
```

### Słowa zastrzeżone w H2

Jeśli nazwa kolumny jest słowem zastrzeżonym, użyj cudzysłowów:
```sql
-- ❌ Błąd
SELECT value FROM treasures;

-- ✅ Poprawnie
SELECT "value" FROM treasures;
```

---

*Powodzenia w ćwiczeniach! 🏴‍☠️*
