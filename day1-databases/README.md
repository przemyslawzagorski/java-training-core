# 🏴‍☠️ Day 1: Bazy danych w Java - od SQL do JPA/Hibernate

## 🎯 Cel dnia szkoleniowego

Po tym dniu szkolenia będziesz:
- Rozumieć podstawy relacyjnych baz danych i SQL
- Potrafić łączyć się z bazą danych przez JDBC
- Wykonywać operacje CRUD w czystym JDBC
- Rozumieć dlaczego powstało JPA/Hibernate
- Mapować obiekty Java na tabele bazy danych
- Zarządzać cyklem życia encji
- Tworzyć relacje między encjami

---

## 📚 Mapa modułów

```
┌─────────────────────────────────────────────────────────────────────┐
│                        DAY 1: DATABASES                              │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐          │
│  │ M01: SQL     │───►│ M02: JDBC    │───►│ M03: JDBC    │          │
│  │   Basics     │    │  Connection  │    │    CRUD      │          │
│  │  (teoria)    │    │  (connect)   │    │ (operacje)   │          │
│  └──────────────┘    └──────────────┘    └──────────────┘          │
│                                                 │                   │
│                                                 ▼                   │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐          │
│  │ M06: JPA     │◄───│ M05: Entity  │◄───│ M04: JPA     │          │
│  │  Relations   │    │  Lifecycle   │    │    Intro     │          │
│  │  (relacje)   │    │   (stany)    │    │  (ORM)       │          │
│  └──────────────┘    └──────────────┘    └──────────────┘          │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 📋 Przegląd modułów

| # | Moduł | Temat | Czas | Ćwiczenia |
|---|-------|-------|------|-----------|
| 01 | [m01-sql-basics](m01-sql-basics/) | Podstawy SQL i relacyjnych baz danych | 60 min | 50+ ćwiczeń SQL |
| 02 | [m02-jdbc-connection](m02-jdbc-connection/) | Połączenie z bazą przez JDBC | 45 min | 4 ćwiczenia |
| 03 | [m03-jdbc-crud](m03-jdbc-crud/) | Operacje CRUD w JDBC | 60 min | 5 ćwiczeń |
| 04 | [m04-jpa-intro](m04-jpa-intro/) | Wprowadzenie do JPA/Hibernate | 60 min | 6 ćwiczeń |
| 05 | [m05-entity-lifecycle](m05-entity-lifecycle/) | Cykl życia encji JPA | 45 min | 6 ćwiczeń |
| 06 | [m06-relations](m06-relations/) | Relacje między encjami | 60 min | 6 ćwiczeń |

**Łączny czas:** ~5.5 godziny (z przerwami: 6-7 godzin)

---

## 🛤️ Ścieżka nauki

### Część 1: Fundamenty SQL (m01)
- Czym są relacyjne bazy danych?
- Składnia SQL: SELECT, INSERT, UPDATE, DELETE
- JOIN, GROUP BY, agregacje
- Indeksy i klucze

### Część 2: JDBC - niskopoziomowy dostęp (m02-m03)
- DriverManager i połączenia
- Statement vs PreparedStatement
- Transakcje i commit/rollback
- Obsługa wyjątków i zasobów

### Część 3: JPA/Hibernate - ORM (m04-m06)
- Po co ORM? Problemy z JDBC
- Encje i mapowanie @Entity
- Persistence Context i EntityManager
- Stany encji: TRANSIENT, MANAGED, DETACHED, REMOVED
- Relacje: @OneToOne, @OneToMany, @ManyToMany

---

## 💻 Wymagania techniczne

### Technologie
- **Java 17+** (zalecane 21)
- **Maven 3.8+**
- **H2 Database** (in-memory, zero konfiguracji)
- **Hibernate 6.x** (implementacja JPA)
- **IDE:** IntelliJ IDEA / Eclipse / VS Code

### Uruchomienie
```bash
# Klonowanie projektu
git clone <repo>
cd java-training-core/day1-databases

# Kompilacja wszystkich modułów
mvn clean compile

# Uruchomienie ćwiczeń dla konkretnego modułu
cd m03-jdbc-crud
mvn exec:java -Dexec.mainClass="pl.przemekzagorski.training.jdbc.PirateExercises"
```

---

## 📁 Struktura modułu

Każdy moduł ma podobną strukturę:

```
mXX-nazwa-modulu/
├── pom.xml              # Zależności Maven
├── README.md            # Teoria i dokumentacja
└── src/main/java/
    └── pl/przemekzagorski/training/
        ├── entity/           # Encje JPA (jeśli dotyczy)
        ├── XxxDemo.java      # Demonstracja (do uruchomienia)
        ├── XxxExercises.java # 🎯 Ćwiczenia (Twoja praca!)
        └── XxxExercisesSolutions.java  # Rozwiązania
