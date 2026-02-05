# HQL/JPQL - zapytania obiektowe

---

## Czym jest HQL/JPQL?

**JPQL (Java Persistence Query Language)** - standard JPA
**HQL (Hibernate Query Language)** - rozszerzenie JPQL w Hibernate

**Różnica vs SQL:**
- SQL operuje na tabelach i kolumnach
- JPQL/HQL operuje na encjach i polach

**Przykład:**
```java
// SQL
SELECT * FROM pirates WHERE bounty > 1000

// JPQL
SELECT p FROM Pirate p WHERE p.bounty > 1000
```

---

## Podstawowe zapytania JPQL

**SELECT**
```java
List<Pirate> pirates = em.createQuery(
    "SELECT p FROM Pirate p WHERE p.bounty > :minBounty", Pirate.class)
    .setParameter("minBounty", 1000)
    .getResultList();
```

**JOIN**
```java
List<Pirate> pirates = em.createQuery(
    "SELECT p FROM Pirate p JOIN p.ship s WHERE s.name = :shipName", Pirate.class)
    .setParameter("shipName", "Black Pearl")
    .getResultList();
```

**Agregacje**
```java
Long count = em.createQuery(
    "SELECT COUNT(p) FROM Pirate p WHERE p.rank = :rank", Long.class)
    .setParameter("rank", "Captain")
    .getSingleResult();
```

**UPDATE**
```java
int updated = em.createQuery(
    "UPDATE Pirate p SET p.bounty = p.bounty * 1.1 WHERE p.rank = 'Captain'")
    .executeUpdate();
```

**DELETE**
```java
int deleted = em.createQuery(
    "DELETE FROM Pirate p WHERE p.bounty < 100")
    .executeUpdate();
```

---

## Criteria API - type-safe queries

**Problem z JPQL:** Błędy wykrywane w runtime
```java
// Literówka w nazwie pola - błąd w runtime!
"SELECT p FROM Pirate p WHERE p.bountyyy > 1000"
```

**Rozwiązanie - Criteria API:** Błędy wykrywane w compile-time
```java
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Pirate> cq = cb.createQuery(Pirate.class);
Root<Pirate> pirate = cq.from(Pirate.class);

cq.select(pirate)
  .where(cb.gt(pirate.get("bounty"), 1000));  // Type-safe!

List<Pirate> pirates = em.createQuery(cq).getResultList();
```

---

## Native SQL - gdy JPQL nie wystarcza

**Czasem potrzebujemy surowego SQL:**
- Funkcje specyficzne dla bazy (PostgreSQL, MySQL)
- Optymalizacje wydajnościowe
- Skomplikowane zapytania

```java
List<Pirate> pirates = em.createNativeQuery(
    "SELECT * FROM pirates WHERE bounty > ? ORDER BY bounty DESC", Pirate.class)
    .setParameter(1, 1000)
    .getResultList();
```

---

## Wskazówka dla trenera
**Czas:** 15 minut

**Co mówię:**
- "JPQL operuje na encjach, nie tabelach. SELECT p FROM Pirate (nie FROM pirates)."
- "Parametry nazwane (:minBounty) - bezpieczne jak PreparedStatement."
- "JOIN w JPQL używa pól encji (p.ship), nie kluczy obcych."
- "Criteria API = type-safe, ale więcej kodu. JPQL = czytelniejsze."
- "Native SQL gdy JPQL nie wystarcza (funkcje specyficzne dla bazy)."

**Co pokazuję:**
- `HqlDemo.java` - podstawowe zapytania JPQL
- `CriteriaApiDemo.java` - type-safe queries
- `NativeSqlDemo.java` - surowy SQL
- Uruchamiam demo, pokazuję wygenerowane SQL

**Ćwiczenia:**
- "Macie 10 ćwiczeń HQL (m07-hql-queries/HqlExercises.java)"
- "Exercises 1-5: SELECT, JOIN, agregacje"
- "Exercises 6-7: UPDATE, DELETE"
- "30 minut na Exercises 1-5"

**Następny krok:** Po ćwiczeniach → Slajd `13-testing-persistence.md`

