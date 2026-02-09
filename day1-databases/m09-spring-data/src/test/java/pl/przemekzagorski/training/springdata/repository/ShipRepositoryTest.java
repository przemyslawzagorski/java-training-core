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
 * 🏴‍☠️ Testy integracyjne dla ShipRepository
 *
 * Ten plik zawiera:
 * - ✅ Przykładowe testy (rozwiązania) - do nauki
 * - 🎯 Ćwiczenia dla kursantów - do samodzielnego rozwiązania
 *
 * @DataJpaTest zapewnia:
 * - Auto-konfigurację JPA (EntityManager, Repositories)
 * - Bazę H2 in-memory
 * - Automatyczny rollback po każdym teście
 * - TestEntityManager do przygotowania danych
 */
@DataJpaTest
@DisplayName("ShipRepository - testy integracyjne")
class ShipRepositoryTest {

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
    @DisplayName("⭐ POZIOM 1: Query Methods")
    class Level1_QueryMethods {

        /**
         * ✅ PRZYKŁAD: Test findByName()
         *
         * Pokazuje:
         * - Podstawowy wzorzec AAA (Arrange-Act-Assert)
         * - Użycie TestEntityManager do przygotowania danych
         * - AssertJ fluent assertions
         */
        @Test
        @DisplayName("✅ PRZYKŁAD: findByName() - powinien znaleźć statek po nazwie")
        void example_shouldFindShipByName() {
            // Arrange - przygotuj dane
            Ship blackPearl = new Ship("Black Pearl", "Galleon", 32);
            entityManager.persist(blackPearl);
            entityManager.flush();

            // Act - wykonaj akcję
            Optional<Ship> found = shipRepository.findByName("Black Pearl");

            // Assert - sprawdź wynik
            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("Black Pearl");
            assertThat(found.get().getType()).isEqualTo("Galleon");
            assertThat(found.get().getCannons()).isEqualTo(32);
        }

        /**
         * 🎯 ĆWICZENIE 1: Test findByType()
         *
         * ZADANIE:
         * Napisz test sprawdzający że findByType("Galleon") zwraca wszystkie galiony.
         *
         * KROKI:
         * 1. Arrange: Zapisz 3 statki - 2 Galleon, 1 Brig
         * 2. Act: Wywołaj shipRepository.findByType("Galleon")
         * 3. Assert: Sprawdź że zwrócono 2 statki
         *
         * 💡 PODPOWIEDZI:
         * - entityManager.persist(new Ship("Black Pearl", "Galleon", 32));
         * - entityManager.persist(new Ship("Flying Dutchman", "Galleon", 48));
         * - entityManager.persist(new Ship("Interceptor", "Brig", 16));
         * - entityManager.flush();
         * - List<Ship> galleons = shipRepository.findByType("Galleon");
         * - assertThat(galleons).hasSize(2);
         * - assertThat(galleons).extracting(Ship::getName)
         *       .containsExactlyInAnyOrder("Black Pearl", "Flying Dutchman");
         */
        @Test
        @DisplayName("🎯 ĆWICZENIE 1: findByType() - znajdź statki według typu")
        @Disabled("Usuń @Disabled i napisz test")
        void exercise1_shouldFindShipsByType() {
            // TODO: Napisz test dla findByType()
            fail("Usuń fail() i zaimplementuj test");
        }
    }

    // ========================================================================
    // POZIOM 2: Relacje @OneToMany ⭐⭐
    // ========================================================================

    @Nested
    @DisplayName("⭐⭐ POZIOM 2: Relacje @OneToMany")
    class Level2_Relations {

        /**
         * ✅ PRZYKŁAD: Test findShipsWithCrew()
         *
         * Pokazuje:
         * - Testowanie relacji @OneToMany
         * - Użycie addCrewMember() do zarządzania relacją
         * - Custom @Query z JOIN
         */
        @Test
        @DisplayName("✅ PRZYKŁAD: findShipsWithCrew() - znajdź statki z załogą")
        void example_shouldFindShipsWithCrew() {
            // Arrange
            Ship blackPearl = new Ship("Black Pearl", "Galleon", 32);
            Ship emptyShip = new Ship("Empty Ship", "Brig", 10);

            Pirate jack = new Pirate("Jack Sparrow", "Captain", new BigDecimal("100000"));
            blackPearl.addCrewMember(jack);

            entityManager.persist(blackPearl);
            entityManager.persist(emptyShip);
            entityManager.flush();

            // Act
            List<Ship> shipsWithCrew = shipRepository.findShipsWithCrew();

            // Assert
            assertThat(shipsWithCrew).hasSize(1);
            assertThat(shipsWithCrew.get(0).getName()).isEqualTo("Black Pearl");
        }

