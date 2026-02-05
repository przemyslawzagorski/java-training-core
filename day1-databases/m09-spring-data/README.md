# Moduł 09: Spring Data JPA - Profesjonalny dostęp do danych

## 🎯 Cel modułu

Ten moduł pokazuje jak **Spring Boot + Spring Data JPA** rewolucjonizuje pracę z bazą danych:
- **Zero boilerplate** - nie piszesz implementacji DAO!
- **Automatyczne CRUD** - extends JpaRepository i gotowe
- **Query methods** - Spring generuje SQL z nazwy metody
- **Transakcje** - automatyczne zarządzanie przez @Transactional

Po tym module:
- Zrozumiesz dlaczego Spring Data jest standardem w produkcji
- Będziesz pisać repozytoria w 5 minut
- Poznasz zaawansowane techniki query methods

---

## 📊 Porównanie: JDBC → JPA → Spring Data

| Aspekt | Raw JDBC (m02-m03) | Pure JPA (m04-m08) | Spring Data (m09) |
|--------|--------------------|--------------------|-------------------|
| Kod CRUD | 50+ linii | 30+ linii | **0 linii!** |
| SQL | Ręcznie | JPQL/Criteria | **Auto-generated** |
| Transakcje | Ręcznie | Ręcznie | **@Transactional** |
| Connection Pool | Ręcznie | Ręcznie | **Auto-configured** |
| Testowanie | Trudne | Średnie | **@DataJpaTest** |
| Produktywność | ⭐ | ⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Kluczowe koncepty

### 1. JpaRepository - Magia bez kodu

```java
// To jest CAŁY KOD repozytorium!
public interface PirateRepository extends JpaRepository<Pirate, Long> {
}

// Masz za darmo:
// save(), findById(), findAll(), delete(), count(), existsById()...
```

### 2. Query Methods - SQL z nazwy metody

```java
public interface PirateRepository extends JpaRepository<Pirate, Long> {

    // Spring generuje: SELECT * FROM pirates WHERE rank = ?
    List<Pirate> findByRank(String rank);

    // SELECT * FROM pirates WHERE bounty > ? ORDER BY bounty DESC
    List<Pirate> findByBountyGreaterThanOrderByBountyDesc(BigDecimal amount);

    // SELECT * FROM pirates WHERE name LIKE '%?%'
    List<Pirate> findByNameContaining(String namePart);

    // SELECT COUNT(*) FROM pirates WHERE rank = ?
    long countByRank(String rank);

    // SELECT * FROM pirates WHERE rank = ? AND bounty > ?
    List<Pirate> findByRankAndBountyGreaterThan(String rank, BigDecimal minBounty);
}
```

### 3. @Query - Własne zapytania

```java
public interface PirateRepository extends JpaRepository<Pirate, Long> {

    // JPQL
    @Query("SELECT p FROM Pirate p WHERE p.bounty = (SELECT MAX(p2.bounty) FROM Pirate p2)")
    Optional<Pirate> findMostWanted();

    // Native SQL
    @Query(value = "SELECT * FROM pirates WHERE bounty > :amount", nativeQuery = true)
    List<Pirate> findRichPiratesNative(@Param("amount") BigDecimal amount);

    // Modifying query
    @Modifying
    @Transactional
    @Query("UPDATE Pirate p SET p.bounty = p.bounty * 1.1 WHERE p.rank = :rank")
    int increaseBountyForRank(@Param("rank") String rank);
}
```

### 4. @Transactional - Automatyczne transakcje

```java
@Service
@Transactional  // Każda metoda publiczna = osobna transakcja
public class PirateService {

    private final PirateRepository pirateRepository;
    private final ShipRepository shipRepository;

    public void transferPirateToShip(Long pirateId, Long shipId) {
        // Wszystko w jednej transakcji!
        Pirate pirate = pirateRepository.findById(pirateId).orElseThrow();
        Ship ship = shipRepository.findById(shipId).orElseThrow();

        pirate.setShip(ship);
        // Nie ma save() - dirty checking działa!

        // Jeśli cokolwiek się wysypie, całość się wycofuje
    }
}
```

---

## 📁 Struktura modułu

```
m09-spring-data/
├── src/main/java/.../
│   ├── SpringDataApplication.java     # @SpringBootApplication
│   ├── entity/
│   │   ├── Pirate.java                # Encja z @Version, @NotBlank
│   │   └── Ship.java                  # Encja z relacjami
│   ├── repository/
│   │   ├── PirateRepository.java      # Magia Spring Data
│   │   └── ShipRepository.java
│   ├── service/
│   │   └── PirateService.java         # Logika biznesowa
│   └── demo/
│       └── SpringDataDemo.java        # CommandLineRunner
├── src/main/resources/
│   ├── application.yml                # Konfiguracja Spring Boot
│   └── data.sql                       # Dane testowe
└── src/test/java/.../
    └── PirateRepositoryTest.java      # @DataJpaTest
```

