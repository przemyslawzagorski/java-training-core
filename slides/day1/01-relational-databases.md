# Relacyjne bazy danych - podstawy

---

## Czym jest relacyjna baza danych?

**Tabele** - dane w wierszach i kolumnach
- Każdy wiersz = rekord (np. jeden pirat, jeden statek)
- Każda kolumna = atrybut (np. imię, wiek, ranga)

**Klucze**
- **Primary Key (PK)** - unikalny identyfikator wiersza
- **Foreign Key (FK)** - referencja do innego rekordu

**Relacje między tabelami**
- 1:1 (jeden do jednego) - np. statek ↔ kapitan
- 1:N (jeden do wielu) - np. statek ↔ piraci
- M:N (wiele do wielu) - np. statki ↔ wyspy (przez tabelę pośrednią)

---

## Dlaczego nie Excel?

| Excel | Relacyjna baza |
|-------|----------------|
| Duplikaty danych | Normalizacja (brak duplikatów) |
| Brak powiązań | Klucze obce (FK) |
| Trudne zapytania | SQL (JOIN, GROUP BY) |
| Jeden użytkownik | Tysiące jednocześnie |
| Brak transakcji | ACID |

---

## Wskazówka dla trenera
**Czas:** 5 minut

**Co mówię:**
- "Relacyjna baza to nie Excel - to system zaprojektowany do przechowywania powiązanych danych."
- "Klucz główny (PK) = unikalny identyfikator. Klucz obcy (FK) = powiązanie między tabelami."
- "Normalizacja eliminuje duplikaty - dane przechowujemy raz, linkujemy przez FK."

**Co pokazuję:**
- Diagram ERD z 5 tabelami (PIRATES, SHIPS, ISLANDS, TREASURES, SHIP_ISLAND_VISITS)
- Przykład relacji 1:N (SHIPS → PIRATES przez ship_id)
- Przykład relacji M:N (SHIPS ↔ ISLANDS przez SHIP_ISLAND_VISITS)

**Następny krok:** Slajd `02-sql-basics.md` (język SQL)

