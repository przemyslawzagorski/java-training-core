# SQL - język zapytań

---

## Podstawowe operacje SQL

**SELECT** - pobieranie danych
```sql
SELECT name, rank FROM pirates WHERE bounty > 1000;
```

**INSERT** - dodawanie danych
```sql
INSERT INTO pirates (name, rank, bounty) VALUES ('Jack', 'Captain', 5000);
```

**UPDATE** - aktualizacja danych
```sql
UPDATE pirates SET bounty = 6000 WHERE id = 1;
```

**DELETE** - usuwanie danych
```sql
DELETE FROM pirates WHERE id = 1;
```

---

## Zaawansowane zapytania

**JOIN** - łączenie tabel
```sql
SELECT p.name, s.name AS ship
FROM pirates p
JOIN ships s ON p.ship_id = s.id;
```

**Agregacje** - COUNT, SUM, AVG, MAX, MIN
```sql
SELECT ship_id, COUNT(*) AS crew_size
FROM pirates
GROUP BY ship_id;
```

---

## Wskazówka dla trenera
**Czas:** 10 minut

**Co mówię:**
- "SQL to język deklaratywny - mówisz CO chcesz, nie JAK to zrobić."
- "4 podstawowe operacje: SELECT (czytaj), INSERT (dodaj), UPDATE (zmień), DELETE (usuń)."
- "JOIN łączy tabele przez klucze obce - to fundament relacyjnych baz."

**Co pokazuję:**
- H2 Console (http://localhost:8082)
- Wykonuję proste SELECT, pokazuję wyniki
- Wykonuję JOIN pokazujący piraci + statki
- Pokazuję agregację (COUNT, GROUP BY)

**Ćwiczenia:**
- "Macie 50+ ćwiczeń SQL w 7 poziomach trudności (m01-sql-basics/exercises.md)"
- "Zacznijcie od Poziomu 1-3 (SELECT, WHERE, agregacje) - 20 minut"

**Następny krok:** Po ćwiczeniach → Slajd `03-jdbc-intro.md`

