package pl.przemekzagorski.training.patterns;

import org.junit.jupiter.api.*;
import pl.przemekzagorski.training.patterns.builder.PirateShip;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ╔═══════════════════════════════════════════════════════════════════╗
 * ║         TESTY - WZORCE PROJEKTOWE (DEMO)                         ║
 * ╠═══════════════════════════════════════════════════════════════════╣
 * ║  Testujemy klasy demonstracyjne (Captain, ShipFactory, etc.)    ║
 * ║  TEN PLIK JEST DLA STUDENTÓW - COMMITUJ DO REPO!                ║
 * ╚═══════════════════════════════════════════════════════════════════╝
 */
@DisplayName("Design Patterns Demo Tests")
class PatternTest {

    // ════════════════════════════════════════════════════════════════
    // TESTY SINGLETON
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Singleton Pattern - Demo")
    class SingletonTests {

        @Test
        @DisplayName("Captain Singleton - zawsze ta sama instancja")
        void captainSingleton_shouldReturnSameInstance() {
            // When
            pl.przemekzagorski.training.patterns.singleton.Captain captain1 =
                pl.przemekzagorski.training.patterns.singleton.Captain.getInstance();
            pl.przemekzagorski.training.patterns.singleton.Captain captain2 =
                pl.przemekzagorski.training.patterns.singleton.Captain.getInstance();

            // Then
            assertSame(captain1, captain2, "Singleton powinien zwrócić tę samą instancję");
        }

        @Test
        @DisplayName("Captain Singleton - przechowuje dane")
        void captainSingleton_shouldStoreData() {
            // Given
            pl.przemekzagorski.training.patterns.singleton.Captain captain =
                pl.przemekzagorski.training.patterns.singleton.Captain.getInstance();

            // When
            String name = captain.getName();
            String shipName = captain.getShipName();

            // Then
            assertNotNull(name);
            assertNotNull(shipName);
        }

        @Test
        @DisplayName("CaptainEnum Singleton - zawsze ta sama instancja")
        void captainEnumSingleton_shouldReturnSameInstance() {
            // When
            pl.przemekzagorski.training.patterns.singleton.CaptainEnum captain1 =
                pl.przemekzagorski.training.patterns.singleton.CaptainEnum.INSTANCE;
            pl.przemekzagorski.training.patterns.singleton.CaptainEnum captain2 =
                pl.przemekzagorski.training.patterns.singleton.CaptainEnum.INSTANCE;

            // Then
            assertSame(captain1, captain2);
        }

        @Test
        @DisplayName("CaptainEnum - może wydawać rozkazy")
        void captainEnum_shouldGiveOrders() {
            // Given
            pl.przemekzagorski.training.patterns.singleton.CaptainEnum captain =
                pl.przemekzagorski.training.patterns.singleton.CaptainEnum.INSTANCE;

            // When/Then - nie rzuca wyjątku
            assertDoesNotThrow(() -> captain.giveOrder("Hoist the sails!"));
        }
    }

    // ════════════════════════════════════════════════════════════════
    // TESTY FACTORY
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Factory Pattern - Demo")
    class FactoryTests {

        @Test
        @DisplayName("ShipFactory - tworzy Galleon")
        void factory_shouldCreateGalleon() {
            // When
            pl.przemekzagorski.training.patterns.factory.Ship ship =
                pl.przemekzagorski.training.patterns.factory.ShipFactory.createShip(
                    pl.przemekzagorski.training.patterns.factory.ShipFactory.ShipType.GALLEON, "HMS Victory");

            // Then
            assertNotNull(ship);
            assertTrue(ship instanceof pl.przemekzagorski.training.patterns.factory.Galleon);
            assertEquals("HMS Victory", ship.getName());
        }

        @Test
        @DisplayName("ShipFactory - tworzy Frigate")
        void factory_shouldCreateFrigate() {
            // When
            pl.przemekzagorski.training.patterns.factory.Ship ship =
                pl.przemekzagorski.training.patterns.factory.ShipFactory.createShip(
                    pl.przemekzagorski.training.patterns.factory.ShipFactory.ShipType.FRIGATE, "Sea Hawk");

            // Then
            assertNotNull(ship);
            assertTrue(ship instanceof pl.przemekzagorski.training.patterns.factory.Frigate);
        }

        @Test
        @DisplayName("ShipFactory - tworzy Sloop")
        void factory_shouldCreateSloop() {
            // When
            pl.przemekzagorski.training.patterns.factory.Ship ship =
                pl.przemekzagorski.training.patterns.factory.ShipFactory.createShip(
                    pl.przemekzagorski.training.patterns.factory.ShipFactory.ShipType.SLOOP, "Swift Wind");

            // Then
            assertNotNull(ship);
            assertTrue(ship instanceof pl.przemekzagorski.training.patterns.factory.Sloop);
        }

