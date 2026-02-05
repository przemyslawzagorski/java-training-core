package pl.przemekzagorski.training.patterns;

import org.junit.jupiter.api.*;
import pl.przemekzagorski.training.patterns.PatternExercisesSolutions.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ╔═══════════════════════════════════════════════════════════════════╗
 * ║         TESTY - WZORCE PROJEKTOWE                                ║
 * ╠═══════════════════════════════════════════════════════════════════╣
 * ║  Testujemy Singleton, Factory, Builder, Strategy                 ║
 * ╚═══════════════════════════════════════════════════════════════════╝
 */
@DisplayName("Design Patterns Tests")
class PatternTest {

    // ════════════════════════════════════════════════════════════════
    // TESTY SINGLETON
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Singleton Pattern")
    class SingletonTests {

        @Test
        @DisplayName("Enum Singleton - zawsze ta sama instancja")
        void enumSingleton_shouldReturnSameInstance() {
            // When
            AppConfig config1 = AppConfig.INSTANCE;
            AppConfig config2 = AppConfig.INSTANCE;

            // Then
            assertSame(config1, config2, "Singleton powinien zwrócić tę samą instancję");
        }

        @Test
        @DisplayName("Singleton - przechowuje konfigurację")
        void singleton_shouldStoreConfiguration() {
            // Given
            AppConfig config = AppConfig.INSTANCE;

            // When
            String dbUrl = config.getDatabaseUrl();
            int port = config.getPort();

            // Then
            assertNotNull(dbUrl);
            assertTrue(port > 0);
        }

        @Test
        @DisplayName("Classic Singleton - thread-safe getInstance()")
        void classicSingleton_shouldReturnSameInstance() {
            // When
            AppConfigClassic config1 = AppConfigClassic.getInstance();
            AppConfigClassic config2 = AppConfigClassic.getInstance();

            // Then
            assertSame(config1, config2);
        }
    }

    // ════════════════════════════════════════════════════════════════
    // TESTY FACTORY
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Factory Pattern")
    class FactoryTests {

        @Test
        @DisplayName("Factory - tworzy Cutlass")
        void factory_shouldCreateCutlass() {
            // When
            Weapon weapon = WeaponFactory.create("cutlass");

            // Then
            assertNotNull(weapon);
            assertEquals("Cutlass (Szabla Piracka)", weapon.name());
            assertEquals(20, weapon.damage());
            assertEquals(1, weapon.range());
        }

        @Test
        @DisplayName("Factory - tworzy Pistol")
        void factory_shouldCreatePistol() {
            // When
            Weapon weapon = WeaponFactory.create("pistol");

            // Then
            assertNotNull(weapon);
            assertEquals("Flintlock Pistol", weapon.name());
            assertEquals(35, weapon.damage());
            assertEquals(5, weapon.range());
        }

        @Test
        @DisplayName("Factory - tworzy Cannon")
        void factory_shouldCreateCannon() {
            // When
            Weapon weapon = WeaponFactory.create("cannon");

            // Then
            assertEquals(100, weapon.damage());
            assertEquals(50, weapon.range());
        }

        @Test
        @DisplayName("Factory - alias 'sword' tworzy Cutlass")
        void factory_shouldHandleAliases() {
            // When
            Weapon sword = WeaponFactory.create("sword");
            Weapon cutlass = WeaponFactory.create("cutlass");

            // Then
            assertEquals(sword.name(), cutlass.name());
        }

        @Test
        @DisplayName("Factory - nieznany typ rzuca wyjątek")
        void factory_shouldThrowForUnknownType() {
            // When/Then
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                WeaponFactory.create("musket");
            });

