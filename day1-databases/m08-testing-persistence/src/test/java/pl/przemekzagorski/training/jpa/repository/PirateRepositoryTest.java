package pl.przemekzagorski.training.jpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;
import pl.przemekzagorski.training.jpa.entity.Pirate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Testy integracyjne dla PirateRepository.
 *
 * Używają prawdziwej bazy H2 in-memory.
 * Każdy test jest izolowany przez rollback transakcji.
 *
 * WZORZEC: Arrange-Act-Assert (AAA)
 */
@DisplayName("PirateRepository - Testy Integracyjne")
class PirateRepositoryTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private PirateRepository repository;

    @BeforeAll
    static void setUpFactory() {
        // Jeden EMF dla wszystkich testów (wydajność)
        emf = Persistence.createEntityManagerFactory("test-pu");
    }

    @AfterAll
    static void tearDownFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @BeforeEach
    void setUp() {
        // Nowy EM dla każdego testu (izolacja)
        em = emf.createEntityManager();
        repository = new PirateRepository(em);

        // Rozpocznij transakcję - będzie wycofana po teście
        em.getTransaction().begin();
    }

    @AfterEach
    void tearDown() {
        // Rollback - dane testowe nie zostają w bazie
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    // ========================================================================
    // Testy CRUD - Create
    // ========================================================================

    @Test
    @DisplayName("should save new pirate and generate ID")
    void shouldSaveNewPirate() {
        // Arrange
        Pirate jack = new Pirate("Jack Sparrow", "Captain", new BigDecimal("100000"));

        // Act
        Pirate saved = repository.save(jack);
        em.flush();  // Wymuś INSERT

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Jack Sparrow");

        // 🎯 ĆWICZENIE 1: Dodaj dodatkowe asercje
        // TODO: Sprawdź że saved.getRank() == "Captain"
        // TODO: Sprawdź że saved.getBounty() == 100000
        // Hint: assertThat(saved.getRank()).isEqualTo("Captain");
        // Hint: assertThat(saved.getBounty()).isEqualByComparingTo(new BigDecimal("100000"));
    }

    @Test
    @DisplayName("should throw exception when saving null pirate")
    void shouldThrowWhenSavingNull() {
        // Act & Assert
        assertThatThrownBy(() -> repository.save(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
    }

    // ========================================================================
    // Testy CRUD - Read
    // ========================================================================

    @Test
    @DisplayName("should find pirate by ID")
    void shouldFindById() {
        // Arrange
        Pirate jack = new Pirate("Jack Sparrow", "Captain", new BigDecimal("100000"));
        repository.save(jack);
        em.flush();
        em.clear();  // Wyczyść cache 1st level

        // Act
        Optional<Pirate> found = repository.findById(jack.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Jack Sparrow");
    }

    @Test
    @DisplayName("should return empty Optional when ID not found")
    void shouldReturnEmptyWhenNotFound() {
        // Act
        Optional<Pirate> found = repository.findById(999L);

        // Assert
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("should throw exception when finding by null ID")
    void shouldThrowWhenFindingByNullId() {
        // Act & Assert
        assertThatThrownBy(() -> repository.findById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID cannot be null");
    }

    @Test
    @DisplayName("should find pirate by name")
    void shouldFindByName() {
        // Arrange
        Pirate barbossa = new Pirate("Hector Barbossa", "Captain", new BigDecimal("80000"));
        repository.save(barbossa);
        em.flush();
        em.clear();

        // Act
        Optional<Pirate> found = repository.findByName("Hector Barbossa");

        // Assert
        assertThat(found).isPresent()
                         .hasValueSatisfying(p -> {
                             assertThat(p.getName()).isEqualTo("Hector Barbossa");
                             assertThat(p.getRank()).isEqualTo("Captain");
                         });
    }

    @Test
    @DisplayName("should find all pirates ordered by name")
    void shouldFindAll() {
        // Arrange
        repository.save(new Pirate("Barbossa", "Captain", new BigDecimal("80000")));
        repository.save(new Pirate("Jack Sparrow", "Captain", new BigDecimal("100000")));
        repository.save(new Pirate("Gibbs", "First Mate", new BigDecimal("20000")));
        em.flush();
        em.clear();

        // Act
        List<Pirate> all = repository.findAll();

        // Assert
        assertThat(all).hasSize(3)
                       .extracting(Pirate::getName)
                       .containsExactly("Barbossa", "Gibbs", "Jack Sparrow");

        // 🎯 ĆWICZENIE 2: Dodaj dodatkowe asercje dla kolekcji
        // TODO: Sprawdź że pierwszy pirat (all.get(0)) to Barbossa
        // TODO: Sprawdź że ostatni pirat (all.get(2)) to Jack Sparrow
        // TODO: Sprawdź że wszyscy piraci mają bounty większe niż 0
        // Hint: assertThat(all.get(0).getName()).isEqualTo("Barbossa");
        // Hint: assertThat(all).allSatisfy(p -> assertThat(p.getBounty()).isGreaterThan(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("should find pirates by rank")
    void shouldFindByRank() {
        // Arrange
        repository.save(new Pirate("Jack", "Captain", new BigDecimal("100000")));
        repository.save(new Pirate("Barbossa", "Captain", new BigDecimal("80000")));
        repository.save(new Pirate("Gibbs", "First Mate", new BigDecimal("20000")));
        em.flush();
        em.clear();

        // Act
        List<Pirate> captains = repository.findByRank("Captain");

        // Assert
        assertThat(captains).hasSize(2)
                           .extracting(Pirate::getName)
                           .containsExactlyInAnyOrder("Jack", "Barbossa");

        // 🎯 ĆWICZENIE 3: Dodaj test dla innej rangi
        // TODO: Wywołaj repository.findByRank("First Mate")
        // TODO: Sprawdź że zwrócono 1 pirata
        // TODO: Sprawdź że to Gibbs
        // Hint: List<Pirate> firstMates = repository.findByRank("First Mate");
        // Hint: assertThat(firstMates).hasSize(1);
        // Hint: assertThat(firstMates.get(0).getName()).isEqualTo("Gibbs");
    }

    @Test
    @DisplayName("should find pirates by bounty greater than")
    void shouldFindByBountyGreaterThan() {
        // Arrange
        repository.save(new Pirate("Davy Jones", "Captain", new BigDecimal("500000")));
        repository.save(new Pirate("Jack", "Captain", new BigDecimal("100000")));
        repository.save(new Pirate("Gibbs", "First Mate", new BigDecimal("20000")));
        em.flush();
        em.clear();

        // Act
        List<Pirate> wanted = repository.findByBountyGreaterThan(new BigDecimal("50000"));

        // Assert
        assertThat(wanted).hasSize(2)
                         .extracting(Pirate::getName)
                         .containsExactly("Davy Jones", "Jack");  // Posortowane desc
    }

    // ========================================================================
    // Testy CRUD - Update
    // ========================================================================

    @Test
    @DisplayName("should update existing pirate")
    void shouldUpdatePirate() {
        // Arrange
        Pirate jack = new Pirate("Jack Sparrow", "Captain", new BigDecimal("100000"));
        repository.save(jack);
        em.flush();
        em.clear();

        // Act
        Pirate toUpdate = repository.findById(jack.getId()).orElseThrow();
        toUpdate.setBounty(new BigDecimal("150000"));
        repository.save(toUpdate);
        em.flush();
        em.clear();

        // Assert
        Pirate updated = repository.findById(jack.getId()).orElseThrow();
        assertThat(updated.getBounty()).isEqualByComparingTo(new BigDecimal("150000"));

        // 🎯 ĆWICZENIE 4: Dodaj asercje sprawdzające że inne pola się nie zmieniły
        // TODO: Sprawdź że nazwa nadal to "Jack Sparrow"
        // TODO: Sprawdź że ranga nadal to "Captain"
        // TODO: Sprawdź że ID się nie zmieniło (porównaj z jack.getId())
        // Hint: assertThat(updated.getName()).isEqualTo("Jack Sparrow");
        // Hint: assertThat(updated.getId()).isEqualTo(jack.getId());
    }

    // ========================================================================
    // Testy CRUD - Delete
    // ========================================================================

    @Test
    @DisplayName("should delete pirate")
    void shouldDeletePirate() {
        // Arrange
        Pirate jack = new Pirate("Jack Sparrow", "Captain", new BigDecimal("100000"));
        repository.save(jack);
        em.flush();

        // Act
        repository.delete(jack);
        em.flush();
        em.clear();

        // Assert
        assertThat(repository.findById(jack.getId())).isEmpty();
    }

    @Test
    @DisplayName("should delete pirate by ID and return true")
    void shouldDeleteByIdAndReturnTrue() {
        // Arrange
        Pirate jack = new Pirate("Jack Sparrow", "Captain", new BigDecimal("100000"));
        repository.save(jack);
        em.flush();

        // Act
        boolean deleted = repository.deleteById(jack.getId());
        em.flush();
        em.clear();

        // Assert
        assertThat(deleted).isTrue();
        assertThat(repository.existsById(jack.getId())).isFalse();
    }

    @Test
    @DisplayName("should return false when deleting non-existent ID")
    void shouldReturnFalseWhenDeletingNonExistent() {
        // Act
        boolean deleted = repository.deleteById(999L);

        // Assert
        assertThat(deleted).isFalse();
    }

    // ========================================================================
    // Testy pomocnicze
    // ========================================================================

    @Test
    @DisplayName("should count all pirates")
    void shouldCountPirates() {
        // Arrange
        repository.save(new Pirate("Jack", "Captain", BigDecimal.ZERO));
        repository.save(new Pirate("Barbossa", "Captain", BigDecimal.ZERO));
        repository.save(new Pirate("Gibbs", "First Mate", BigDecimal.ZERO));
        em.flush();

        // Act
        long count = repository.count();

        // Assert
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("should return true when pirate exists")
    void shouldReturnTrueWhenExists() {
        // Arrange
        Pirate jack = new Pirate("Jack", "Captain", BigDecimal.ZERO);
        repository.save(jack);
        em.flush();

        // Act & Assert
        assertThat(repository.existsById(jack.getId())).isTrue();
    }

    @Test
    @DisplayName("should return false when pirate does not exist")
    void shouldReturnFalseWhenNotExists() {
        // Act & Assert
        assertThat(repository.existsById(999L)).isFalse();
    }

    // ========================================================================
    // ĆWICZENIA - napisz nowe testy od zera
    // ========================================================================

    /**
     * 🎯 ĆWICZENIE 5: Test walidacji - nazwa nie może być pusta
     *
     * Zadanie:
     * Napisz test sprawdzający że save() rzuca wyjątek gdy nazwa jest pusta.
     *
     * STRUKTURA:
     * 1. Arrange: Utwórz pirata z pustą nazwą (new Pirate("", "Captain", BigDecimal.ZERO))
     * 2. Act & Assert: Użyj assertThatThrownBy(() -> repository.save(pirate))
     * 3. Sprawdź typ wyjątku i komunikat
     *
     * 💡 WSKAZÓWKI:
     * - assertThatThrownBy(() -> ...) - sprawdza że kod rzuca wyjątek
     * - .isInstanceOf(IllegalArgumentException.class) - sprawdza typ
     * - .hasMessageContaining("name") - sprawdza fragment komunikatu
     *
     * 🆘 Jeśli utkniesz, sprawdź shouldThrowWhenSavingNull() powyżej
     */
    @Test
    @DisplayName("🎯 ĆWICZENIE 5: should throw when name is empty")
    @Disabled("Odkomentuj @Disabled gdy będziesz gotowy")
    void exercise5_shouldThrowWhenNameIsEmpty() {
        // TODO: Napisz test sprawdzający walidację pustej nazwy
        // Hint: Pirate pirate = new Pirate("", "Captain", BigDecimal.ZERO);
        // Hint: assertThatThrownBy(() -> repository.save(pirate))
        //           .isInstanceOf(IllegalArgumentException.class)
        //           .hasMessageContaining("name");
    }

    /**
     * 🎯 ĆWICZENIE 6: Test deleteById - zwraca false gdy nie znaleziono
     *
     * Zadanie:
     * Napisz test sprawdzający że deleteById(999L) zwraca false.
     *
     * STRUKTURA:
     * 1. Act: Wywołaj repository.deleteById(999L)
     * 2. Assert: Sprawdź że zwrócono false
     *
     * 💡 WSKAZÓWKI:
     * - boolean deleted = repository.deleteById(999L);
     * - assertThat(deleted).isFalse();
     *
     * 🆘 Jeśli utkniesz, sprawdź shouldReturnFalseWhenDeletingNonExistent() powyżej
     */
    @Test
    @DisplayName("🎯 ĆWICZENIE 6: should return false when deleting non-existent ID")
    @Disabled("Odkomentuj @Disabled gdy będziesz gotowy")
    void exercise6_shouldReturnFalseWhenDeletingNonExistent() {
        // TODO: Napisz test sprawdzający deleteById dla nieistniejącego ID
        // Hint: boolean deleted = repository.deleteById(999L);
        // Hint: assertThat(deleted).isFalse();
    }

    /**
     * 🎯 ĆWICZENIE 7: Test findByName - zwraca empty gdy nie znaleziono
     *
     * Zadanie:
     * Napisz test sprawdzający że findByName("Unknown") zwraca pusty Optional.
     *
     * STRUKTURA:
     * 1. Act: Wywołaj repository.findByName("Unknown")
     * 2. Assert: Sprawdź że Optional jest pusty
     *
     * 💡 WSKAZÓWKI:
     * - Optional<Pirate> found = repository.findByName("Unknown");
     * - assertThat(found).isEmpty();
     *
     * 🆘 Jeśli utkniesz, sprawdź shouldReturnEmptyWhenNotFound() powyżej
     */
    @Test
    @DisplayName("🎯 ĆWICZENIE 7: should return empty when name not found")
    @Disabled("Odkomentuj @Disabled gdy będziesz gotowy")
    void exercise7_shouldReturnEmptyWhenNameNotFound() {
        // TODO: Napisz test sprawdzający findByName dla nieistniejącej nazwy
        // Hint: Optional<Pirate> found = repository.findByName("Unknown");
        // Hint: assertThat(found).isEmpty();
    }

    /**
     * 🎯 ĆWICZENIE 8: Test findByBountyGreaterThan - pusta lista
     *
     * Zadanie:
     * Napisz test sprawdzający że findByBountyGreaterThan zwraca pustą listę
     * gdy żaden pirat nie spełnia warunku.
     *
     * STRUKTURA:
     * 1. Arrange: Zapisz pirata z bounty = 10,000
     * 2. Act: Wywołaj findByBountyGreaterThan(50,000)
     * 3. Assert: Sprawdź że lista jest pusta
     *
     * 💡 WSKAZÓWKI:
     * - repository.save(new Pirate("Jack", "Captain", new BigDecimal("10000")));
     * - em.flush(); em.clear();
     * - List<Pirate> result = repository.findByBountyGreaterThan(new BigDecimal("50000"));
     * - assertThat(result).isEmpty();
     *
     * 🆘 Jeśli utkniesz, sprawdź shouldFindByBountyGreaterThan() powyżej
     */
    @Test
    @DisplayName("🎯 ĆWICZENIE 8: should return empty list when no pirates match bounty")
    @Disabled("Odkomentuj @Disabled gdy będziesz gotowy")
    void exercise8_shouldReturnEmptyWhenNoPiratesMatchBounty() {
        // TODO: Napisz test sprawdzający pustą listę dla findByBountyGreaterThan
        // Hint: Zapisz pirata z małym bounty (10000)
        // Hint: Szukaj piratów z bounty > 50000
        // Hint: assertThat(result).isEmpty();
    }
}

