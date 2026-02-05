# Moduł 8: Testowanie warstwy persystencji

## 📋 Cele modułu

Po ukończeniu tego modułu uczestnik będzie potrafił:
- ✅ Pisać testy jednostkowe dla repozytoriów JPA
- ✅ Konfigurować środowisko testowe z H2 in-memory
- ✅ Stosować wzorce testowania (Arrange-Act-Assert)
- ✅ Używać AssertJ dla czytelnych asercji
- ✅ Izolować testy z @BeforeEach i transakcjami
- ✅ Mockować EntityManager z Mockito

## 🎯 Kluczowe koncepcje

| Koncepcja | Opis |
|-----------|------|
| **Test jednostkowy** | Testuje pojedynczą jednostkę kodu w izolacji |
| **Test integracyjny** | Testuje interakcję z prawdziwą bazą (H2) |
| **AAA Pattern** | Arrange (przygotuj) → Act (wykonaj) → Assert (sprawdź) |
| **AssertJ** | Biblioteka fluent assertions dla czytelnych testów |
| **@BeforeEach** | Setup przed każdym testem |
| **@AfterEach** | Cleanup po każdym teście |
| **Rollback** | Cofanie transakcji po teście |

## 📁 Struktura modułu

```
m08-testing-persistence/
├── src/
│   ├── main/java/
│   │   └── pl/przemekzagorski/training/jpa/
│   │       ├── entity/
│   │       │   └── Pirate.java            # Encja do testów
│   │       └── repository/
│   │           └── PirateRepository.java  # Repozytorium CRUD
│   └── test/java/
│       └── pl/przemekzagorski/training/jpa/
│           ├── repository/
│           │   ├── PirateRepositoryTest.java  # Testy integracyjne
│           │   └── PirateRepositoryUnitTest.java  # Testy jednostkowe z Mockito
│           └── TestPersistenceConfig.java     # Konfiguracja testowa
├── src/main/resources/META-INF/
│   └── persistence.xml
├── src/test/resources/META-INF/
│   └── persistence.xml                    # Osobna konfiguracja dla testów
└── README.md
```

## 🔧 Wzorce testowania

### 1. Arrange-Act-Assert (AAA)

```java
@Test
void shouldFindPirateByName() {
    // Arrange - przygotowanie danych
    Pirate jack = new Pirate("Jack Sparrow", "Captain", new BigDecimal("100000"));
    repository.save(jack);
    
    // Act - wykonanie akcji
    Optional<Pirate> found = repository.findByName("Jack Sparrow");
    
    // Assert - weryfikacja wyniku
    assertThat(found).isPresent();
    assertThat(found.get().getName()).isEqualTo("Jack Sparrow");
}
```

### 2. Test integracyjny z H2

```java
class PirateRepositoryTest {
    
    private EntityManagerFactory emf;
    private EntityManager em;
    private PirateRepository repository;
    
    @BeforeEach
    void setUp() {
        // Użyj persistence unit z konfiguracją testową
        emf = Persistence.createEntityManagerFactory("test-pu");
        em = emf.createEntityManager();
        repository = new PirateRepository(em);
        
        em.getTransaction().begin();
    }
    
    @AfterEach
    void tearDown() {
        // Rollback - nie zapisuje zmian do bazy
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        em.close();
        emf.close();
    }
}
```

### 3. Mockowanie z Mockito

```java
@ExtendWith(MockitoExtension.class)
class PirateRepositoryUnitTest {
    
    @Mock
    private EntityManager em;
    
    @Mock
    private TypedQuery<Pirate> query;
    
    @InjectMocks
    private PirateRepository repository;
    
    @Test
    void shouldFindById() {
        // Arrange
        Pirate expected = new Pirate("Jack", "Captain", BigDecimal.ZERO);
        when(em.find(Pirate.class, 1L)).thenReturn(expected);
        
        // Act
        Optional<Pirate> result = repository.findById(1L);
        
        // Assert
        assertThat(result).contains(expected);
        verify(em).find(Pirate.class, 1L);
    }
}
```

## 🧪 AssertJ - fluent assertions

```java
// Podstawowe asercje
assertThat(pirate.getName()).isEqualTo("Jack Sparrow");
assertThat(pirate.getBounty()).isGreaterThan(BigDecimal.ZERO);

// Kolekcje
assertThat(pirates).hasSize(3)
                   .extracting(Pirate::getName)
                   .contains("Jack Sparrow", "Barbossa");

// Optional
assertThat(found).isPresent()
                 .hasValueSatisfying(p -> {
                     assertThat(p.getName()).isEqualTo("Jack");
                     assertThat(p.getRank()).isEqualTo("Captain");
                 });

// Exception
assertThatThrownBy(() -> repository.findById(null))
    .isInstanceOf(IllegalArgumentException.class)
    .hasMessageContaining("ID cannot be null");
```

## ⚠️ Typowe błędy

### 1. Brak transakcji w teście
```java
// ❌ ŹLE - persist nie działa bez transakcji
@Test
void test() {
    em.persist(pirate);  // Nie zapisze się!
}

// ✅ DOBRZE - transakcja w @BeforeEach
@BeforeEach
void setUp() {
    em.getTransaction().begin();
}
```

### 2. Współdzielony stan między testami
```java
// ❌ ŹLE - dane z poprzedniego testu wpływają na kolejny
private static EntityManager em;  // static = współdzielony!

// ✅ DOBRZE - nowy EntityManager per test
@BeforeEach
void setUp() {
    em = emf.createEntityManager();
}

@AfterEach
void tearDown() {
    em.close();
}
```

### 3. Brak izolacji - commit zamiast rollback
```java
// ❌ ŹLE - dane zostają w bazie
@AfterEach
void tearDown() {
    em.getTransaction().commit();  // Dane zostają!
}

// ✅ DOBRZE - rollback czyści dane
@AfterEach
void tearDown() {
    em.getTransaction().rollback();
}
```

## 🏃 Jak uruchomić

```bash
# Uruchom wszystkie testy
cd m08-testing-persistence
mvn test

# Uruchom konkretną klasę testową
mvn test -Dtest=PirateRepositoryTest

# Uruchom konkretny test
mvn test -Dtest=PirateRepositoryTest#shouldSaveAndFindPirate
```

## 📚 Najlepsze praktyki

1. **Jeden test = jedna asercja logiczna** - test sprawdza jedną rzecz
2. **Nazewnictwo**: `should<ExpectedBehavior>_when<Condition>`
3. **Izolacja** - każdy test jest niezależny
4. **Szybkość** - testy jednostkowe < 100ms, integracyjne < 1s
5. **Determinizm** - test zawsze daje ten sam wynik
6. **Brak side effects** - nie modyfikuj współdzielonego stanu

## 🔗 Powiązane moduły

- [m04-jpa-intro](../m04-jpa-intro) - podstawy JPA/Hibernate
- [m05-entity-lifecycle](../m05-entity-lifecycle) - cykl życia encji
- [m07-hql-queries](../m07-hql-queries) - zapytania do testowania

## ✏️ Ćwiczenia

### Ćwiczenie 1: Test CRUD ⭐
Napisz testy dla operacji Create, Read, Update, Delete.

### Ćwiczenie 2: Test walidacji ⭐⭐
Dodaj walidację (nazwa nie może być pusta) i napisz test.

### Ćwiczenie 3: Test wyjątków ⭐⭐
Napisz test sprawdzający że `findById(null)` rzuca wyjątek.

### Ćwiczenie 4: Mockowanie ⭐⭐⭐
Napisz test jednostkowy mockujący EntityManager.