        @Test
        @DisplayName("ShipFactory - tworzy statek ze String")
        void factory_shouldCreateFromString() {
            // When
            pl.przemekzagorski.training.patterns.factory.Ship galleon =
                pl.przemekzagorski.training.patterns.factory.ShipFactory.createShip("galleon", "Black Pearl");
            pl.przemekzagorski.training.patterns.factory.Ship frigate =
                pl.przemekzagorski.training.patterns.factory.ShipFactory.createShip("frigate", "Flying Dutchman");
            pl.przemekzagorski.training.patterns.factory.Ship sloop =
                pl.przemekzagorski.training.patterns.factory.ShipFactory.createShip("sloop", "Interceptor");

            // Then
            assertTrue(galleon instanceof pl.przemekzagorski.training.patterns.factory.Galleon);
            assertTrue(frigate instanceof pl.przemekzagorski.training.patterns.factory.Frigate);
            assertTrue(sloop instanceof pl.przemekzagorski.training.patterns.factory.Sloop);
        }

        @Test
        @DisplayName("ShipFactory - semantyczne metody")
        void factory_shouldHaveSemanticMethods() {
            // When
            pl.przemekzagorski.training.patterns.factory.Ship battleship =
                pl.przemekzagorski.training.patterns.factory.ShipFactory.createBattleship("Warship");
            pl.przemekzagorski.training.patterns.factory.Ship scout =
                pl.przemekzagorski.training.patterns.factory.ShipFactory.createScoutShip("Scout");
            pl.przemekzagorski.training.patterns.factory.Ship trade =
                pl.przemekzagorski.training.patterns.factory.ShipFactory.createTradeShip("Merchant");

            // Then
            assertNotNull(battleship);
            assertNotNull(scout);
            assertNotNull(trade);
        }

        @Test
        @DisplayName("ShipFactory - nieznany typ rzuca wyjątek")
        void factory_shouldThrowForUnknownType() {
            // When/Then
            assertThrows(IllegalArgumentException.class, () -> {
                pl.przemekzagorski.training.patterns.factory.ShipFactory.createShip("submarine", "Nautilus");
            });
        }
    }

    // ════════════════════════════════════════════════════════════════
    // TESTY BUILDER
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Builder Pattern - Demo")
    class BuilderTests {

        @Test
        @DisplayName("PirateShip Builder - minimalny statek (tylko nazwa)")
        void builder_shouldCreateMinimalShip() {
            // When
            PirateShip ship = new PirateShip.Builder("Black Pearl").build();

            // Then
            assertEquals("Black Pearl", ship.getName());
            assertEquals("Unknown", ship.getType());
            assertEquals(0, ship.getCannons());
            assertEquals(10, ship.getCrewCapacity());
        }

        @Test
        @DisplayName("PirateShip Builder - pełny statek")
        void builder_shouldCreateFullShip() {
            // When
            PirateShip ship = new PirateShip.Builder("Queen Anne's Revenge")
                    .type("Galleon")
                    .cannons(40)
                    .crewCapacity(200)
                    .withJollyRoger()
                    .captain("Blackbeard")
                    .cargoCapacity(500)
                    .homePort("Nassau")
                    .build();

            // Then
            assertEquals("Queen Anne's Revenge", ship.getName());
            assertEquals("Galleon", ship.getType());
            assertEquals(40, ship.getCannons());
            assertEquals(200, ship.getCrewCapacity());
            assertTrue(ship.hasJollyRoger());
            assertEquals("Blackbeard", ship.getCaptainName());
            assertEquals(500, ship.getCargoCapacity());
            assertEquals("Nassau", ship.getHomePort());
        }

        @Test
        @DisplayName("PirateShip Builder - fluent API")
        void builder_shouldSupportFluentChaining() {
            // When - jedna linia z chainowaniem
            PirateShip ship = new PirateShip.Builder("Flying Dutchman")
                    .type("Ghost Ship")
                    .cannons(50)
                    .captain("Davy Jones")
                    .build();

            // Then
            assertNotNull(ship);
            assertEquals("Flying Dutchman", ship.getName());
            assertEquals("Davy Jones", ship.getCaptainName());
        }

        @Test
        @DisplayName("PirateShip Builder - nazwa jest wymagana")
        void builder_shouldRequireName() {
            // When/Then
            assertThrows(IllegalStateException.class, () -> {
                new PirateShip.Builder(null).build();
            });

            assertThrows(IllegalStateException.class, () -> {
                new PirateShip.Builder("   ").build();
            });
        }