        /**
         * 🎯 ĆWICZENIE 2: Test countCrewMembers()
         *
         * ZADANIE:
         * Napisz test sprawdzający że countCrewMembers() zwraca poprawną liczbę piratów na statku.
         *
         * KROKI:
         * 1. Arrange: Stwórz statek i dodaj 3 piratów do załogi
         * 2. Act: Wywołaj shipRepository.countCrewMembers(shipId)
         * 3. Assert: Sprawdź że zwrócono 3
         *
         * 💡 PODPOWIEDZI:
         * - Ship blackPearl = new Ship("Black Pearl", "Galleon", 32);
         * - blackPearl.addCrewMember(new Pirate("Jack", "Captain", new BigDecimal("100000")));
         * - blackPearl.addCrewMember(new Pirate("Gibbs", "First Mate", new BigDecimal("20000")));
         * - blackPearl.addCrewMember(new Pirate("Cotton", "Sailor", new BigDecimal("5000")));
         * - entityManager.persist(blackPearl);
         * - entityManager.flush();
         * - int crewCount = shipRepository.countCrewMembers(blackPearl.getId());
         * - assertThat(crewCount).isEqualTo(3);
         */
        @Test
        @DisplayName("🎯 ĆWICZENIE 2: countCrewMembers() - policz załogę")
        @Disabled("Usuń @Disabled i napisz test")
        void exercise2_shouldCountCrewMembers() {
            // TODO: Napisz test dla countCrewMembers()
            fail("Usuń fail() i zaimplementuj test");
        }
    }

    // ========================================================================
    // POZIOM 3: JOIN FETCH i N+1 Problem ⭐⭐⭐
    // ========================================================================

    @Nested
    @DisplayName("⭐⭐⭐ POZIOM 3: JOIN FETCH i N+1 Problem")
    class Level3_JoinFetch {

        /**
         * ✅ PRZYKŁAD: Test findByIdWithCrew() - JOIN FETCH
         *
         * Pokazuje:
         * - Różnicę między LAZY loading a JOIN FETCH
         * - Jak uniknąć problemu N+1
         * - Użycie entityManager.clear() do wyczyszczenia cache
         */
        @Test
        @DisplayName("✅ PRZYKŁAD: findByIdWithCrew() - załaduj statek z załogą (JOIN FETCH)")
        void example_shouldLoadShipWithCrew() {
            // Arrange
            Ship blackPearl = new Ship("Black Pearl", "Galleon", 32);
            blackPearl.addCrewMember(new Pirate("Jack", "Captain", new BigDecimal("100000")));
            blackPearl.addCrewMember(new Pirate("Gibbs", "First Mate", new BigDecimal("20000")));

            entityManager.persist(blackPearl);
            entityManager.flush();
            entityManager.clear(); // Wyczyść cache - wymusza SELECT z bazy

            // Act
            Optional<Ship> found = shipRepository.findByIdWithCrew(blackPearl.getId());

            // Assert
            assertThat(found).isPresent();

            Ship ship = found.get();
            assertThat(ship.getName()).isEqualTo("Black Pearl");

            // Załoga załadowana w jednym SELECT (JOIN FETCH) - brak dodatkowych zapytań!
            assertThat(ship.getCrew()).hasSize(2);
            assertThat(ship.getCrew()).extracting(Pirate::getName)
                    .containsExactlyInAnyOrder("Jack", "Gibbs");
        }

