# Moduł 01: SQL Basics

## 🎯 Cel modułu
Nauczenie podstaw języka SQL poprzez praktyczne ćwiczenia na bazie danych piratów. Kursant pozna strukturę relacyjnej bazy danych oraz nauczy się pisać zapytania SELECT, JOIN, agregacje i podzapytania.

## 🔑 Kluczowe pojęcia

| Pojęcie | Opis |
|---------|------|
| **SELECT** | Podstawowe zapytanie do odczytu danych z tabeli |
| **WHERE** | Filtrowanie wyników według warunków |
| **JOIN** | Łączenie danych z wielu tabel (INNER, LEFT, RIGHT) |
| **GROUP BY** | Grupowanie danych do agregacji |
| **HAVING** | Filtrowanie grup po agregacji |
| **ORDER BY** | Sortowanie wyników |
| **Funkcje agregujące** | COUNT, SUM, AVG, MIN, MAX |
| **Klucz główny (PK)** | Unikalny identyfikator rekordu |
| **Klucz obcy (FK)** | Referencja do innej tabeli |
| **Relacje** | 1:1, 1:N, M:N między tabelami |

## 📁 Zawartość

| Plik/Klasa | Opis |
|------------|------|
| `H2ConsoleStarter.java` | Uruchamia konsolę webową H2 do ćwiczeń SQL |
| `schema.sql` | Definicja struktury bazy (5 tabel: pirates, ships, islands, treasures, ship_island_visits) |
| `data.sql` | Przykładowe dane pirackie do ćwiczeń |
| `exercises.md` | 50+ ćwiczeń SQL podzielonych na 7 poziomów trudności |
| `database-diagram.md` | Diagram ERD i szczegółowy opis struktury bazy |

## 🗺️ Struktura bazy danych

Baza zawiera 5 powiązanych tabel:
- **PIRATES** - piraci z rangami i nagrodami za głowy
- **SHIPS** - statki z armatami i kapitanami
- **ISLANDS** - wyspy ze skarbami
- **TREASURES** - skarby znalezione przez statki
- **SHIP_ISLAND_VISITS** - historia wizyt statków na wyspach (relacja M:N)

## 🚀 Jak uruchomić

### Uruchomienie konsoli H2
```bash
# Z poziomu głównego katalogu projektu
mvn exec:java -pl day1-databases/m01-sql-basics -Dexec.mainClass="pl.przemekzagorski.training.sql.H2ConsoleStarter"
```

Lub uruchom klasę `H2ConsoleStarter.main()` bezpośrednio w IntelliJ IDEA.

### Połączenie z bazą
Po uruchomieniu otwórz przeglądarkę: **http://localhost:8082**

Dane do logowania:
- **JDBC URL:** `jdbc:h2:mem:pirates`
- **User:** `sa`
- **Password:** _(puste)_

### Wykonywanie ćwiczeń
1. Otwórz plik `exercises.md` w katalogu `src/main/resources/`
2. Skopiuj zapytanie SQL z ćwiczenia
3. Wklej do konsoli H2 i uruchom
4. Sprawdź wynik z rozwiązaniem (rozwiązania są w tym samym pliku pod spoilerem)

## 📚 Powiązane materiały

- **Ćwiczenia:** `src/main/resources/exercises.md` - 50+ zadań SQL z rozwiązaniami
- **Diagram bazy:** `src/main/resources/database-diagram.md` - szczegółowy ERD
- **Przewodnik trenera:** `docs/01-TRAINER-GUIDE-DAY1.md` - sekcja "BLOK 2: SQL BASICS"
- **Workbook kursanta:** `docs/03-STUDENT-WORKBOOK-DAY1.md`

## 💡 Wskazówki

- Zacznij od poziomu 1 (podstawy SELECT) i stopniowo przechodź do trudniejszych
- Każde ćwiczenie buduje na poprzednich - nie przeskakuj poziomów
- Eksperymentuj z zapytaniami - baza jest w pamięci, nic nie zepsujesz!
- Używaj `SHOW TABLES;` aby zobaczyć wszystkie tabele
- Używaj `SHOW COLUMNS FROM nazwa_tabeli;` aby zobaczyć strukturę tabeli

## 🎓 Poziomy ćwiczeń

1. **Poziom 1:** SELECT - Podstawy (5 zadań)
2. **Poziom 2:** WHERE, ORDER BY, LIMIT (5 zadań)
3. **Poziom 3:** Funkcje agregujące (5 zadań)
4. **Poziom 4:** GROUP BY i HAVING (5 zadań)
5. **Poziom 5:** JOIN (5 zadań)
6. **Poziom 6:** Podzapytania (5 zadań)
7. **Poziom 7:** Zaawansowane (10+ zadań)

---

🏴‍☠️ **Powodzenia w opanowaniu SQL!** ⚓
