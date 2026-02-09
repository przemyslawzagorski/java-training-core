package pl.przemekzagorski.training.springdata.repository;

import jakarta.validation.ConstraintViolationException;
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
 * 🏴‍☠️ ROZWIĄZANIA - ShipRepository Testy
 *
 * Ten plik zawiera GOTOWE ROZWIĄZANIA wszystkich ćwiczeń z ShipRepositoryTest.
 *
 * ⚠️ UWAGA DLA KURSANTÓW:
 * - NIE ZAGLĄDAJ tutaj przed rozwiązaniem ćwiczeń!
 * - Najpierw spróbuj sam w ShipRepositoryTest.java
 * - Tutaj sprawdzisz swoje rozwiązanie
 *
 * @DataJpaTest zapewnia:
 * - Auto-konfigurację JPA (EntityManager, Repositories)
 * - Bazę H2 in-memory
 * - Automatyczny rollback po każdym teście
 * - TestEntityManager do przygotowania danych
 */
@DataJpaTest
@DisplayName("ShipRepository - ROZWIĄZANIA")
class ShipRepositoryTestSolutions {

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private PirateRepository pirateRepository;

    @Autowired
    private TestEntityManager entityManager;

    // ========================================================================
    // POZIOM 1: Query Methods ⭐
    // ========================================================================

    @Nested
    @DisplayName("⭐ POZIOM 1: Query Methods - ROZWIĄZANIA")
    class Level1_QueryMethods_Solutions {

        /**
         * ✅ ROZWIĄZANIE ĆWICZENIA 1: findByType()
         */
        @Test
        @DisplayName("ROZWIĄZANIE 1: findByType() - znajdź statki według typu")
        void solution1_shouldFindShipsByType() {
            // Arrange - przygotuj 3 statki (2 Galleon, 1 Brig)
            entityManager.persist(new Ship("Black Pearl", "Galleon", 32));
            entityManager.persist(new Ship("Flying Dutchman", "Galleon", 48));
            entityManager.persist(new Ship("Interceptor", "Brig", 16));
            entityManager.flush();

            // Act - znajdź wszystkie Galleon
            List<Ship> galleons = shipRepository.findByType("Galleon");

            // Assert - sprawdź że znaleziono 2 statki
            assertThat(galleons).hasSize(2);
            assertThat(galleons).extracting(Ship::getName)
                    .containsExactlyInAnyOrder("Black Pearl", "Flying Dutchman");
        }
    }

    // ========================================================================
    // POZIOM 2: Relacje @OneToMany ⭐⭐
    // ========================================================================

    @Nested
    @DisplayName("⭐⭐ POZIOM 2: Relacje @OneToMany - ROZWIĄZANIA")
    class Level2_Relations_Solutions {

        /**
         * ✅ ROZWIĄZANIE ĆWICZENIA 2: countCrewMembers()
         */
        @Test
        @DisplayName("ROZWIĄZANIE 2: countCrewMembers() - policz załogę")
        void solution2_shouldCountCrewMembers() {
            // Arrange - stwórz statek z 3 piratami
            Ship blackPearl = new Ship("Black Pearl", "Galleon", 32);
            blackPearl.addCrewMember(new Pirate("Jack", "Captain", new BigDecimal("100000")));
            blackPearl.addCrewMember(new Pirate("Gibbs", "First Mate", new BigDecimal("20000")));
            blackPearl.addCrewMember(new Pirate("Cotton", "Sailor", new BigDecimal("5000")));

            entityManager.persist(blackPearl);
            entityManager.flush();

            // Act - policz załogę
            int crewCount = shipRepository.countCrewMembers(blackPearl.getId());

            // Assert - sprawdź że jest 3 piratów
            assertThat(crewCount).isEqualTo(3);
        }

        @Test
        @DisplayName("ROZWIĄZANIE 2 (bonus): countCrewMembers() - pusty statek")
        void solution2_bonus_shouldReturnZeroForEmptyShip() {
            // Arrange - stwórz pusty statek
            Ship emptyShip = new Ship("Empty Ship", "Brig", 10);
            entityManager.persist(emptyShip);
            entityManager.flush();

            // Act
            int crewCount = shipRepository.countCrewMembers(emptyShip.getId());

            // Assert - sprawdź że jest 0 piratów
            assertThat(crewCount).isEqualTo(0);
        }
    }

    // ========================================================================
    // POZIOM 3: JOIN FETCH i N+1 Problem ⭐⭐⭐
    // ========================================================================

    @Nested
    @DisplayName("⭐⭐⭐ POZIOM 3: JOIN FETCH i N+1 Problem - ROZWIĄZANIA")
    class Level3_JoinFetch_Solutions {

