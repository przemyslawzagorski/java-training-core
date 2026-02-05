# 🏴‍☠️ Java Training Core - Piraci z Karaibów Edition

Materiały szkoleniowe dla **Junior Java Developer** - Comarch.

## 📋 O szkoleniu

| Parametr | Wartość |
|----------|---------|
| **Czas trwania** | 2 dni (16h) |
| **Wersja Java** | 21 |
| **Baza danych** | H2 (in-memory) |
| **Motyw domenowy** | Piraci z Karaibów ⚓ |

## 🗓️ Agenda

### Dzień 1: Bazy danych
- Powtórka SQL
- JDBC - połączenie, CRUD, transakcje, wzorzec DAO
- JPA/Hibernate - encje, cykl życia, relacje
- Zaawansowane zapytania (JPQL, Criteria API, Native SQL)
- Testowanie warstwy persystencji
- **Spring Data JPA** - profesjonalny dostęp do danych

### Dzień 2: Kod wysokiej jakości  
- Hibernate zaawansowany (Lazy/Eager, N+1, Cache)
- Wzorce projektowe (Singleton, Factory, Builder, Strategy)
- Refaktoring i SOLID
- Narzędzia (SonarLint, AI, CI/CD)

## 📁 Struktura projektu

```
java-training-core/
├── day1-databases/          # Dzień 1: Bazy danych
│   ├── m01-sql-basics/      # SQL, konsola H2
│   ├── m02-jdbc-connection/ # JDBC, SQL Injection, HikariCP
│   ├── m03-jdbc-crud/       # Wzorzec DAO, testy JUnit
│   ├── m04-jpa-intro/       # JPA, encje, Bean Validation
│   ├── m05-entity-lifecycle/# Stany encji, @Version
│   ├── m06-relations/       # Relacje JPA, equals/hashCode
│   ├── m07-hql-queries/     # JPQL, Criteria API, Native SQL
│   ├── m08-testing-persistence/ # Testy JPA, Mockito
│   └── m09-spring-data/     # Spring Boot + Spring Data JPA
│
└── day2-code-quality/       # Dzień 2: Kod wysokiej jakości
    ├── m01-hibernate-advanced/
    ├── m02-design-patterns/
    ├── m03-refactoring/
    └── m04-tools-and-ai/
```

---

## 📚 Nawigacja modułów - Dzień 1

### 🗄️ Podstawy SQL i JDBC

| Moduł | Opis | Kluczowe koncepty |
|-------|------|-------------------|
| [m01-sql-basics](day1-databases/m01-sql-basics/) | SQL dla juniorów | SELECT, JOIN, GROUP BY, ćwiczenia |
| [m02-jdbc-connection](day1-databases/m02-jdbc-connection/) | Połączenie z bazą | PreparedStatement, SQL Injection, HikariCP |
| [m03-jdbc-crud](day1-databases/m03-jdbc-crud/) | Wzorzec DAO | CRUD, Optional, testy JUnit 5 |

### 🔷 JPA / Hibernate

| Moduł | Opis | Kluczowe koncepty |
|-------|------|-------------------|
| [m04-jpa-intro](day1-databases/m04-jpa-intro/) | Wprowadzenie do JPA | @Entity, @Id, persist, Bean Validation |
| [m05-entity-lifecycle](day1-databases/m05-entity-lifecycle/) | Cykl życia encji | MANAGED/DETACHED, dirty checking, @Version |
| [m06-relations](day1-databases/m06-relations/) | Relacje JPA | @OneToMany, @ManyToMany, equals/hashCode |

### 🔍 Zapytania zaawansowane

| Moduł | Opis | Kluczowe koncepty |
|-------|------|-------------------|
| [m07-hql-queries](day1-databases/m07-hql-queries/) | Zapytania JPQL | JPQL, Criteria API, Native SQL, DTO |

### 🧪 Testowanie

| Moduł | Opis | Kluczowe koncepty |
|-------|------|-------------------|
| [m08-testing-persistence](day1-databases/m08-testing-persistence/) | Testy JPA | H2 in-memory, Mockito, @BeforeEach |
| [m03-jdbc-crud](day1-databases/m03-jdbc-crud/) | Testy DAO | JUnit 5, AssertJ, AAA pattern |

### 🚀 Spring Data

| Moduł | Opis | Kluczowe koncepty |
|-------|------|-------------------|
| [m09-spring-data](day1-databases/m09-spring-data/) | Spring Data JPA | JpaRepository, Query Methods, @DataJpaTest |

---

## 🎯 Ścieżka nauki

```
Dzień 1: Bazy danych
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

m01 SQL Basics        → Powtórka SQL, konsola H2
        ↓
m02 JDBC Connection   → PreparedStatement, SQL Injection, HikariCP
        ↓
m03 JDBC CRUD         → Wzorzec DAO, Optional, testy JUnit
        ↓
m04 JPA Intro         → @Entity, EntityManager, Bean Validation
        ↓
m05 Entity Lifecycle  → MANAGED/DETACHED, @Version
        ↓
m06 Relations         → @OneToMany, @ManyToMany, equals/hashCode
        ↓
m07 HQL Queries       → JPQL, Criteria API, Native SQL
        ↓
m08 Testing           → Testy integracyjne, Mockito
        ↓
m09 Spring Data       → JpaRepository, @Query, @DataJpaTest

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

## 🚀 Uruchomienie

```bash
# Kompilacja całego projektu
mvn clean compile

# Uruchomienie testów
mvn test

# Uruchomienie konkretnego modułu
cd day1-databases/m02-jdbc-connection
mvn exec:java -Dexec.mainClass="pl.przemekzagorski.training.jdbc.ConnectionDemo"

# Uruchomienie Spring Boot (m09)
cd day1-databases/m09-spring-data
mvn spring-boot:run
```

---

## 🧰 Technologie

| Technologia | Wersja | Zastosowanie |
|-------------|--------|--------------|
| Java | 21 | Język główny |
| Hibernate ORM | 6.4.1 | Implementacja JPA |
| H2 Database | 2.2.224 | Baza in-memory |
| HikariCP | 5.1.0 | Connection pooling |
| Spring Boot | 3.2.2 | Framework (m09) |
| JUnit 5 | 5.10.2 | Testy jednostkowe |
| AssertJ | 3.25.1 | Fluent assertions |
| Mockito | 5.8.0 | Mocking |
| Hibernate Validator | 8.0.1 | Bean Validation |

---

## 👨‍🏫 Autor

Przemek Zagórski - Trener Java