            assertTrue(exception.getMessage().contains("Unknown weapon"));
        }

        @Test
        @DisplayName("Factory - case insensitive")
        void factory_shouldBeCaseInsensitive() {
            // When
            Weapon upper = WeaponFactory.create("CUTLASS");
            Weapon lower = WeaponFactory.create("cutlass");
            Weapon mixed = WeaponFactory.create("Cutlass");

            // Then - wszystkie powinny być tego samego typu
            assertEquals(upper.name(), lower.name());
            assertEquals(lower.name(), mixed.name());
        }
    }

    // ════════════════════════════════════════════════════════════════
    // TESTY BUILDER
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Builder Pattern")
    class BuilderTests {

        @Test
        @DisplayName("Builder - minimalne zamówienie (tylko drink)")
        void builder_shouldCreateMinimalOrder() {
            // When
            TavernOrder order = TavernOrder.builder("Rum").build();

            // Then
            assertEquals("Rum", order.getDrink());
            assertNull(order.getFood());
            assertNull(order.getDessert());
            assertFalse(order.isToGo());
            assertEquals(0, order.getTableNumber());
        }

        @Test
        @DisplayName("Builder - pełne zamówienie")
        void builder_shouldCreateFullOrder() {
            // When
            TavernOrder order = TavernOrder.builder("Grog")
                    .food("Fish and Chips")
                    .dessert("Parrot Cake")
                    .tableNumber(7)
                    .toGo(false)
                    .build();

            // Then
            assertEquals("Grog", order.getDrink());
            assertEquals("Fish and Chips", order.getFood());
            assertEquals("Parrot Cake", order.getDessert());
            assertEquals(7, order.getTableNumber());
            assertFalse(order.isToGo());
        }

        @Test
        @DisplayName("Builder - zamówienie na wynos")
        void builder_shouldCreateToGoOrder() {
            // When
            TavernOrder order = TavernOrder.builder("Whiskey")
                    .food("Beef Jerky")
                    .toGo(true)
                    .build();

            // Then
            assertTrue(order.isToGo());
        }

        @Test
        @DisplayName("Builder - drink jest wymagany")
        void builder_shouldRequireDrink() {
            // When/Then
            assertThrows(IllegalArgumentException.class, () -> {
                TavernOrder.builder(null).build();
            });

            assertThrows(IllegalArgumentException.class, () -> {
                TavernOrder.builder("   ").build();
            });
        }

        @Test
        @DisplayName("Builder - immutability (obiekt nie może być zmieniony)")
        void builder_shouldCreateImmutableObject() {
            // Given
            TavernOrder order = TavernOrder.builder("Ale")
                    .food("Stew")
                    .build();

            // Then - gettery nie pozwalają na modyfikację
            // (TavernOrder nie ma setterów, jest immutable)
            assertEquals("Ale", order.getDrink());
            assertEquals("Stew", order.getFood());
        }

        @Test
        @DisplayName("Builder - fluent API chain")
        void builder_shouldSupportFluentChaining() {
            // When - jedna linia z chainowaniem
            TavernOrder order = TavernOrder.builder("Mead")
                    .food("Roast")
                    .dessert("Tart")
                    .tableNumber(3)
                    .toGo(false)
                    .build();

            // Then
            assertNotNull(order);
            assertEquals("Mead", order.getDrink());
        }
    }

    // ════════════════════════════════════════════════════════════════
    // TESTY STRATEGY
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Strategy Pattern")
    class StrategyTests {

        @Test
        @DisplayName("Strategy - zmiana strategii w runtime")
        void strategy_shouldAllowRuntimeChange() {
            // Given
            NavigableShip ship = new NavigableShip("Black Pearl");

            // When/Then - początkowa strategia
            ship.setNavigationStrategy(new CompassNavigation());
            // Nie możemy łatwo zweryfikować output, ale metoda powinna się wykonać bez błędu

            // Zmiana strategii
            ship.setNavigationStrategy(new StarNavigation());
            // Ponownie - działa bez błędu

            ship.setNavigationStrategy(new MapNavigation());
            // I znowu zmiana
        }

        @Test
        @DisplayName("Strategy - różne speed ratings")
        void strategy_shouldHaveDifferentSpeedRatings() {
            // Given
            NavigationStrategy stars = new StarNavigation();
            NavigationStrategy compass = new CompassNavigation();
            NavigationStrategy map = new MapNavigation();

            // Then - mapa najszybsza, gwiazdy najwolniejsze
            assertTrue(stars.speedRating() < compass.speedRating());
            assertTrue(compass.speedRating() < map.speedRating());
        }

        @Test
        @DisplayName("Strategy - każda ma unikalną nazwę")
        void strategy_shouldHaveUniqueNames() {
            // Given
            NavigationStrategy stars = new StarNavigation();
            NavigationStrategy compass = new CompassNavigation();
            NavigationStrategy map = new MapNavigation();

            // Then
            assertNotEquals(stars.methodName(), compass.methodName());
            assertNotEquals(compass.methodName(), map.methodName());
            assertNotEquals(stars.methodName(), map.methodName());
        }

        @Test
        @DisplayName("Strategy - statek wykonuje nawigację")
        void strategy_shipShouldNavigate() {
            // Given
            NavigableShip ship = new NavigableShip("Flying Dutchman");
            ship.setNavigationStrategy(new CompassNavigation());

            // When/Then - nie rzuca wyjątku
            assertDoesNotThrow(() -> ship.navigate("Tortuga", "Nassau"));
        }
    }

    // ════════════════════════════════════════════════════════════════
    // TESTY MINI-PROJEKT (Bonus)
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Mini-Project (Singleton + Builder + Factory)")
    class MiniProjectTests {

        @Test
        @DisplayName("CrewMember Builder - tworzy członka załogi")
        void crewMember_shouldBeCreatedByBuilder() {
            // When
            CrewMember member = CrewMember.builder("Jack")
                    .role("Captain")
                    .experience(100)
                    .weapon("Cutlass")
                    .skill("Navigation")
                    .skill("Sword fighting")
                    .build();

            // Then
            assertNotNull(member);
            assertTrue(member.toString().contains("Jack"));
            assertTrue(member.toString().contains("Captain"));
        }

        @Test
        @DisplayName("RoleFactory - tworzy kapitana z predefiniowaną konfiguracją")
        void roleFactory_shouldCreateCaptain() {
            // When
            CrewMember captain = RoleFactory.createCaptain("Barbossa");

            // Then
            assertNotNull(captain);
            assertTrue(captain.toString().contains("Barbossa"));
            assertTrue(captain.toString().contains("Captain"));
            assertTrue(captain.toString().contains("Cutlass"));
        }

        @Test
        @DisplayName("RoleFactory - tworzy kucharza")
        void roleFactory_shouldCreateCook() {
            // When
            CrewMember cook = RoleFactory.createCook("Cookie");

            // Then
            assertTrue(cook.toString().contains("Cook"));
            assertTrue(cook.toString().contains("Knife"));
        }

        @Test
        @DisplayName("RoleFactory - tworzy marynarza")
        void roleFactory_shouldCreateSailor() {
            // When
            CrewMember sailor = RoleFactory.createSailor("Will");

            // Then
            assertTrue(sailor.toString().contains("Sailor"));
        }
    }

    // ════════════════════════════════════════════════════════════════
    // TEST INTEGRACYJNY
    // ════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Integracja - pełen workflow z wieloma wzorcami")
    void integration_shouldWorkWithMultiplePatterns() {
        // Singleton - konfiguracja
        AppConfig config = AppConfig.INSTANCE;
        assertNotNull(config.getDatabaseUrl());

        // Factory - bronie
        Weapon weapon1 = WeaponFactory.create("cutlass");
        Weapon weapon2 = WeaponFactory.create("pistol");
        assertNotEquals(weapon1.damage(), weapon2.damage());

        // Builder - zamówienie
        TavernOrder order = TavernOrder.builder("Rum")
                .food("Fish")
                .build();
        assertEquals("Rum", order.getDrink());

        // Strategy - nawigacja
        NavigableShip ship = new NavigableShip("Queen Anne's Revenge");
        ship.setNavigationStrategy(new MapNavigation());
        assertDoesNotThrow(() -> ship.navigate("Port Royal", "Tortuga"));
    }
}
