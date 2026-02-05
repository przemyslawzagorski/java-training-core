package pl.przemekzagorski.training.springdata.repository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import pl.przemekzagorski.training.springdata.entity.Pirate;
import pl.przemekzagorski.training.springdata.entity.Ship;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Testy integracyjne dla PirateRepository.
 *
 * @DataJpaTest zapewnia:
 * - Auto-konfigurację JPA (EntityManager, Repositories)
 * - Bazę H2 in-memory
 * - Automatyczny rollback po każdym teście
 * - TestEntityManager do przygotowania danych
 */
@DataJpaTest
@DisplayName("PirateRepository - testy integracyjne")
class PirateRepositoryTest {

    @Autowired
    private PirateRepository repository;

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Ship blackPearl;

    @BeforeEach
    void setUp() {
        // Tworzenie statku dla testów
        blackPearl = new Ship("Black Pearl", "Galleon", 32);
        entityManager.persist(blackPearl);
        entityManager.flush();
    }

    // ==========================================
    // Query Methods Tests
    // ==========================================

    @Nested
    @DisplayName("findByRank()")
    class FindByRankTests {

        @Test
        @DisplayName("powinien znaleźć piratów według rangi")
        void shouldFindPiratesByRank() {
            // Given
            entityManager.persist(new Pirate("Jack Sparrow", "Captain", new BigDecimal("100000")));
            entityManager.persist(new Pirate("Barbossa", "Captain", new BigDecimal("80000")));
            entityManager.persist(new Pirate("Gibbs", "Quartermaster", new BigDecimal("30000")));
            entityManager.flush();

            // When
            List<Pirate> captains = repository.findByRank("Captain");

            // Then
            assertThat(captains).hasSize(2);
            assertThat(captains).extracting(Pirate::getName)
                    .containsExactlyInAnyOrder("Jack Sparrow", "Barbossa");
        }