        /**
         * ✅ ROZWIĄZANIE ĆWICZENIA 3: Demonstracja N+1 Problem
         */
        @Test
        @DisplayName("ROZWIĄZANIE 3: Porównaj findById() vs findByIdWithCrew()")
        void solution3_shouldDemonstrateNPlusOneProblem() {
            // Arrange - stwórz statek z 2 piratami
            Ship blackPearl = new Ship("Black Pearl", "Galleon", 32);
            blackPearl.addCrewMember(new Pirate("Jack", "Captain", new BigDecimal("100000")));
            blackPearl.addCrewMember(new Pirate("Gibbs", "First Mate", new BigDecimal("20000")));

            entityManager.persist(blackPearl);
            entityManager.flush();
            entityManager.clear(); // Wyczyść cache

            // Act 1: findById() - N+1 problem
            Ship ship1 = shipRepository.findById(blackPearl.getId()).orElseThrow();
            int crewSize1 = ship1.getCrew().size(); // Dodatkowe SELECT dla załogi!

            entityManager.clear(); // Wyczyść cache przed drugim testem

            // Act 2: findByIdWithCrew() - JOIN FETCH
            Ship ship2 = shipRepository.findByIdWithCrew(blackPearl.getId()).orElseThrow();
            int crewSize2 = ship2.getCrew().size(); // Załoga już załadowana - brak dodatkowego SELECT

            // Assert - obie metody zwracają tę samą liczbę piratów
            assertThat(crewSize1).isEqualTo(crewSize2).isEqualTo(2);

            // 💡 SPRAWDŹ LOGI SQL:
            // findById() wykonuje 2 SELECT:
            //   1. SELECT dla Ship
            //   2. SELECT dla Crew (N+1 problem!)
            //
            // findByIdWithCrew() wykonuje 1 SELECT:
            //   1. SELECT z JOIN FETCH - wszystko w jednym zapytaniu
        }
    }

    // ========================================================================
    // POZIOM 4: Cascade i OrphanRemoval ⭐⭐⭐⭐
    // ========================================================================

    @Nested
    @DisplayName("⭐⭐⭐⭐ POZIOM 4: Cascade i OrphanRemoval - ROZWIĄZANIA")
    class Level4_CascadeAndOrphanRemoval_Solutions {

        /**
         * ✅ ROZWIĄZANIE ĆWICZENIA 4: Cascade.ALL
         */
        @Test
        @DisplayName("ROZWIĄZANIE 4: Cascade.ALL - zapisz statek z załogą")
        void solution4_shouldSaveShipWithCrew() {
            // Arrange - stwórz statek z 2 piratami
            Ship blackPearl = new Ship("Black Pearl", "Galleon", 32);
            blackPearl.addCrewMember(new Pirate("Jack", "Captain", new BigDecimal("100000")));
            blackPearl.addCrewMember(new Pirate("Gibbs", "First Mate", new BigDecimal("20000")));

            // Act - zapisz TYLKO statek, załoga zapisze się automatycznie dzięki Cascade.ALL
            Ship saved = shipRepository.save(blackPearl);
            entityManager.flush();
            entityManager.clear();

            // Assert - sprawdź że załoga została zapisana
            Ship found = shipRepository.findByIdWithCrew(saved.getId()).orElseThrow();
            assertThat(found.getCrew()).hasSize(2);
            assertThat(found.getCrew()).extracting(Pirate::getName)
                    .containsExactlyInAnyOrder("Jack", "Gibbs");

            // 💡 KLUCZOWA KONCEPCJA:
            // Dzięki @OneToMany(cascade = CascadeType.ALL) w Ship.java
            // nie musimy ręcznie zapisywać każdego Pirate - Hibernate robi to za nas!
        }
    }

    // ========================================================================
    // POZIOM 5: Bean Validation ⭐⭐⭐⭐⭐
    // ========================================================================

    @Nested
    @DisplayName("⭐⭐⭐⭐⭐ POZIOM 5: Bean Validation - ROZWIĄZANIA")
    class Level5_BeanValidation_Solutions {

        /**
         * ✅ ROZWIĄZANIE ĆWICZENIA 5: @Min - liczba dział nie może być ujemna
         */
        @Test
        @DisplayName("ROZWIĄZANIE 5: @Min - liczba dział nie może być ujemna")
        void solution5_shouldThrowWhenCannonsNegative() {
            // Arrange - stwórz statek z UJEMNĄ liczbą dział
            Ship ship = new Ship("Black Pearl", "Galleon", -10);

            // Act & Assert - sprawdź że rzuca wyjątek
            assertThatThrownBy(() -> {
                shipRepository.save(ship);
                entityManager.flush(); // Walidacja działa podczas flush()
            })
                    .isInstanceOf(ConstraintViolationException.class)
                    .hasMessageContaining("nie może być ujemna");

            // 💡 KLUCZOWA KONCEPCJA:
            // @Min(value = 0, message = "Liczba dział nie może być ujemna") w Ship.java
            // zapobiega zapisaniu nieprawidłowych danych do bazy
        }

        /**
         * ✅ ROZWIĄZANIE BONUS: @Max - maksymalnie 200 dział
         */
        @Test
        @DisplayName("ROZWIĄZANIE BONUS: @Max - maksymalnie 200 dział")
        void solutionBonus_shouldThrowWhenCannonsTooMany() {
            // Arrange - stwórz statek z ZA DUŻĄ liczbą dział
            Ship ship = new Ship("Super Ship", "Galleon", 300);

            // Act & Assert
            assertThatThrownBy(() -> {
                shipRepository.save(ship);
                entityManager.flush();
            })
                    .isInstanceOf(ConstraintViolationException.class)
                    .hasMessageContaining("Maksymalnie 200 dział");
        }

        /**
         * ✅ ROZWIĄZANIE BONUS: @NotBlank - nazwa nie może być pusta
         */
        @Test
        @DisplayName("ROZWIĄZANIE BONUS: @NotBlank - nazwa nie może być pusta")
        void solutionBonus_shouldThrowWhenNameIsBlank() {
            // Arrange - stwórz statek z PUSTĄ nazwą
            Ship ship = new Ship("", "Galleon", 32);

            // Act & Assert
            assertThatThrownBy(() -> {
                shipRepository.save(ship);
                entityManager.flush();
            })
                    .isInstanceOf(ConstraintViolationException.class)
                    .hasMessageContaining("Nazwa statku jest wymagana");
        }
    }
}