        @Test
        @DisplayName("PirateShip - immutability (brak setterów)")
        void builder_shouldCreateImmutableObject() {
            // Given
            PirateShip ship = new PirateShip.Builder("Interceptor")
                    .cannons(20)
                    .build();

            // Then - tylko gettery, brak setterów
            assertEquals("Interceptor", ship.getName());
            assertEquals(20, ship.getCannons());
            // Nie ma metod setCannons(), setName() - obiekt jest immutable
        }
    }

    // ════════════════════════════════════════════════════════════════
    // TESTY STRATEGY
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Strategy Pattern - Demo")
    class StrategyTests {

        @Test
        @DisplayName("BattleShip - domyślna strategia to CannonAttack")
        void battleShip_shouldHaveDefaultStrategy() {
            // Given
            pl.przemekzagorski.training.patterns.strategy.BattleShip ship =
                new pl.przemekzagorski.training.patterns.strategy.BattleShip("Black Pearl");

            // When/Then - nie rzuca wyjątku
            assertDoesNotThrow(() -> ship.attack("HMS Interceptor"));
        }

        @Test
        @DisplayName("BattleShip - zmiana strategii w runtime")
        void battleShip_shouldAllowStrategyChange() {
            // Given
            pl.przemekzagorski.training.patterns.strategy.BattleShip ship =
                new pl.przemekzagorski.training.patterns.strategy.BattleShip("Queen Anne's Revenge");

            // When - zmiana strategii
            ship.setAttackStrategy(new pl.przemekzagorski.training.patterns.strategy.BoardingAttack());
            assertDoesNotThrow(() -> ship.attack("Merchant Ship"));

            ship.setAttackStrategy(new pl.przemekzagorski.training.patterns.strategy.RammingAttack());
            assertDoesNotThrow(() -> ship.attack("Enemy Ship"));

            ship.setAttackStrategy(new pl.przemekzagorski.training.patterns.strategy.CannonAttack());
            assertDoesNotThrow(() -> ship.attack("Fort"));
        }

        @Test
        @DisplayName("AttackStrategy - różne strategie mają różne zachowania")
        void attackStrategies_shouldHaveDifferentBehaviors() {
            // Given
            pl.przemekzagorski.training.patterns.strategy.AttackStrategy cannon =
                new pl.przemekzagorski.training.patterns.strategy.CannonAttack();
            pl.przemekzagorski.training.patterns.strategy.AttackStrategy boarding =
                new pl.przemekzagorski.training.patterns.strategy.BoardingAttack();
            pl.przemekzagorski.training.patterns.strategy.AttackStrategy ramming =
                new pl.przemekzagorski.training.patterns.strategy.RammingAttack();

            // When/Then - wszystkie działają bez błędu
            assertDoesNotThrow(() -> cannon.attack("Attacker", "Target"));
            assertDoesNotThrow(() -> boarding.attack("Attacker", "Target"));
            assertDoesNotThrow(() -> ramming.attack("Attacker", "Target"));
        }

        @Test
        @DisplayName("BattleShip - nazwa statku jest przechowywana")
        void battleShip_shouldStoreName() {
            // Given
            pl.przemekzagorski.training.patterns.strategy.BattleShip ship =
                new pl.przemekzagorski.training.patterns.strategy.BattleShip("Flying Dutchman");

            // Then
            assertEquals("Flying Dutchman", ship.getName());
        }
    }

    // ════════════════════════════════════════════════════════════════
    // TESTY OBSERVER
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Observer Pattern - Demo")
    class ObserverTests {

        @Test
        @DisplayName("Captain - może dodawać obserwatorów")
        void captain_shouldAddObservers() {
            // Given
            pl.przemekzagorski.training.patterns.observer.Captain captain =
                new pl.przemekzagorski.training.patterns.observer.Captain("Blackbeard");
            pl.przemekzagorski.training.patterns.observer.Cook cook =
                new pl.przemekzagorski.training.patterns.observer.Cook("Cookie");
            pl.przemekzagorski.training.patterns.observer.Gunner gunner =
                new pl.przemekzagorski.training.patterns.observer.Gunner("Boom");
            pl.przemekzagorski.training.patterns.observer.Navigator navigator =
                new pl.przemekzagorski.training.patterns.observer.Navigator("Compass");

            // When/Then - nie rzuca wyjątku
            assertDoesNotThrow(() -> {
                captain.addObserver(cook);
                captain.addObserver(gunner);
                captain.addObserver(navigator);
            });
        }

