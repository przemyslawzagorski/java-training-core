# Testowanie warstwy persystencji

---

## Dlaczego testować warstwę persystencji?

**Typowe błędy:**
- Niepoprawne mapowanie encji (@Column, @JoinColumn)
- Błędy w zapytaniach JPQL/HQL
- Problemy z relacjami (LAZY loading, N+1 SELECT)
- Błędy w transakcjach

**Testy wykrywają problemy wcześnie!**

---

## Testy jednostkowe vs integracyjne

**Testy jednostkowe (Unit Tests)**
- Testują pojedynczą klasę w izolacji
- Mockują zależności (np. EntityManager)
- Szybkie (milisekundy)

**Testy integracyjne (Integration Tests)**
- Testują współpracę komponentów
- Używają prawdziwej bazy (H2 in-memory)
- Wolniejsze (sekundy)

**Dla warstwy persystencji: testy integracyjne!**
- Sprawdzają czy SQL jest poprawny
- Sprawdzają czy mapowanie działa
- Sprawdzają czy relacje działają

---

## JUnit 5 + H2 in-memory

**Konfiguracja testów:**
```java
@BeforeAll
static void setupDatabase() {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("test-pu");
}

@BeforeEach
void setupTransaction() {
    em = emf.createEntityManager();
    em.getTransaction().begin();
}

@AfterEach
void rollbackTransaction() {
    if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
    }
    em.close();
}

@AfterAll
static void closeDatabase() {
    emf.close();
}
```

**Test:**
```java
@Test
void shouldFindPirateById() {
    // Given
    Pirate pirate = new Pirate("Jack", "Captain", 5000);
    em.persist(pirate);
    em.flush();
    
    // When
    Pirate found = em.find(Pirate.class, pirate.getId());
    
    // Then
    assertThat(found).isNotNull();
    assertThat(found.getName()).isEqualTo("Jack");
}
```

---

## AssertJ - fluent assertions

**JUnit assertions (stary styl):**
```java
assertEquals("Jack", pirate.getName());
assertTrue(pirate.getBounty() > 1000);
```

**AssertJ (nowy styl):**
```java
assertThat(pirate.getName()).isEqualTo("Jack");
assertThat(pirate.getBounty()).isGreaterThan(1000);
assertThat(pirates)
    .hasSize(3)
    .extracting(Pirate::getName)
    .containsExactly("Jack", "Will", "Elizabeth");
```

**Korzyści:**
- Czytelniejsze (fluent API)
- Lepsze komunikaty błędów
- Więcej asercji (hasSize, extracting, containsExactly)

---

## Spring Boot - @DataJpaTest

**Dla projektów Spring Boot:**
```java
@DataJpaTest
class PirateRepositoryTest {
    @Autowired
    private PirateRepository pirateRepository;
    
    @Test
    void shouldFindByName() {
        // Given
        Pirate pirate = new Pirate("Jack", "Captain", 5000);
        pirateRepository.save(pirate);
        
        // When
        Optional<Pirate> found = pirateRepository.findByName("Jack");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getRank()).isEqualTo("Captain");
    }
}
```

**@DataJpaTest:**
- Konfiguruje H2 in-memory
- Rollback po każdym teście
- Ładuje tylko komponenty JPA (nie cały Spring Context)

---

## Wskazówka dla trenera
**Czas:** 15 minut

**Co mówię:**
- "Warstwa persystencji wymaga testów integracyjnych - sprawdzamy czy SQL działa."
- "H2 in-memory - szybka baza do testów (nie trzeba instalować PostgreSQL)."
- "JUnit 5: @BeforeEach, @AfterEach - setup i rollback transakcji."
- "AssertJ - czytelniejsze asercje niż JUnit."
- "Spring Boot: @DataJpaTest - automatyczna konfiguracja testów."

**Co pokazuję:**
- `PirateDaoTest.java` - testy DAO (JUnit 5 + H2)
- `PirateRepositoryTest.java` - testy Spring Data (@DataJpaTest)
- Uruchamiam testy: `mvn test`
- Pokazuję wyniki (wszystkie PASS)

**Ćwiczenia:**
- "Macie 5 ćwiczeń testowych (m08-testing-persistence/TestingExercises.java)"
- "Exercises 1-3: Testy CRUD, relacji, zapytań"
- "30 minut na Exercises 1-3"

**Następny krok:** Po ćwiczeniach → Slajd `14-spring-data.md`

