# 🏴‍☠️ Moduł 01: Spring Data Advanced

## 📖 Opis modułu

Ten moduł pokazuje **zaawansowane techniki Spring Data JPA** w kontekście produkcyjnym.

**Kontekst:**
- **Dzień 1, m09**: Spring Data podstawy ✅
- **Dzień 2, m01**: Spring Data zaawansowane (TEN MODUŁ)

**Różnica vs m09:**
- m09: Podstawy (CRUD, query methods, relacje)
- m01: Zaawansowane (Cache L2, Flyway, paginacja, projekcje, batch processing)

---

## 🎯 Cel modułu

Nauczyć się **produkcyjnych technik** Spring Data JPA:
1. **Flyway** - migracje bazy danych
2. **Cache L2** - Hibernate Second Level Cache
3. **Paginacja** - Pageable i Page<T>
4. **Projekcje** - Spring Data Projections
5. **Batch Processing** - wydajny import danych
6. **Read-Only Mode** - optymalizacja zapytań

---

## 🔑 Kluczowe koncepty

### 1. Flyway - Database Migrations
```sql
-- V1__create_pirates_table.sql
CREATE TABLE pirates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    ...
);
```

**Korzyści:**
- ✅ Wersjonowanie schematu bazy
- ✅ Automatyczne migracje
- ✅ Bezpieczne zmiany w produkcji

### 2. Cache L2 - Hibernate Second Level Cache
```java
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Pirate { ... }
```

**Korzyści:**
- ✅ Mniej zapytań do bazy
- ✅ Szybsze odczyty
- ✅ Cache współdzielony między sesjami

### 3. Paginacja - Pageable i Page<T>
```java
Pageable pageable = PageRequest.of(0, 10, Sort.by("bounty").descending());
Page<Pirate> page = pirateRepository.findAll(pageable);
```

**Korzyści:**
- ✅ Nie ładujemy wszystkich danych naraz
- ✅ Metadane (total, hasNext, etc.)
- ✅ Łatwa iteracja

### 4. Projekcje - Spring Data Projections
```java
public interface PirateNameOnly {
    String getName();
    String getRank();
}

List<PirateNameOnly> pirates = repository.findProjectedByRank("Captain");
```

**Korzyści:**
- ✅ Tylko potrzebne pola
- ✅ Szybsze zapytania
- ✅ Automatyczna implementacja

### 5. Batch Processing
```java
for (int i = 0; i < 1000; i++) {
    em.persist(pirate);
    if (i % 50 == 0) {
        em.flush();
        em.clear();
    }
}
```

**Korzyści:**
- ✅ Szybszy import dużej ilości danych
- ✅ Mniejsze zużycie pamięci

### 6. Read-Only Mode
```java
@Transactional(readOnly = true)
public List<Pirate> findAll() { ... }
```

**Korzyści:**
- ✅ Brak dirty checking
- ✅ Szybsze zapytania

---

## 📁 Struktura modułu

```
m01-spring-data-advanced/
├── src/main/java/.../
│   ├── entity/
│   │   ├── Pirate.java          # Z @Cacheable
│   │   └── Ship.java            # Z @BatchSize
│   ├── repository/
│   │   ├── PirateRepository.java
│   │   └── ShipRepository.java
│   ├── projection/
│   │   ├── PirateNameOnly.java
│   │   ├── PirateSummary.java
│   │   └── ShipSummary.java
│   ├── service/
│   │   ├── CacheL2DemoService.java
│   │   ├── PaginationService.java
│   │   ├── ProjectionService.java
│   │   ├── BatchProcessingService.java
│   │   └── ReadOnlyService.java
│   └── demo/
│       └── AdvancedFeaturesDemo.java
├── src/main/resources/
│   ├── application.yml          # Konfiguracja
│   ├── ehcache.xml              # Cache L2
│   └── db/migration/
│       ├── V1__create_pirates_table.sql
│       ├── V2__create_ships_table.sql
│       └── V3__insert_sample_data.sql
├── src/test/java/.../exercises/
│   ├── PerformanceExercises.java         # Ćwiczenia
│   └── PerformanceExercisesSolutions.java # Rozwiązania
├── COMPARISON.md                # Pure JPA vs Spring Data
└── README.md                    # Ten plik
```

---

## 🚀 Uruchomienie

### 1. Uruchom aplikację
```bash
mvn spring-boot:run -pl day2-code-quality/m01-spring-data-advanced
```

### 2. Uruchom demo
```bash
mvn spring-boot:run -pl day2-code-quality/m01-spring-data-advanced -Ddemo.enabled=true
```

### 3. Uruchom testy
```bash
mvn test -pl day2-code-quality/m01-spring-data-advanced
```

### 4. H2 Console
Otwórz: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:advanceddb`
- Username: `sa`
- Password: (puste)

---

## 📝 Ćwiczenia

Ćwiczenia znajdują się w:
```
src/test/java/.../exercises/PerformanceExercises.java
```

**Progresja trudności:**
- ⭐ Ćwiczenie 1: Podstawowa paginacja
- ⭐⭐ Ćwiczenie 2: Paginacja + filtrowanie
- ⭐⭐ Ćwiczenie 3: Projekcje
- ⭐⭐⭐ Ćwiczenie 4: Projekcje + Paginacja
- ⭐⭐⭐ Ćwiczenie 5: Iteracja przez strony
- ⭐⭐⭐⭐ Ćwiczenie 6: Cache L2
- ⭐⭐⭐⭐⭐ BONUS: Złożone sortowanie

**Rozwiązania:**
```
src/test/java/.../exercises/PerformanceExercisesSolutions.java
```

---

## 📚 Odniesienia

- **Dzień 1, m09**: [Spring Data podstawy](../../day1-databases/m09-spring-data)
- **Porównanie**: [Pure JPA vs Spring Data](./COMPARISON.md)
- **Dokumentacja**: [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- **Flyway**: [Flyway Documentation](https://flywaydb.org/documentation/)
- **Hibernate Cache**: [Hibernate Second Level Cache](https://docs.jboss.org/hibernate/orm/6.0/userguide/html_single/Hibernate_User_Guide.html#caching)

---

## 🎓 Co dalej?

Po tym module powinieneś umieć:
- ✅ Używać Flyway do migracji bazy danych
- ✅ Konfigurować i używać Cache L2
- ✅ Implementować paginację z Pageable
- ✅ Tworzyć projekcje dla optymalizacji
- ✅ Wykonywać batch processing
- ✅ Optymalizować zapytania z readOnly=true

**Następny krok:** Wzorce projektowe (m02-design-patterns)