```

### Jak pracować z ćwiczeniami?

1. **Przeczytaj README.md** - zrozum teorię
2. **Uruchom Demo** - zobacz działający kod
3. **Otwórz Exercises** - znajdź miejsca TODO
4. **Uzupełnij kod** - zaimplementuj rozwiązania
5. **Uruchom main()** - sprawdź wyniki
6. **Jeśli utkniesz** - zajrzyj do Solutions

---

## 🎯 Cele szczegółowe modułów

### M01: SQL Basics
Po tym module potrafisz:
- ✅ Pisać zapytania SELECT z WHERE, ORDER BY
- ✅ Używać JOIN do łączenia tabel
- ✅ Stosować funkcje agregujące (COUNT, SUM, AVG)
- ✅ Wykonywać INSERT, UPDATE, DELETE

### M02: JDBC Connection
Po tym module potrafisz:
- ✅ Nawiązać połączenie z bazą H2
- ✅ Rozumiesz JDBC URL
- ✅ Prawidłowo zamykasz zasoby (try-with-resources)
- ✅ Rozumiesz problemy z JDBC (boilerplate)

### M03: JDBC CRUD
Po tym module potrafisz:
- ✅ Wykonywać operacje CRUD przez JDBC
- ✅ Używać PreparedStatement (bezpieczeństwo!)
- ✅ Zarządzać transakcjami
- ✅ Obsługiwać ResultSet

### M04: JPA Intro
Po tym module potrafisz:
- ✅ Skonfigurować persistence.xml
- ✅ Definiować encje z @Entity, @Id, @Column
- ✅ Używać EntityManager do CRUD
- ✅ Pisać proste zapytania JPQL

### M05: Entity Lifecycle
Po tym module potrafisz:
- ✅ Rozpoznawać 4 stany encji
- ✅ Rozumiesz dirty checking
- ✅ Prawidłowo używasz merge() vs persist()
- ✅ Unikasz LazyInitializationException

### M06: Relations
Po tym module potrafisz:
- ✅ Mapować relacje @OneToOne, @OneToMany, @ManyToMany
- ✅ Rozumiesz stronę właściciela vs odwrotną
- ✅ Używasz cascade i orphanRemoval
- ✅ Rozwiązujesz problem N+1 przez JOIN FETCH

---

## ⚠️ Typowe błędy juniorów

| Błąd | Moduł | Rozwiązanie |
|------|-------|-------------|
| SQL Injection | M03 | Używaj PreparedStatement z parametrami |
| Niezamknięte Connection | M02-M03 | Używaj try-with-resources |
| LazyInitializationException | M05-M06 | Pobierz dane przed zamknięciem EM lub użyj JOIN FETCH |
| Zmiana strony odwrotnej | M06 | Zmieniaj tylko stronę właściciela (bez mappedBy) |
| Brak transakcji | M03-M06 | Modyfikacje wymagają transakcji |
| Ignorowanie merge() | M05 | Używaj wartości zwróconej przez merge() |

---

## 📚 Materiały dodatkowe

### Dokumentacja
- [JDBC Tutorial (Oracle)](https://docs.oracle.com/javase/tutorial/jdbc/)
- [JPA 3.0 Specification](https://jakarta.ee/specifications/persistence/)
- [Hibernate User Guide](https://docs.jboss.org/hibernate/orm/6.4/userguide/html_single/Hibernate_User_Guide.html)

### Książki
- "Java Persistence with Hibernate" - Bauer, King, Gregory
- "High-Performance Java Persistence" - Vlad Mihalcea

### Blogi
- [Vlad Mihalcea](https://vladmihalcea.com/) - JPA/Hibernate best practices
- [Thorben Janssen](https://thorben-janssen.com/) - JPA tutorials

---

## 🏴‍☠️ Motyw przewodni

Wszystkie przykłady w szkoleniu używają motywu **pirackiego**:
- **Pirates** - piraci (załoga)
- **Ships** - statki
- **Islands** - wyspy
- **Treasures** - skarby
- **Captains** - kapitanowie

Dzięki temu nauka jest przyjemniejsza, a przykłady łatwiejsze do zapamiętania! ⚓

---

## 🚀 Powodzenia!

Pamiętaj:
1. **Nie ma głupich pytań** - pytaj, jeśli czegoś nie rozumiesz
2. **Błędy to nauka** - każdy exception to okazja do zrozumienia
3. **Praktyka czyni mistrza** - rób ćwiczenia, nie tylko czytaj
4. **Rozwiązania są po to, żeby z nich korzystać** - ale najpierw spróbuj sam!

---

*Szkolenie przygotowane przez zespół Java Training.*
