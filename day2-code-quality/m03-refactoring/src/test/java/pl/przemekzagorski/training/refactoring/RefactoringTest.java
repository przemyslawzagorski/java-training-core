package pl.przemekzagorski.training.refactoring;

import org.junit.jupiter.api.*;
import pl.przemekzagorski.training.refactoring.smells.after.*;
import pl.przemekzagorski.training.refactoring.smells.before.BadPirateService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ╔═══════════════════════════════════════════════════════════════════╗
 * ║         TESTY DEMO - REFACTORING & SOLID                         ║
 * ╠═══════════════════════════════════════════════════════════════════╣
 * ║  Testujemy klasy Demo z pakietu smells.after                    ║
 * ║  Dla studentów - commitowane do repo                            ║
 * ╚═══════════════════════════════════════════════════════════════════╝
 */
@DisplayName("Refactoring & SOLID Demo Tests")
class RefactoringTest {

    // ════════════════════════════════════════════════════════════════
    // TESTY SHIP - klasa z pakietu smells.after
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Ship - Demo Class")
    class ShipTests {

        @Test
        @DisplayName("Ship oblicza obrażenia - mała załoga")
        void ship_calculateDamage_smallCrew() {
            // Given
            Ship ship = new Ship("Sloop", 10, 30, 100);

            // When
            int damage = ship.calculateDamage();

            // Then - 10 * 10 = 100 (bez bonusu)
            assertEquals(100, damage);
        }

        @Test
        @DisplayName("Ship oblicza obrażenia - duża załoga")
        void ship_calculateDamage_largeCrew() {
            // Given
            Ship ship = new Ship("Man-o-War", 20, 150, 500);

            // When
            int damage = ship.calculateDamage();

            // Then - 20 * 10 + 20 + 30 = 250
            assertEquals(250, damage);
        }

        @Test
        @DisplayName("Ship przyjmuje obrażenia")
        void ship_takeDamage() {
            // Given
            Ship ship = new Ship("Frigate", 15, 80, 300);

            // When
            ship.takeDamage(100);

            // Then
            assertEquals(200, ship.getHealth());
        }

        @Test
        @DisplayName("Ship jest zniszczony gdy HP <= 0")
        void ship_isDestroyed() {
            // Given
            Ship ship = new Ship("Wreck", 5, 20, 10);

            // When
            ship.takeDamage(50);

            // Then
            assertTrue(ship.isDestroyed());
        }

        @Test
        @DisplayName("Ship porównuje siłę z innym statkiem")
        void ship_isStrongerThan() {
            // Given
            Ship stronger = new Ship("Black Pearl", 20, 100, 400);
            Ship weaker = new Ship("Sloop", 10, 50, 200);

            // Then
            assertTrue(stronger.isStrongerThan(weaker));
            assertFalse(weaker.isStrongerThan(stronger));
        }
    }

    // ════════════════════════════════════════════════════════════════
    // TESTY LOOT CALCULATOR - klasa z pakietu smells.after
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("LootCalculator - Demo Class")
    class LootCalculatorTests {

        private LootCalculator calculator;

        @BeforeEach
        void setUp() {
            calculator = new LootCalculator();
        }

        @Test
        @DisplayName("Oblicza łupy z małego statku")
        void calculate_smallShip() {
            // Given
            Ship winner = new Ship("Black Pearl", 20, 100, 300);
            Ship loser = new Ship("Sloop", 10, 30, 0);

            // When
            BigDecimal loot = calculator.calculate(winner, loser);

            // Then - 30 crew * 10 = 300 (bez bonusu, bo < 20 dział)
            assertEquals(new BigDecimal("300"), loot);
        }

        @Test
        @DisplayName("Oblicza łupy z dużego statku (bonus)")
        void calculate_largeShip() {
            // Given
            Ship winner = new Ship("Black Pearl", 20, 100, 300);
            Ship loser = new Ship("Man-o-War", 25, 100, 0);

            // When
            BigDecimal loot = calculator.calculate(winner, loser);

            // Then - 100 crew * 10 + 500 bonus = 1500
            assertEquals(new BigDecimal("1500"), loot);
        }
    }

