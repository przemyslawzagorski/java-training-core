# Materiały na slajdy - 2-dniowe szkolenie Java

---

## Struktura katalogów

```
slides/
├── day1/                          # Day 1: Databases
│   ├── 01-relational-databases.md
│   ├── 02-sql-basics.md
│   ├── 03-jdbc-intro.md
│   ├── 04-sql-injection.md
│   ├── 05-connection-pooling.md
│   ├── 06-transactions.md
│   ├── 07-dao-pattern.md
│   ├── 08-dao-crud.md
│   ├── 09-jpa-intro.md
│   ├── 10-entity-lifecycle.md
│   ├── 11-jpa-relations.md
│   ├── 12-hql-jpql.md
│   ├── 13-testing-persistence.md
│   └── 14-spring-data.md
├── day2/                          # Day 2: Code Quality
│   ├── 01-hibernate-performance.md
│   ├── 02-design-patterns.md
│   ├── 03-refactoring.md
│   └── 04-tools-and-ai.md
├── TRAINER-SCRIPT.md              # Instrukcje dla trenera
└── README.md                      # Ten plik

```

---

## Format slajdów

**Każdy plik .md = 1 slajd**

Struktura slajdu:
1. **Tytuł** - krótki, merytoryczny
2. **Treść techniczna** - koncepty, definicje, kod (jeśli potrzebny)
3. **Wskazówka dla trenera** - co mówię, co pokazuję, info o ćwiczeniach

---

## Day 1: Databases (14 slajdów)

### M01: SQL Basics (2 slajdy)
- Relacyjne bazy danych - podstawy
- SQL - język zapytań

### M02: JDBC Connection (4 slajdy)
- JDBC - Java Database Connectivity
- SQL Injection - zagrożenie bezpieczeństwa
- Connection Pooling - optymalizacja wydajności
- Transakcje - ACID

### M03: DAO Pattern (2 slajdy)
- DAO Pattern - Data Access Object
- CRUD Operations w DAO

### M04-M06: JPA & Relations (3 slajdy)
- JPA - Java Persistence API
- Entity Lifecycle - cykl życia encji
- Relacje w JPA

### M07: HQL/JPQL (1 slajd)
- HQL/JPQL - zapytania obiektowe

### M08: Testing (1 slajd)
- Testowanie warstwy persystencji

### M09: Spring Data (1 slajd)
- Spring Data JPA - automatyzacja CRUD

---

## Day 2: Code Quality (4 slajdy)

### M01: Hibernate Performance (1 slajd)
- Optymalizacja wydajności Hibernate

### M02: Design Patterns (1 slajd)
- 6 wzorców projektowych (Singleton, Factory, Builder, Strategy, Decorator, Observer)

### M03: Refactoring (1 slajd)
- Code Smells i SOLID

### M04: Tools & AI (1 slajd)
- SonarLint, Checkstyle, GitHub Actions, AI Coaching

---

## Jak używać tych materiałów?

### Dla trenera:
1. Przeczytaj `TRAINER-SCRIPT.md` - szczegółowy plan szkolenia
2. Przejrzyj slajdy w kolejności (day1/01, day1/02, ...)
3. Przygotuj demo (kod w katalogach day1-databases/, day2-code-quality/)
4. Sprawdź ćwiczenia (każdy moduł ma plik *Exercises.java)

### Konwersja do PowerPoint/Google Slides:
1. Każdy plik .md to osobny slajd
2. Tytuł → Tytuł slajdu
3. Treść techniczna → Punkty na slajdzie
4. Wskazówka dla trenera → Notatki prelegenta (speaker notes)

### Przykład konwersji:

**Plik:** `day1/03-jdbc-intro.md`

**Slajd PowerPoint:**
- **Tytuł:** JDBC - Java Database Connectivity
- **Treść:**
  - API do komunikacji Java ↔ Baza danych
  - Kluczowe interfejsy: DriverManager, Connection, Statement, ResultSet
  - Try-with-resources (Java 7+) - automatyczne zamykanie
- **Notatki prelegenta:** (z sekcji "Wskazówka dla trenera")

---

## Proporcje szkolenia

**20% teoria** - slajdy, wyjaśnienia  
**30% demo** - pokazywanie kodu, uruchamianie  
**50% ćwiczenia** - uczestnicy pracują samodzielnie

**Przykład dla M02: JDBC Connection (80 min):**
- Teoria (slajdy): 40 min (4 slajdy × 10 min)
- Demo (kod): 10 min (ConnectionDemo, SqlInjectionDemo, ...)
- Ćwiczenia: 30 min (JdbcExercises.java)

---

## Wskazówki

### Dla trenera:
- **Nie czytaj slajdów** - wyjaśniaj własnymi słowami
- **Pokazuj kod** - demo jest kluczowe
- **Daj czas na ćwiczenia** - min 20-40 min na moduł
- **Pomagaj indywidualnie** - chodź między uczestnikami
- **Omów rozwiązania** - przegląd typowych błędów

### Dla uczestników:
- **Eksperymentuj** - baza H2 in-memory, nic nie zepsujesz
- **Pytaj** - nie ma głupich pytań
- **Rób notatki** - slajdy to tylko szkielet
- **Testuj** - uruchamiaj `mvn test` po każdym ćwiczeniu

---

## Materiały dodatkowe

### Kod źródłowy:
- `day1-databases/` - 9 modułów z kodem i ćwiczeniami
- `day2-code-quality/` - 4 moduły z kodem i ćwiczeniami

### Dokumentacja:
- `docs/SLIDE-CONCEPT.md` - koncepcja slajdów (piracki motyw)
- `day1-databases/AUDIT-REPORT-FINAL.md` - audyt Day 1
- `day2-code-quality/AUDIT-REPORT-FINAL.md` - audyt Day 2

### Testy:
- Każdy moduł ma testy jednostkowe (JUnit 5)
- Uruchom: `mvn test` w katalogu modułu
- Wszystkie testy powinny przechodzić (PASS)

---

## Kontakt

Pytania? Problemy? Sugestie?
- Otwórz issue na GitHubie
- Napisz email do trenera

---

**Powodzenia na szkoleniu!** 🚀

