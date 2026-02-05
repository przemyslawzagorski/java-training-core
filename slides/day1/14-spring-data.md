# Spring Data JPA - automatyzacja CRUD

---

## Czym jest Spring Data JPA?

**Automatyzacja warstwy DAO**
- Eliminuje kod boilerplate (findById, save, delete)
- Generuje implementację na podstawie interfejsu
- Query Methods - zapytania z nazwy metody
- @Query - własne zapytania JPQL

**Ewolucja:**
```
JDBC (100 linii) → JPA (50 linii) → Spring Data (5 linii)
```

---

## JpaRepository - gotowe metody CRUD

**Zamiast pisać DAO:**
```java
public class PirateDaoJdbc implements PirateDao {
    public Optional<Pirate> findById(Long id) {
        // 20 linii kodu JDBC
    }
    public List<Pirate> findAll() {
        // 15 linii kodu JDBC
    }
    public Pirate save(Pirate pirate) {
        // 30 linii kodu JDBC (INSERT lub UPDATE)
    }
    public void delete(Long id) {
        // 10 linii kodu JDBC
    }
}
```

**Wystarczy interface:**
```java
public interface PirateRepository extends JpaRepository<Pirate, Long> {
    // Gotowe metody: findById, findAll, save, delete, count, existsById, ...
}
```

**Spring Data generuje implementację automatycznie!**

---

## Query Methods - zapytania z nazwy metody

**Spring Data parsuje nazwę metody i generuje JPQL:**

```java
public interface PirateRepository extends JpaRepository<Pirate, Long> {
    // SELECT p FROM Pirate p WHERE p.name = ?1
    Optional<Pirate> findByName(String name);
    
    // SELECT p FROM Pirate p WHERE p.rank = ?1
    List<Pirate> findByRank(String rank);
    
    // SELECT p FROM Pirate p WHERE p.bounty > ?1
    List<Pirate> findByBountyGreaterThan(Integer bounty);
    
    // SELECT p FROM Pirate p WHERE p.name LIKE %?1%
    List<Pirate> findByNameContaining(String keyword);
    
    // SELECT p FROM Pirate p WHERE p.rank = ?1 AND p.bounty > ?2
    List<Pirate> findByRankAndBountyGreaterThan(String rank, Integer bounty);
    
    // SELECT COUNT(p) FROM Pirate p WHERE p.rank = ?1
    long countByRank(String rank);
    
    // DELETE FROM Pirate p WHERE p.bounty < ?1
    @Transactional
    void deleteByBountyLessThan(Integer bounty);
}
```

**Konwencje nazewnictwa:**
- `findBy` - SELECT
- `countBy` - COUNT
- `deleteBy` - DELETE
- `GreaterThan`, `LessThan`, `Containing`, `And`, `Or` - warunki

---

## @Query - własne zapytania JPQL

**Gdy Query Methods nie wystarczają:**

```java
public interface PirateRepository extends JpaRepository<Pirate, Long> {
    @Query("SELECT p FROM Pirate p WHERE p.bounty > :minBounty ORDER BY p.bounty DESC")
    List<Pirate> findTopPirates(@Param("minBounty") Integer minBounty);
    
    @Query("SELECT p FROM Pirate p JOIN FETCH p.ship WHERE p.rank = :rank")
    List<Pirate> findByRankWithShip(@Param("rank") String rank);
    
    @Modifying
    @Transactional
    @Query("UPDATE Pirate p SET p.bounty = p.bounty * 1.1 WHERE p.rank = 'Captain'")
    int increaseCaptainBounties();
}
```

**@Modifying** - dla UPDATE/DELETE  
**@Transactional** - wymagane dla modyfikacji

---

## Spring Data vs DAO Pattern

| DAO Pattern | Spring Data JPA |
|-------------|-----------------|
| Interface + Implementation | Tylko Interface |
| Ręczny kod CRUD | Automatyczny CRUD |
| Ręczne zapytania JPQL | Query Methods |
| Pełna kontrola | Mniej kontroli |
| Więcej kodu | Mniej kodu |

**Kiedy używać DAO Pattern?**
- Skomplikowana logika dostępu do danych
- Potrzeba pełnej kontroli nad SQL
- Optymalizacje wydajnościowe

**Kiedy używać Spring Data?**
- Proste CRUD
- Standardowe zapytania
- Szybki rozwój aplikacji

---

## Wskazówka dla trenera
**Czas:** 20 minut

**Co mówię:**
- "Spring Data eliminuje kod boilerplate - wystarczy interface."
- "Query Methods - Spring parsuje nazwę metody i generuje JPQL."
- "findByRankAndBountyGreaterThan - czytelne, ale długie nazwy!"
- "@Query gdy Query Methods nie wystarczają."
- "Spring Data to automatyzacja, ale mniej kontroli niż DAO Pattern."

**Co pokazuję:**
- `PirateRepository` - interface z Query Methods
- `SpringDataDemo.java` - użycie repository
- Uruchamiam demo, pokazuję wygenerowane SQL
- Pokazuję jak Spring Data generuje implementację (debug)

**Ćwiczenia:**
- "Macie 5 ćwiczeń Spring Data (m09-spring-data/SpringDataExercises.java)"
- "Exercises 1-3: Query Methods, @Query, paginacja"
- "30 minut na Exercises 1-3"

**Podsumowanie Day 1:**
- "Przeszliśmy drogę: SQL → JDBC → DAO → JPA → Spring Data"
- "Od 100 linii kodu (JDBC) do 5 linii (Spring Data)"
- "Automatyzacja vs kontrola - wybierajcie narzędzie do zadania!"

**Następny krok:** Koniec Day 1 → Day 2: Code Quality