        @Test
        @DisplayName("powinien zwrócić pustą listę dla nieistniejącej rangi")
        void shouldReturnEmptyForNonExistentRank() {
            // Given
            entityManager.persist(new Pirate("Jack", "Captain", new BigDecimal("100000")));
            entityManager.flush();

            // When
            List<Pirate> admirals = repository.findByRank("Admiral");

            // Then
            assertThat(admirals).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByBountyGreaterThanOrderByBountyDesc()")
    class FindByBountyTests {

        @Test
        @DisplayName("powinien znaleźć i posortować piratów według bounty")
        void shouldFindAndSortByBounty() {
            // Given
            entityManager.persist(new Pirate("Jack", "Captain", new BigDecimal("100000")));
            entityManager.persist(new Pirate("Davy", "Captain", new BigDecimal("150000")));
            entityManager.persist(new Pirate("Will", "First Mate", new BigDecimal("50000")));
            entityManager.flush();

            // When
            List<Pirate> richPirates = repository.findByBountyGreaterThanOrderByBountyDesc(
                    new BigDecimal("60000"));

            // Then
            assertThat(richPirates).hasSize(2);
            assertThat(richPirates.get(0).getName()).isEqualTo("Davy");
            assertThat(richPirates.get(1).getName()).isEqualTo("Jack");
        }
    }

    @Nested
    @DisplayName("countByRank()")
    class CountByRankTests {

        @Test
        @DisplayName("powinien zwrócić poprawną liczbę")
        void shouldReturnCorrectCount() {
            // Given
            entityManager.persist(new Pirate("Jack", "Captain", new BigDecimal("100000")));
            entityManager.persist(new Pirate("Barbossa", "Captain", new BigDecimal("80000")));
            entityManager.persist(new Pirate("Gibbs", "Quartermaster", new BigDecimal("30000")));
            entityManager.flush();

            // When
            long count = repository.countByRank("Captain");

            // Then
            assertThat(count).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("existsByName()")
    class ExistsByNameTests {

        @Test
        @DisplayName("powinien zwrócić true dla istniejącego pirata")
        void shouldReturnTrueForExistingPirate() {
            // Given
            entityManager.persist(new Pirate("Jack Sparrow", "Captain", new BigDecimal("100000")));
            entityManager.flush();

            // When & Then
            assertThat(repository.existsByName("Jack Sparrow")).isTrue();
        }

        @Test
        @DisplayName("powinien zwrócić false dla nieistniejącego pirata")
        void shouldReturnFalseForNonExistingPirate() {
            // When & Then
            assertThat(repository.existsByName("Blackbeard")).isFalse();
        }
    }

    @Nested
    @DisplayName("findTop3ByOrderByBountyDesc()")
    class FindTop3Tests {

        @Test
        @DisplayName("powinien zwrócić top 3 piratów")
        void shouldReturnTop3Pirates() {
            // Given
            entityManager.persist(new Pirate("Jack", "Captain", new BigDecimal("100000")));
            entityManager.persist(new Pirate("Davy", "Captain", new BigDecimal("150000")));
            entityManager.persist(new Pirate("Barbossa", "Captain", new BigDecimal("80000")));
            entityManager.persist(new Pirate("Will", "First Mate", new BigDecimal("50000")));
            entityManager.persist(new Pirate("Gibbs", "Quartermaster", new BigDecimal("30000")));
            entityManager.flush();

            // When
            List<Pirate> top3 = repository.findTop3ByOrderByBountyDesc();

            // Then
            assertThat(top3).hasSize(3);
            assertThat(top3).extracting(Pirate::getName)
                    .containsExactly("Davy", "Jack", "Barbossa");
        }
    }

    // ==========================================
    // Custom @Query Tests
    // ==========================================

    @Nested
    @DisplayName("findMostWanted()")
    class FindMostWantedTests {

        @Test
        @DisplayName("powinien znaleźć pirata z największym bounty")
        void shouldFindPirateWithHighestBounty() {
            // Given
            entityManager.persist(new Pirate("Jack", "Captain", new BigDecimal("100000")));
            entityManager.persist(new Pirate("Davy", "Captain", new BigDecimal("150000")));
            entityManager.flush();

            // When
            Optional<Pirate> mostWanted = repository.findMostWanted();

            // Then
            assertThat(mostWanted).isPresent();
            assertThat(mostWanted.get().getName()).isEqualTo("Davy");
        }

        @Test
        @DisplayName("powinien zwrócić empty dla pustej bazy")
        void shouldReturnEmptyForEmptyDatabase() {
            // When
            Optional<Pirate> mostWanted = repository.findMostWanted();

            // Then
            assertThat(mostWanted).isEmpty();
        }
    }

    @Nested
    @DisplayName("sumAllBounties()")
    class SumAllBountiesTests {

        @Test
        @DisplayName("powinien zsumować wszystkie bounty")
        void shouldSumAllBounties() {
            // Given
            entityManager.persist(new Pirate("Jack", "Captain", new BigDecimal("100000")));
            entityManager.persist(new Pirate("Will", "First Mate", new BigDecimal("50000")));
            entityManager.flush();

            // When
            BigDecimal sum = repository.sumAllBounties();

            // Then
            assertThat(sum).isEqualByComparingTo("150000");
        }
    }

    @Nested
    @DisplayName("findByShipIsNull()")
    class FindByShipIsNullTests {

        @Test
        @DisplayName("powinien znaleźć piratów bez statku")
        void shouldFindPiratesWithoutShip() {
            // Given
            Pirate jack = new Pirate("Jack", "Captain", new BigDecimal("100000"));
            jack.setShip(blackPearl);
            entityManager.persist(jack);

            Pirate homeless = new Pirate("Homeless", "Sailor", new BigDecimal("1000"));
            entityManager.persist(homeless);
            entityManager.flush();

            // When
            List<Pirate> withoutShip = repository.findByShipIsNull();

            // Then
            assertThat(withoutShip).hasSize(1);
            assertThat(withoutShip.get(0).getName()).isEqualTo("Homeless");
        }
    }
}
