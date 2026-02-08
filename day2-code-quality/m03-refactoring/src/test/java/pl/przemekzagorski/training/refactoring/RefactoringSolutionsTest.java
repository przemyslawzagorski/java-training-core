package pl.przemekzagorski.training.refactoring;

import org.junit.jupiter.api.*;
import pl.przemekzagorski.training.refactoring.RefactoringExercisesSolutions.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ╔═══════════════════════════════════════════════════════════════════╗
 * ║         TESTY SOLUTIONS - REFACTORING & SOLID                    ║
 * ╠═══════════════════════════════════════════════════════════════════╣
 * ║  Testujemy rozwiązania z RefactoringExercisesSolutions          ║
 * ║  Ten plik jest dla TRENERA - nie commituj do repo!              ║
 * ╚═══════════════════════════════════════════════════════════════════╝
 */
@DisplayName("Refactoring & SOLID Solutions Tests")
class RefactoringSolutionsTest {

    private RefactoringExercisesSolutions solutions;

    @BeforeEach
    void setUp() {
        solutions = new RefactoringExercisesSolutions();
    }

    // ════════════════════════════════════════════════════════════════
    // TESTY EXTRACT METHOD
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Extract Method - calculateDamage")
    class CalculateDamageTests {

        @Test
        @DisplayName("Małą załoga (< 50) - brak bonusu")
        void smallCrew_shouldHaveNoBonus() {
            // Given
            int cannons = 10;
            int crew = 30;

            // When
            int damage = solutions.calculateDamage(cannons, crew);

            // Then - 10 * 10 = 100, bez bonusu
            assertEquals(100, damage);
        }

        @Test
        @DisplayName("Średnia załoga (51-100) - bonus +20")
        void mediumCrew_shouldHaveMediumBonus() {
            // Given
            int cannons = 10;
            int crew = 75;

            // When
            int damage = solutions.calculateDamage(cannons, crew);

            // Then - 10 * 10 + 20 = 120
            assertEquals(120, damage);
        }

        @Test
        @DisplayName("Duża załoga (> 100) - bonus +30")
        void largeCrew_shouldHaveLargeBonus() {
            // Given
            int cannons = 10;
            int crew = 150;

            // When
            int damage = solutions.calculateDamage(cannons, crew);

            // Then - 10 * 10 + 30 = 130
            assertEquals(130, damage);
        }

        @Test
        @DisplayName("Zero dział = zero obrażeń bazowych")
        void zeroCannons_shouldHaveZeroBaseDamage() {
            // Given
            int cannons = 0;
            int crew = 150;

            // When
            int damage = solutions.calculateDamage(cannons, crew);

            // Then - 0 * 10 + 30 = 30 (tylko bonus)
            assertEquals(30, damage);
        }

        @Test
        @DisplayName("Wiele dział + duża załoga = duże obrażenia")
        void manyCannonsLargeCrew_shouldHaveHighDamage() {
            // Given
            int cannons = 50;  // 50 dział
            int crew = 200;   // duża załoga

            // When
            int damage = solutions.calculateDamage(cannons, crew);

            // Then - 50 * 10 + 30 = 530
            assertEquals(530, damage);
        }
    }

    @Nested
    @DisplayName("Extract Method - determineWinner")
    class DetermineWinnerTests {

        @Test
        @DisplayName("Ship1 wygrywa gdy Ship2 zatopiony")
        void ship1Wins_whenShip2Sunk() {
            // When
            String winner = solutions.determineWinner("Black Pearl", 50, "Flying Dutchman", 0);

            // Then
            assertEquals("Black Pearl WINS!", winner);
        }

        @Test
        @DisplayName("Ship2 wygrywa gdy Ship1 zatopiony")
        void ship2Wins_whenShip1Sunk() {
            // When
            String winner = solutions.determineWinner("Black Pearl", 0, "Flying Dutchman", 50);

            // Then
            assertEquals("Flying Dutchman WINS!", winner);
        }

        @Test
        @DisplayName("Remis gdy oba zatopione")
        void draw_whenBothSunk() {
            // When
            String winner = solutions.determineWinner("Ship1", -10, "Ship2", 0);

            // Then
            assertTrue(winner.contains("DRAW"));
        }

