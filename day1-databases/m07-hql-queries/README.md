# Moduł 07: HQL/JPQL Queries - Zaawansowane zapytania

## 🎯 Cel modułu

Po module m04-jpa-intro znasz podstawowe JPQL. Teraz czas na **zaawansowane techniki zapytań**, które są niezbędne w realnych projektach:
- Projekcje do DTO (SELECT NEW)
- Subqueries (podzapytania)
- Criteria API (dynamiczne zapytania)
- Native SQL (gdy JPQL nie wystarcza)
- Named Queries (wydajność i porządek)

---

## 📚 Skąd to się bierze?

### Progresja nauki:

```
m04-jpa-intro          m07-hql-queries (TU JESTEŚ)
     │                        │
     ▼                        ▼
Podstawy JPQL:         Zaawansowane zapytania:
- SELECT s FROM Ship   - SELECT NEW DTO(...)
- WHERE, ORDER BY      - Subqueries, CASE WHEN
- Proste JOIN          - JOIN FETCH vs lazy
                       - Criteria API
                       - Native SQL
```

**Dlaczego to ważne?**
- 90% pracy z bazą to zapytania - musisz je znać!
- Criteria API = bezpieczne dynamiczne filtrowanie
- JOIN FETCH = rozwiązanie problemu N+1
- Native SQL = gdy JPQL nie potrafi (procedury, specyfika bazy)

---

## 🔑 Kluczowe pojęcia

| Pojęcie | Opis |
|---------|------|
| **JPQL** | Java Persistence Query Language - zapytania na encjach |
| **HQL** | Hibernate Query Language - rozszerzenie JPQL przez Hibernate |
| **Projekcja** | Wybór konkretnych pól zamiast całych encji |
| **DTO Projection** | `SELECT NEW DTO(...)` - wynik jako obiekt DTO |
| **Criteria API** | Type-safe budowanie zapytań w kodzie Java |
| **Native Query** | Surowy SQL wykonywany przez JPA |
| **Named Query** | Prekompilowane zapytanie zdefiniowane na encji |
| **JOIN FETCH** | Eager loading relacji w jednym SELECT |
| **Subquery** | Podzapytanie zagnieżdżone w WHERE lub SELECT |

---

## 📁 Zawartość modułu

| Klasa/Plik | Opis |
|------------|------|
| `entity/Pirate.java` | Encja pirata z relacjami |
| `entity/Ship.java` | Encja statku |
| `entity/Treasure.java` | Encja skarbu |
| `dto/PirateDTO.java` | DTO dla projekcji |
| `dto/ShipSummaryDTO.java` | DTO dla agregacji |
| `JpqlAdvancedDemo.java` | Demo zaawansowanego JPQL |
| `CriteriaApiDemo.java` | Demo Criteria API |
| `NativeQueryDemo.java` | Demo Native SQL |
| `QueryExercises.java` | 🎯 Ćwiczenia |
| `QueryExercisesSolutions.java` | Rozwiązania |

---

## 📊 JPQL vs HQL vs Native SQL

| Cecha | JPQL | HQL | Native SQL |
|-------|------|-----|------------|
| Standard | ✅ Jakarta EE | Hibernate only | Baza-specyficzny |
| Operuje na | Encjach | Encjach | Tabelach |
| Przenośność | Wysoka | Średnia | Niska |
| Wydajność | Dobra | Dobra | Najlepsza* |
| Funkcje specjalne | Ograniczone | Więcej | Wszystkie |

*Native SQL może być szybszy dla skomplikowanych zapytań

---

## 🔍 Projekcje - SELECT tylko to co potrzebujesz

### ❌ Problem: Pobieranie całych encji

```java
// Pobieramy WSZYSTKIE pola, a używamy tylko 2
List<Pirate> pirates = em.createQuery(
    "SELECT p FROM Pirate p", Pirate.class).getResultList();

for (Pirate p : pirates) {
    System.out.println(p.getName() + ": " + p.getBounty());
    // Nie używamy: id, nickname, rank, ship, joinedAt, version...
}
```

### ✅ Rozwiązanie 1: Projekcja do Object[]

```java
List<Object[]> results = em.createQuery(
    "SELECT p.name, p.bounty FROM Pirate p", Object[].class).getResultList();

for (Object[] row : results) {
    String name = (String) row[0];
    BigDecimal bounty = (BigDecimal) row[1];
}
```

### ✅ Rozwiązanie 2: Projekcja do DTO (ZALECANE)

```java
// DTO
public record PirateDTO(String name, BigDecimal bounty) {}

// Zapytanie z SELECT NEW
List<PirateDTO> pirates = em.createQuery(
    "SELECT NEW pl.training.dto.PirateDTO(p.name, p.bounty) FROM Pirate p",
    PirateDTO.class).getResultList();

// Teraz mamy type-safe obiekty!
pirates.forEach(dto -> System.out.println(dto.name() + ": " + dto.bounty()));
```

---

## 🔗 JOIN FETCH - Rozwiązanie problemu N+1

### ❌ Problem N+1 SELECT

```java
// 1 SELECT dla piratów
List<Pirate> pirates = em.createQuery("SELECT p FROM Pirate p", Pirate.class)
    .getResultList();

// N SELECT dla statków (lazy loading!)
for (Pirate p : pirates) {
    System.out.println(p.getShip().getName()); // Każdy -> osobny SELECT!
}
// Jeśli mamy 100 piratów = 101 zapytań SQL!
```

### ✅ Rozwiązanie: JOIN FETCH