        /**
         * 🎯 ĆWICZENIE 3: Demonstracja N+1 Problem
         *
         * ZADANIE:
         * Napisz test porównujący findById() (N+1 problem) z findByIdWithCrew() (JOIN FETCH).
         *
         * KROKI:
         * 1. Arrange: Stwórz statek z 2 piratami
         * 2. Act: Wywołaj findById() i dostęp do getCrew() - spowoduje dodatkowe SELECT
         * 3. Act: Wywołaj findByIdWithCrew() - załoga załadowana w jednym SELECT
         * 4. Assert: Sprawdź że obie metody zwracają tę samą liczbę piratów
         *
         * 💡 PODPOWIEDZI:
         * - Ship blackPearl = new Ship("Black Pearl", "Galleon", 32);
         * - blackPearl.addCrewMember(new Pirate("Jack", "Captain", new BigDecimal("100000")));
         * - blackPearl.addCrewMember(new Pirate("Gibbs", "First Mate", new BigDecimal("20000")));
         * - entityManager.persist(blackPearl);
         * - entityManager.flush();
         * - entityManager.clear();
         *
         * - // Test 1: findById() - N+1 problem
         * - Ship ship1 = shipRepository.findById(blackPearl.getId()).orElseThrow();
         * - int crewSize1 = ship1.getCrew().size(); // Dodatkowe SELECT!
         * - entityManager.clear();
         *
         * - // Test 2: findByIdWithCrew() - JOIN FETCH
         * - Ship ship2 = shipRepository.findByIdWithCrew(blackPearl.getId()).orElseThrow();
         * - int crewSize2 = ship2.getCrew().size(); // Brak dodatkowego SELECT
         *
         * - assertThat(crewSize1).isEqualTo(crewSize2).isEqualTo(2);
         *
         * 💡 SPRAWDŹ LOGI SQL:
         * - findById() wykonuje 2 SELECT (1 dla Ship + 1 dla Crew)
         * - findByIdWithCrew() wykonuje 1 SELECT (JOIN FETCH)
         */
        @Test
        @DisplayName("🎯 ĆWICZENIE 3: Porównaj findById() vs findByIdWithCrew()")
        @Disabled("Usuń @Disabled i napisz test")
        void exercise3_shouldDemonstrateNPlusOneProblem() {
            // TODO: Napisz test porównujący N+1 problem z JOIN FETCH
            fail("Usuń fail() i zaimplementuj test");
        }
    }

    // ========================================================================
    // POZIOM 4: Cascade i OrphanRemoval ⭐⭐⭐⭐
    // ========================================================================

    @Nested
    @DisplayName("⭐⭐⭐⭐ POZIOM 4: Cascade i OrphanRemoval")
    class Level4_CascadeAndOrphanRemoval {

        /**
         * ✅ PRZYKŁAD: Test orphanRemoval
         *
         * Pokazuje:
         * - Jak działa orphanRemoval=true
         * - Automatyczne usuwanie piratów usuniętych z załogi
         * - Zarządzanie relacją bidirectional
         */
        @Test
        @DisplayName("✅ PRZYKŁAD: orphanRemoval - usuń pirata z załogi")
        void example_shouldDeleteOrphanedPirate() {
            // Arrange
            Ship blackPearl = new Ship("Black Pearl", "Galleon", 32);
            Pirate jack = new Pirate("Jack", "Captain", new BigDecimal("100000"));
            Pirate gibbs = new Pirate("Gibbs", "First Mate", new BigDecimal("20000"));

            blackPearl.addCrewMember(jack);
            blackPearl.addCrewMember(gibbs);

            Ship saved = shipRepository.save(blackPearl);
            entityManager.flush();
            entityManager.clear();

            // Act - usuń Gibbsa z załogi
            Ship ship = shipRepository.findByIdWithCrew(saved.getId()).orElseThrow();
            Pirate toRemove = ship.getCrew().stream()
                    .filter(p -> p.getName().equals("Gibbs"))
                    .findFirst()
                    .orElseThrow();
            ship.removeCrewMember(toRemove);

            shipRepository.save(ship);
            entityManager.flush();
            entityManager.clear();

            // Assert - Gibbs został usunięty z bazy (orphanRemoval=true)
            Ship updated = shipRepository.findByIdWithCrew(saved.getId()).orElseThrow();
            assertThat(updated.getCrew()).hasSize(1);
            assertThat(updated.getCrew().get(0).getName()).isEqualTo("Jack");

            // Sprawdź że Gibbs nie istnieje w bazie
            assertThat(pirateRepository.findByNameIgnoreCase("Gibbs")).isEmpty();
        }