        @Test
        @DisplayName("Wyższe HP wygrywa")
        void higherHealth_shouldWin() {
            // When
            String winner = solutions.determineWinner("Ship1", 100, "Ship2", 50);

            // Then
            assertEquals("Ship1", winner);
        }
    }

    // ════════════════════════════════════════════════════════════════
    // TESTY EXTRACT CLASS - Ship record
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Extract Class - Ship entity")
    class ShipTests {

        @Test
        @DisplayName("Ship record przechowuje dane")
        void ship_shouldStoreData() {
            // When
            Ship ship = new Ship("Black Pearl", 20, 100, 500);

            // Then
            assertEquals("Black Pearl", ship.name());
            assertEquals(20, ship.cannons());
            assertEquals(100, ship.crew());
            assertEquals(500, ship.health());
        }

        @Test
        @DisplayName("Ship.withHealth tworzy kopię z nowym HP")
        void ship_withHealthShouldCreateCopy() {
            // Given
            Ship original = new Ship("HMS Interceptor", 15, 80, 400);

            // When
            Ship damaged = original.withHealth(250);

            // Then - nowy obiekt z zmienionym HP
            assertEquals(250, damaged.health());
            assertEquals(original.name(), damaged.name());
            assertEquals(original.cannons(), damaged.cannons());

            // Original unchanged (immutable)
            assertEquals(400, original.health());
        }
    }

    // ════════════════════════════════════════════════════════════════
    // TESTY EXTRACT CLASS - DamageCalculator
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Extract Class - DamageCalculator")
    class DamageCalculatorTests {

        private DamageCalculator calculator;

        @BeforeEach
        void setUp() {
            calculator = new DamageCalculator();
        }

        @Test
        @DisplayName("Oblicza damage dla małej załogi")
        void calculate_smallCrew() {
            // Given
            Ship ship = new Ship("Sloop", 5, 20, 100);

            // When
            int damage = calculator.calculate(ship);

            // Then - 5 * 10 = 50
            assertEquals(50, damage);
        }

        @Test
        @DisplayName("Oblicza damage dla dużej załogi")
        void calculate_largeCrew() {
            // Given
            Ship ship = new Ship("Man-o-War", 40, 200, 800);

            // When
            int damage = calculator.calculate(ship);

            // Then - 40 * 10 + 30 = 430
            assertEquals(430, damage);
        }
    }

    // ════════════════════════════════════════════════════════════════
    // TESTY MAGIC NUMBERS - CrewSize enum
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Magic Numbers - CrewSize enum")
    class CrewSizeTests {

        @Test
        @DisplayName("Mała załoga - bonus 0")
        void smallCrew_zeroBonus() {
            assertEquals(0, CrewSize.bonusForCrew(30));
            assertEquals(0, CrewSize.bonusForCrew(50));  // dokładnie 50 to jeszcze small
        }

        @Test
        @DisplayName("Średnia załoga - bonus 20")
        void mediumCrew_bonus20() {
            assertEquals(20, CrewSize.bonusForCrew(51));
            assertEquals(20, CrewSize.bonusForCrew(75));
            assertEquals(20, CrewSize.bonusForCrew(100));  // dokładnie 100 to jeszcze medium
        }

        @Test
        @DisplayName("Duża załoga - bonus 30")
        void largeCrew_bonus30() {
            assertEquals(30, CrewSize.bonusForCrew(101));
            assertEquals(30, CrewSize.bonusForCrew(500));
        }
    }

    // ════════════════════════════════════════════════════════════════
    // TESTY DEPENDENCY INVERSION
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Dependency Inversion - Interfaces")
    class DependencyInversionTests {

        @Test
        @DisplayName("BattleService działa z mock repository")
        void battleService_shouldWorkWithMockRepository() {
            // Given - mock implementations
            List<BattleResult> savedResults = new ArrayList<>();
            List<String> notifications = new ArrayList<>();

            BattleRepository mockRepo = savedResults::add;
            NotificationService mockNotif = notifications::add;

            BattleService service = new BattleService(
                    new DamageCalculator(),
                    new BattleReporter(),
                    mockRepo,
                    mockNotif
            );

            Ship ship1 = new Ship("Ship1", 10, 50, 100);
            Ship ship2 = new Ship("Ship2", 8, 40, 80);

            // When
            BattleResult result = service.fight(ship1, ship2);

            // Then - mock repository was called
            assertEquals(1, savedResults.size());
            assertEquals(result, savedResults.get(0));

            // And notification was sent
            assertEquals(1, notifications.size());
            assertEquals(result.winner(), notifications.get(0));
        }