---

## 🧪 Testowanie z @DataJpaTest

```java
@DataJpaTest  // Auto-konfiguracja JPA + H2 + Rollback!
class PirateRepositoryTest {

    @Autowired
    private PirateRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByRank_shouldReturnOnlyCaptains() {
        // Given
        entityManager.persist(new Pirate("Jack", "Captain", new BigDecimal("100000")));
        entityManager.persist(new Pirate("Will", "First Mate", new BigDecimal("50000")));
        entityManager.flush();

        // When
        List<Pirate> captains = repository.findByRank("Captain");

        // Then
        assertThat(captains).hasSize(1);
        assertThat(captains.get(0).getName()).isEqualTo("Jack");
    }
}
```

---

## 🚀 Uruchomienie

```bash
# Budowanie modułu
mvn clean compile -pl day1-databases/m09-spring-data

# Uruchomienie aplikacji Spring Boot
mvn spring-boot:run -pl day1-databases/m09-spring-data

# Testy
mvn test -pl day1-databases/m09-spring-data
```

---

## 📐 Słowa kluczowe Query Methods

| Keyword | Przykład | JPQL |
|---------|----------|------|
| `And` | `findByNameAndRank` | `WHERE name = ? AND rank = ?` |
| `Or` | `findByNameOrNickname` | `WHERE name = ? OR nickname = ?` |
| `Between` | `findByBountyBetween` | `WHERE bounty BETWEEN ? AND ?` |
| `LessThan` | `findByBountyLessThan` | `WHERE bounty < ?` |
| `GreaterThan` | `findByBountyGreaterThan` | `WHERE bounty > ?` |
| `Like` | `findByNameLike` | `WHERE name LIKE ?` |
| `Containing` | `findByNameContaining` | `WHERE name LIKE %?%` |
| `StartingWith` | `findByNameStartingWith` | `WHERE name LIKE ?%` |
| `EndingWith` | `findByNameEndingWith` | `WHERE name LIKE %?` |
| `OrderBy` | `findByRankOrderByNameAsc` | `ORDER BY name ASC` |
| `Not` | `findByRankNot` | `WHERE rank <> ?` |
| `In` | `findByRankIn` | `WHERE rank IN (?, ?, ?)` |
| `IsNull` | `findByShipIsNull` | `WHERE ship IS NULL` |
| `IsNotNull` | `findByShipIsNotNull` | `WHERE ship IS NOT NULL` |
| `Top` | `findTop3ByOrderByBountyDesc` | `LIMIT 3` |
| `First` | `findFirstByRank` | `LIMIT 1` |
| `Distinct` | `findDistinctByRank` | `SELECT DISTINCT` |
| `Count` | `countByRank` | `SELECT COUNT(*)` |
| `Exists` | `existsByName` | `SELECT 1 WHERE EXISTS` |

---

## ⚠️ Typowe błędy

### 1. Brak @Transactional przy modyfikacjach

```java
// ❌ ŹLE - @Modifying wymaga transakcji
@Query("UPDATE Pirate p SET p.bounty = 0")
int resetBounties();

// ✅ DOBRZE
@Modifying
@Transactional
@Query("UPDATE Pirate p SET p.bounty = 0")
int resetBounties();
```

### 2. N+1 w relacjach

```java
// ❌ ŹLE - N+1 dla każdego ship.getCrew()
List<Ship> ships = shipRepository.findAll();
ships.forEach(s -> System.out.println(s.getCrew().size()));

// ✅ DOBRZE - JOIN FETCH
@Query("SELECT s FROM Ship s JOIN FETCH s.crew")
List<Ship> findAllWithCrew();
```

### 3. LazyInitializationException

```java
// ❌ ŹLE - transakcja się zamknęła
@GetMapping("/pirates/{id}")
public Pirate getPirate(@PathVariable Long id) {
    Pirate p = repository.findById(id).orElseThrow();
    p.getShip().getName();  // 💥 LazyInitializationException!
    return p;
}

// ✅ DOBRZE - EntityGraph lub JOIN FETCH
@EntityGraph(attributePaths = {"ship"})
Optional<Pirate> findById(Long id);
```

---

## 🎓 Kluczowe wnioski

1. **Extend JpaRepository** = pełne CRUD za darmo
2. **Nazwy metod to zapytania** - Spring generuje SQL
3. **@Query dla złożonych zapytań** - JPQL lub native
4. **@DataJpaTest** = szybkie testy z rollback
5. **@Transactional w serwisach** - nie w repozytoriach
6. **EntityGraph/JOIN FETCH** - rozwiązuje N+1

---

## 📚 Materiały dodatkowe

- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Query Methods Keywords](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation)
- [Vlad Mihalcea - Spring Data Tips](https://vladmihalcea.com/spring-data-jpa/)