        /**
         * 🎯 ĆWICZENIE 4: Test Cascade.ALL - zapis załogi
         *
         * ZADANIE:
         * Napisz test sprawdzający że zapisanie statku automatycznie zapisuje załogę (Cascade.ALL).
         *
         * KROKI:
         * 1. Arrange: Stwórz statek i dodaj 2 piratów (NIE zapisuj piratów osobno!)
         * 2. Act: Zapisz tylko statek - shipRepository.save(blackPearl)
         * 3. Assert: Sprawdź że piraci zostali automatycznie zapisani
         *
         * 💡 PODPOWIEDZI:
         * - Ship blackPearl = new Ship("Black Pearl", "Galleon", 32);
         * - blackPearl.addCrewMember(new Pirate("Jack", "Captain", new BigDecimal("100000")));
         * - blackPearl.addCrewMember(new Pirate("Gibbs", "First Mate", new BigDecimal("20000")));
         *
         * - // Zapisz TYLKO statek - załoga zapisze się automatycznie (Cascade.ALL)
         * - Ship saved = shipRepository.save(blackPearl);
         * - entityManager.flush();
         * - entityManager.clear();
         *
         * - // Sprawdź że załoga została zapisana
         * - Ship found = shipRepository.findByIdWithCrew(saved.getId()).orElseThrow();
         * - assertThat(found.getCrew()).hasSize(2);
         * - assertThat(found.getCrew()).extracting(Pirate::getName)
         *       .containsExactlyInAnyOrder("Jack", "Gibbs");
         */
        @Test
        @DisplayName("🎯 ĆWICZENIE 4: Cascade.ALL - zapisz statek z załogą")
        @Disabled("Usuń @Disabled i napisz test")
        void exercise4_shouldSaveShipWithCrew() {
            // TODO: Napisz test dla Cascade.ALL
            fail("Usuń fail() i zaimplementuj test");
        }
    }

    // ========================================================================
    // POZIOM 5: Bean Validation ⭐⭐⭐⭐⭐
    // ========================================================================

    @Nested
    @DisplayName("⭐⭐⭐⭐⭐ POZIOM 5: Bean Validation")
    class Level5_BeanValidation {

        /**
         * ✅ PRZYKŁAD: Test walidacji @NotBlank
         *
         * Pokazuje:
         * - Jak testować Bean Validation constraints
         * - Użycie assertThatThrownBy() dla wyjątków
         * - Walidacja na poziomie JPA
         */
        @Test
        @DisplayName("✅ PRZYKŁAD: @NotBlank - nazwa nie może być pusta")
        void example_shouldThrowWhenNameIsBlank() {
            // Arrange
            Ship ship = new Ship("", "Galleon", 32); // Pusta nazwa!

            // Act & Assert
            assertThatThrownBy(() -> {
                shipRepository.save(ship);
                entityManager.flush(); // Walidacja następuje przy flush()
            })
                    .isInstanceOf(ConstraintViolationException.class)
                    .hasMessageContaining("Nazwa statku jest wymagana");
        }

        /**
         * 🎯 ĆWICZENIE 5: Test walidacji @Min
         *
         * ZADANIE:
         * Napisz test sprawdzający że liczba dział nie może być ujemna (@Min(0)).
         *
         * KROKI:
         * 1. Arrange: Stwórz statek z ujemną liczbą dział (np. -10)
         * 2. Act & Assert: Sprawdź że rzucony zostanie ConstraintViolationException
         *
         * 💡 PODPOWIEDZI:
         * - Ship ship = new Ship("Black Pearl", "Galleon", -10);
         *
         * - assertThatThrownBy(() -> {
         *       shipRepository.save(ship);
         *       entityManager.flush();
         *   })
         *   .isInstanceOf(ConstraintViolationException.class)
         *   .hasMessageContaining("nie może być ujemna");
         *
         * 💡 SPRAWDŹ:
         * - W Ship.java jest adnotacja @Min(value = 0, message = "Liczba dział nie może być ujemna")
         */
        @Test
        @DisplayName("🎯 ĆWICZENIE 5: @Min - liczba dział nie może być ujemna")
        @Disabled("Usuń @Disabled i napisz test")
        void exercise5_shouldThrowWhenCannonsNegative() {
            // TODO: Napisz test dla walidacji @Min
            fail("Usuń fail() i zaimplementuj test");
        }
    }
}