```java
// 1 SELECT dla piratów WRAZ ze statkami
List<Pirate> pirates = em.createQuery(
    "SELECT p FROM Pirate p JOIN FETCH p.ship", Pirate.class)
    .getResultList();

// Bez dodatkowych zapytań!
for (Pirate p : pirates) {
    System.out.println(p.getShip().getName()); // Dane już załadowane!
}
// Tylko 1 zapytanie SQL z JOIN!
```

---

## 🔄 Criteria API - Dynamiczne zapytania

### Dlaczego Criteria API?

```java
// ❌ Dynamiczny JPQL = konkatenacja stringów = błędy!
String jpql = "SELECT p FROM Pirate p WHERE 1=1";
if (name != null) jpql += " AND p.name = '" + name + "'"; // SQL Injection!
if (minBounty != null) jpql += " AND p.bounty > " + minBounty;
```

### ✅ Criteria API - type-safe i bezpieczne

```java
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Pirate> query = cb.createQuery(Pirate.class);
Root<Pirate> pirate = query.from(Pirate.class);

List<Predicate> predicates = new ArrayList<>();

if (name != null) {
    predicates.add(cb.equal(pirate.get("name"), name));
}
if (minBounty != null) {
    predicates.add(cb.greaterThan(pirate.get("bounty"), minBounty));
}

query.where(predicates.toArray(new Predicate[0]));

List<Pirate> results = em.createQuery(query).getResultList();
```

**Zalety:**
- ✅ Type-safe (błędy kompilacji, nie runtime)
- ✅ Bezpieczne przed SQL Injection
- ✅ Refactoring-friendly (IDE widzi użycia)

---

## 📝 Native SQL - Gdy JPQL nie wystarcza

### Kiedy używać Native SQL?

1. **Funkcje specyficzne dla bazy** (PostgreSQL arrays, MySQL JSON)
2. **Procedury składowane**
3. **Złożone zapytania optymalizacyjne**
4. **Migracja z istniejącego SQL**

### Przykład

```java
// Native SQL z mapowaniem na encję
List<Pirate> pirates = em.createNativeQuery(
    "SELECT * FROM pirates WHERE bounty > :minBounty", Pirate.class)
    .setParameter("minBounty", 5000)
    .getResultList();

// Native SQL z mapowaniem na DTO (wymaga @SqlResultSetMapping lub Tuple)
List<Object[]> results = em.createNativeQuery(
    "SELECT name, bounty FROM pirates ORDER BY bounty DESC")
    .setMaxResults(10)
    .getResultList();
```

---

## 🏷️ Named Queries - Wydajność i porządek

### Definicja na encji

```java
@Entity
@NamedQueries({
    @NamedQuery(
        name = "Pirate.findByRank",
        query = "SELECT p FROM Pirate p WHERE p.rank = :rank"
    ),
    @NamedQuery(
        name = "Pirate.findRichest",
        query = "SELECT p FROM Pirate p ORDER BY p.bounty DESC"
    )
})
public class Pirate { ... }
```

### Użycie

```java
List<Pirate> captains = em.createNamedQuery("Pirate.findByRank", Pirate.class)
    .setParameter("rank", "Captain")
    .getResultList();
```

**Zalety:**
- ✅ Prekompilowane przy starcie aplikacji (szybsze)
- ✅ Błędy wykryte od razu (nie w runtime)
- ✅ Centralne miejsce dla zapytań

---

## 🚀 Jak uruchomić

```bash
# Demo zaawansowanego JPQL
mvn exec:java -pl day1-databases/m07-hql-queries \
    -Dexec.mainClass="pl.przemekzagorski.training.jpa.JpqlAdvancedDemo"

# Demo Criteria API
mvn exec:java -pl day1-databases/m07-hql-queries \
    -Dexec.mainClass="pl.przemekzagorski.training.jpa.CriteriaApiDemo"

# Demo Native SQL
mvn exec:java -pl day1-databases/m07-hql-queries \
    -Dexec.mainClass="pl.przemekzagorski.training.jpa.NativeQueryDemo"

# Ćwiczenia
mvn exec:java -pl day1-databases/m07-hql-queries \
    -Dexec.mainClass="pl.przemekzagorski.training.jpa.QueryExercises"
```

---

## 🎓 Ćwiczenia

| # | Ćwiczenie | Koncept |
|---|-----------|---------|
| 1 | Projekcja do DTO | SELECT NEW |
| 2 | JOIN FETCH vs lazy | Problem N+1 |
| 3 | Subquery - znajdź max | Podzapytania |
| 4 | Criteria API - filtrowanie | Dynamiczne zapytania |
| 5 | Native SQL z mapowaniem | Surowy SQL |
| 6 | Named Query | Prekompilowane zapytania |

---

## ⚠️ Typowe błędy

| Błąd | Rozwiązanie |
|------|-------------|
| N+1 SELECT problem | Użyj `JOIN FETCH` |
| "path expected for join" | Użyj aliasu: `p.ship s` nie `ship` |
| DTO not found | Podaj pełną ścieżkę pakietu w SELECT NEW |
| MultipleBagFetchException | Nie można JOIN FETCH dwóch kolekcji List - użyj Set |
| Criteria API: "cannot be cast" | Sprawdź typ w `get()` |

---

## 📚 Powiązane materiały

- **Poprzedni:** [m06-relations](../m06-relations/) - relacje między encjami
- **Następny:** [m08-testing-persistence](../m08-testing-persistence/) - testy warstwy persistence
- [Hibernate User Guide - HQL](https://docs.jboss.org/hibernate/orm/6.4/userguide/html_single/Hibernate_User_Guide.html#hql)
- [JPA Criteria API](https://jakarta.ee/specifications/persistence/3.0/)

---

🏴‍☠️ **Opanuj zapytania - opanujesz bazę danych!** ⚓