    // ════════════════════════════════════════════════════════════════
    // TESTY BATTLE RESULT - klasa z pakietu smells.after
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("BattleResult - Demo Class")
    class BattleResultTests {

        @Test
        @DisplayName("BattleResult.victory tworzy wynik zwycięstwa")
        void battleResult_victory() {
            // Given
            Ship winner = new Ship("Black Pearl", 20, 100, 200);
            Ship loser = new Ship("Sloop", 10, 50, 0);
            BigDecimal loot = new BigDecimal("500");

            // When
            BattleResult result = BattleResult.victory(winner, loser, loot);

            // Then
            assertEquals(winner, result.winner());
            assertEquals(loser, result.loser());
            assertEquals(loot, result.loot());
            assertFalse(result.isDraw());
        }

        @Test
        @DisplayName("BattleResult.draw tworzy wynik remisu")
        void battleResult_draw() {
            // When
            BattleResult result = BattleResult.draw();

            // Then
            assertNull(result.winner());
            assertNull(result.loser());
            assertEquals(BigDecimal.ZERO, result.loot());
            assertTrue(result.isDraw());
        }
    }

    // ════════════════════════════════════════════════════════════════
    // TESTY GOOD BATTLE SERVICE - klasa z pakietu smells.after
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("GoodBattleService - Demo Class")
    class GoodBattleServiceTests {

        @Test
        @DisplayName("GoodBattleService przetwarza bitwę z Dependency Injection")
        void goodBattleService_processBattle() {
            // Given
            LootCalculator lootCalculator = new LootCalculator();
            List<BattleResult> savedResults = new ArrayList<>();
            BattleReporter reporter = result -> {}; // mock
            BattleRepository repository = savedResults::add; // mock

            GoodBattleService service = new GoodBattleService(
                    lootCalculator, reporter, repository
            );

            Ship ship1 = new Ship("Black Pearl", 20, 100, 300);
            Ship ship2 = new Ship("Flying Dutchman", 25, 120, 350);

            // When
            BattleResult result = service.processBattle(ship1, ship2);

            // Then
            assertNotNull(result);
            assertEquals(1, savedResults.size());
            assertEquals(result, savedResults.get(0));
        }
    }

    // ════════════════════════════════════════════════════════════════
    // TESTY INTERFACES - Dependency Inversion Principle
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Dependency Inversion - Interfaces")
    class DependencyInversionTests {

        @Test
        @DisplayName("BattleRepository - interfejs pozwala na różne implementacje")
        void battleRepository_interface() {
            // Given - różne implementacje tego samego interfejsu
            BattleRepository mockRepo = result -> System.out.println("Mock: " + result);
            BattleRepository inMemoryRepo = new InMemoryBattleRepository();

            // Then - oba implementują ten sam interfejs
            assertNotNull(mockRepo);
            assertNotNull(inMemoryRepo);
        }

        @Test
        @DisplayName("InMemoryBattleRepository zapisuje wyniki (wywołuje save)")
        void inMemoryRepository_savesResults() {
            // Given
            InMemoryBattleRepository repo = new InMemoryBattleRepository();
            Ship winner = new Ship("Winner", 20, 100, 200);
            Ship loser = new Ship("Loser", 10, 50, 0);
            BattleResult result = BattleResult.victory(winner, loser, new BigDecimal("500"));

            // When - wywołujemy save (wyświetla komunikat na konsoli)
            repo.save(result);

            // Then - metoda została wywołana bez błędów
            assertNotNull(repo);
        }
    }

    // ════════════════════════════════════════════════════════════════
    // TEST BAD CODE - klasa z pakietu smells.before
    // ════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("BadPirateService - przykład złego kodu (Long Method, God Class)")
    void badPirateService_exists() {
        // Given - klasa z code smells
        BadPirateService badService = new BadPirateService();

        // Then - klasa istnieje (studenci mogą ją analizować)
        assertNotNull(badService);
    }
}