        @Test
        @DisplayName("Captain - powiadamia obserwatorów o wydarzeniu")
        void captain_shouldNotifyObservers() {
            // Given
            pl.przemekzagorski.training.patterns.observer.Captain captain =
                new pl.przemekzagorski.training.patterns.observer.Captain("Jack Sparrow");
            pl.przemekzagorski.training.patterns.observer.Cook cook =
                new pl.przemekzagorski.training.patterns.observer.Cook("Cookie");

            captain.addObserver(cook);

            // When/Then - nie rzuca wyjątku
            assertDoesNotThrow(() -> captain.announce("TREASURE_FOUND", "Gold coins!"));
        }
    }

    // ════════════════════════════════════════════════════════════════
    // TESTY DECORATOR
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Decorator Pattern - Demo")
    class DecoratorTests {

        @Test
        @DisplayName("BasicShip - podstawowy statek")
        void basicShip_shouldWork() {
            // Given
            pl.przemekzagorski.training.patterns.decorator.Ship ship =
                new pl.przemekzagorski.training.patterns.decorator.BasicShip("Sea Dog");

            // When
            String description = ship.getDescription();
            int cost = ship.getCost();

            // Then
            assertNotNull(description);
            assertTrue(cost > 0);
        }

        @Test
        @DisplayName("Decorator - dodaje funkcjonalność")
        void decorator_shouldAddFunctionality() {
            // Given
            pl.przemekzagorski.training.patterns.decorator.Ship ship =
                new pl.przemekzagorski.training.patterns.decorator.BasicShip("Sea Dog");

            // When - dodajemy dekoratory
            ship = new pl.przemekzagorski.training.patterns.decorator.ArmorPlating(ship);
            ship = new pl.przemekzagorski.training.patterns.decorator.CannonUpgrade(ship);
            ship = new pl.przemekzagorski.training.patterns.decorator.FastSails(ship);

            // Then
            String description = ship.getDescription();
            assertTrue(description.contains("Wzmocniony kadłub") || description.contains("Armor"));
            assertTrue(description.contains("Dodatkowe armaty") || description.contains("Cannon"));
            assertTrue(description.contains("Szybkie żagle") || description.contains("Sails"));
        }

        @Test
        @DisplayName("Decorator - zwiększa koszt")
        void decorator_shouldIncreaseCost() {
            // Given
            pl.przemekzagorski.training.patterns.decorator.Ship basicShip =
                new pl.przemekzagorski.training.patterns.decorator.BasicShip("Sea Dog");
            int basicCost = basicShip.getCost();

            // When
            pl.przemekzagorski.training.patterns.decorator.Ship decoratedShip =
                new pl.przemekzagorski.training.patterns.decorator.ArmorPlating(
                    new pl.przemekzagorski.training.patterns.decorator.CannonUpgrade(basicShip));
            int decoratedCost = decoratedShip.getCost();

            // Then
            assertTrue(decoratedCost > basicCost);
        }
    }

    // ════════════════════════════════════════════════════════════════
    // TEST INTEGRACYJNY
    // ════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Integracja - pełen workflow z wieloma wzorcami")
    void integration_shouldWorkWithMultiplePatterns() {
        // Singleton - Captain
        pl.przemekzagorski.training.patterns.singleton.Captain captain =
            pl.przemekzagorski.training.patterns.singleton.Captain.getInstance();
        assertNotNull(captain.getName());

        // Factory - statki
        pl.przemekzagorski.training.patterns.factory.Ship galleon =
            pl.przemekzagorski.training.patterns.factory.ShipFactory.createShip(
                pl.przemekzagorski.training.patterns.factory.ShipFactory.ShipType.GALLEON, "Victory");
        pl.przemekzagorski.training.patterns.factory.Ship sloop =
            pl.przemekzagorski.training.patterns.factory.ShipFactory.createShip(
                pl.przemekzagorski.training.patterns.factory.ShipFactory.ShipType.SLOOP, "Swift");
        assertNotNull(galleon);
        assertNotNull(sloop);

        // Builder - PirateShip
        PirateShip pirateShip = new PirateShip.Builder("Black Pearl")
                .type("Galleon")
                .cannons(32)
                .captain("Jack Sparrow")
                .build();
        assertEquals("Black Pearl", pirateShip.getName());

        // Strategy - BattleShip
        pl.przemekzagorski.training.patterns.strategy.BattleShip battleShip =
            new pl.przemekzagorski.training.patterns.strategy.BattleShip("Queen Anne's Revenge");
        battleShip.setAttackStrategy(new pl.przemekzagorski.training.patterns.strategy.CannonAttack());
        assertDoesNotThrow(() -> battleShip.attack("Enemy"));
    }
}