        @Test
        @DisplayName("InMemoryBattleRepository zapisuje wyniki")
        void inMemoryRepository_shouldSaveResults() {
            // Given
            InMemoryBattleRepository repo = new InMemoryBattleRepository();
            BattleResult result = new BattleResult(
                    new Ship("A", 10, 50, 100),
                    new Ship("B", 10, 50, 80),
                    100, 100, "A"
            );

            // When
            repo.save(result);

            // Then
            List<BattleResult> all = repo.findAll();
            assertEquals(1, all.size());
            assertEquals(result, all.get(0));
        }

        @Test
        @DisplayName("Łatwa zmiana implementacji (DIP)")
        void dip_shouldAllowEasyImplementationSwap() {
            // Given - możemy użyć różnych implementacji
            BattleRepository sqlRepo = result -> System.out.println("SQL: " + result);
            BattleRepository mongoRepo = result -> System.out.println("Mongo: " + result);
            BattleRepository mockRepo = result -> {}; // do niczego

            // All implement the same interface
            assertNotNull(sqlRepo);
            assertNotNull(mongoRepo);
            assertNotNull(mockRepo);

            // BattleService nie wie która implementacja jest użyta!
            BattleService service = new BattleService(
                    new DamageCalculator(),
                    new BattleReporter(),
                    mockRepo,  // łatwo zamienić!
                    winner -> {}
            );

            assertNotNull(service);
        }
    }

    // ════════════════════════════════════════════════════════════════
    // TESTY BattleResult
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("BattleResult record")
    class BattleResultTests {

        @Test
        @DisplayName("BattleResult przechowuje wynik bitwy")
        void battleResult_shouldStoreData() {
            // Given
            Ship ship1 = new Ship("A", 10, 50, 50);
            Ship ship2 = new Ship("B", 10, 50, 30);

            // When
            BattleResult result = new BattleResult(ship1, ship2, 100, 80, "A");

            // Then
            assertEquals(ship1, result.ship1());
            assertEquals(ship2, result.ship2());
            assertEquals(100, result.damage1());
            assertEquals(80, result.damage2());
            assertEquals("A", result.winner());
        }
    }

    // ════════════════════════════════════════════════════════════════
    // TEST INTEGRACYJNY
    // ════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Integracja - pełna bitwa z wszystkimi komponentami")
    void integration_fullBattleWithAllComponents() {
        // Given
        DamageCalculator calculator = new DamageCalculator();
        BattleReporter reporter = new BattleReporter();
        InMemoryBattleRepository repository = new InMemoryBattleRepository();
        List<String> notifications = new ArrayList<>();
        NotificationService notificationService = notifications::add;

        BattleService battleService = new BattleService(
                calculator, reporter, repository, notificationService
        );

        Ship blackPearl = new Ship("Black Pearl", 20, 120, 300);
        Ship flyingDutchman = new Ship("Flying Dutchman", 25, 100, 350);

        // When
        BattleResult result = battleService.fight(blackPearl, flyingDutchman);

        // Then
        assertNotNull(result);
        assertNotNull(result.winner());

        // Sprawdź że damage został obliczony
        assertTrue(result.damage1() > 0);
        assertTrue(result.damage2() > 0);

        // Sprawdź że wynik został zapisany
        assertEquals(1, repository.findAll().size());

        // Sprawdź że powiadomienie zostało wysłane
        assertEquals(1, notifications.size());
    }

    @Test
    @DisplayName("Zasada Single Responsibility - każda klasa ma jedną odpowiedzialność")
    void srp_eachClassHasSingleResponsibility() {
        // DamageCalculator - tylko oblicza damage
        DamageCalculator calculator = new DamageCalculator();
        Ship ship = new Ship("Test", 10, 50, 100);
        int damage = calculator.calculate(ship);
        assertTrue(damage > 0);

        // BattleReporter - tylko raportuje (nie testujemy output, ale klasa istnieje)
        BattleReporter reporter = new BattleReporter();
        assertNotNull(reporter);

        // InMemoryBattleRepository - tylko przechowuje
        InMemoryBattleRepository repo = new InMemoryBattleRepository();
        assertEquals(0, repo.findAll().size());
    }
}
