package pl.przemekzagorski.training.patterns;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * ╔═══════════════════════════════════════════════════════════════════╗
 * ║         ROZWIĄZANIA - WZORCE PROJEKTOWE                          ║
 * ╠═══════════════════════════════════════════════════════════════════╣
 * ║  Porównaj swoje rozwiązania z tymi poniżej!                      ║
 * ╚═══════════════════════════════════════════════════════════════════╝
 */
public class PatternExercisesSolutions {

    /**
     * ════════════════════════════════════════════════════════════════
     * ROZWIĄZANIE 1: Singleton - Konfiguracja aplikacji
     * ════════════════════════════════════════════════════════════════
     */

    // Wariant 1: Enum Singleton (ZALECANY!)
    public enum AppConfig {
        INSTANCE;

        private String databaseUrl = "jdbc:h2:mem:piratedb";
        private int port = 8080;
        private int maxConnections = 10;

        public String getDatabaseUrl() { return databaseUrl; }
        public int getPort() { return port; }
        public int getMaxConnections() { return maxConnections; }

        // Umożliwia konfigurację w runtime
        public void configure(String url, int port, int maxConn) {
            this.databaseUrl = url;
            this.port = port;
            this.maxConnections = maxConn;
        }
    }

    // Wariant 2: Double-checked locking (tradycyjny)
    public static class AppConfigClassic {
        private static volatile AppConfigClassic instance;
        private static final Object lock = new Object();

        private String databaseUrl = "jdbc:h2:mem:piratedb";
        private int port = 8080;

        private AppConfigClassic() {
            // Prywatny konstruktor!
        }

        public static AppConfigClassic getInstance() {
            if (instance == null) {
                synchronized (lock) {
                    if (instance == null) {
                        instance = new AppConfigClassic();
                    }
                }
            }
            return instance;
        }

        public String getDatabaseUrl() { return databaseUrl; }
        public int getPort() { return port; }
    }

    public void solution1_singleton() {
        System.out.println("=== ROZWIĄZANIE 1: Singleton ===\n");

        // Wariant Enum
        AppConfig config1 = AppConfig.INSTANCE;
        AppConfig config2 = AppConfig.INSTANCE;

        System.out.println("Enum Singleton:");
        System.out.println("  config1 == config2: " + (config1 == config2));
        System.out.println("  Database URL: " + config1.getDatabaseUrl());
        System.out.println("  Port: " + config1.getPort());

        // Wariant classic
        AppConfigClassic classic1 = AppConfigClassic.getInstance();
        AppConfigClassic classic2 = AppConfigClassic.getInstance();

        System.out.println("\nClassic Singleton:");
        System.out.println("  classic1 == classic2: " + (classic1 == classic2));

        System.out.println("\n✅ Zalecany: Enum (thread-safe, serialization-safe!)");
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * ROZWIĄZANIE 2: Factory - Bronie pirackie
     * ════════════════════════════════════════════════════════════════
     */

    public interface Weapon {
        int damage();
        int range();
        String name();
    }

    public static class Cutlass implements Weapon {
        @Override public int damage() { return 20; }
        @Override public int range() { return 1; }
        @Override public String name() { return "Cutlass (Szabla Piracka)"; }
    }

    public static class Pistol implements Weapon {
        @Override public int damage() { return 35; }
        @Override public int range() { return 5; }
        @Override public String name() { return "Flintlock Pistol"; }
    }

    public static class Cannon implements Weapon {
        @Override public int damage() { return 100; }
        @Override public int range() { return 50; }
        @Override public String name() { return "Ship Cannon"; }
    }

    // Wariant 1: Switch (prosty)
    public static class WeaponFactory {
        public static Weapon create(String type) {
            return switch (type.toLowerCase()) {
                case "cutlass", "sword" -> new Cutlass();
                case "pistol", "gun" -> new Pistol();
                case "cannon" -> new Cannon();
                default -> throw new IllegalArgumentException(
                        "Unknown weapon type: " + type + ". Available: cutlass, pistol, cannon");
            };
        }
    }

    // Wariant 2: Map<String, Supplier> (bardziej rozszerzalny!)
    public static class WeaponFactoryAdvanced {
        private static final Map<String, Supplier<Weapon>> weapons = Map.of(
                "cutlass", Cutlass::new,
                "sword", Cutlass::new,
                "pistol", Pistol::new,
                "gun", Pistol::new,
                "cannon", Cannon::new
        );

        public static Weapon create(String type) {
            Supplier<Weapon> supplier = weapons.get(type.toLowerCase());
            if (supplier == null) {
                throw new IllegalArgumentException("Unknown weapon: " + type);
            }
            return supplier.get();
        }

        // Można dynamicznie dodawać nowe bronie!
        // weapons.put("musket", Musket::new);
    }

    public void solution2_factory() {
        System.out.println("\n=== ROZWIĄZANIE 2: Factory ===\n");

        String[] weaponTypes = {"cutlass", "pistol", "cannon"};

        for (String type : weaponTypes) {
            Weapon weapon = WeaponFactory.create(type);
            System.out.printf("  %s - Damage: %d, Range: %d%n",
                    weapon.name(), weapon.damage(), weapon.range());
        }

        // Test błędnego typu
        System.out.println("\nTest nieznanego typu:");
        try {
            WeaponFactory.create("musket");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✅ Złapano wyjątek: " + e.getMessage());
        }
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * ROZWIĄZANIE 3: Builder - Zamówienie w tawernie
     * ════════════════════════════════════════════════════════════════
     */

    public static class TavernOrder {
        private final String drink;      // WYMAGANE
        private final String food;       // opcjonalne
        private final String dessert;    // opcjonalne
        private final boolean isToGo;    // opcjonalne
        private final int tableNumber;   // opcjonalne

        // Prywatny konstruktor - tylko przez Builder!
        private TavernOrder(Builder builder) {
            this.drink = builder.drink;
            this.food = builder.food;
            this.dessert = builder.dessert;
            this.isToGo = builder.isToGo;
            this.tableNumber = builder.tableNumber;
        }

        // Punkt wejścia - wymagany drink
        public static Builder builder(String drink) {
            return new Builder(drink);
        }

        // Gettery - klasa jest IMMUTABLE!
        public String getDrink() { return drink; }
        public String getFood() { return food; }
        public String getDessert() { return dessert; }
        public boolean isToGo() { return isToGo; }
        public int getTableNumber() { return tableNumber; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("🍺 TavernOrder:\n");
            sb.append("   Drink: ").append(drink).append("\n");
            if (food != null) sb.append("   Food: ").append(food).append("\n");
            if (dessert != null) sb.append("   Dessert: ").append(dessert).append("\n");
            sb.append("   To-go: ").append(isToGo ? "Yes" : "No").append("\n");
            if (tableNumber > 0) sb.append("   Table: #").append(tableNumber).append("\n");
            return sb.toString();
        }

        // Builder jako inner class
        public static class Builder {
            private final String drink;  // wymagane w konstruktorze
            private String food;
            private String dessert;
            private boolean isToGo = false;
            private int tableNumber = 0;

            public Builder(String drink) {
                if (drink == null || drink.isBlank()) {
                    throw new IllegalArgumentException("Drink is required!");
                }
                this.drink = drink;
            }

            public Builder food(String food) {
                this.food = food;
                return this;
            }

            public Builder dessert(String dessert) {
                this.dessert = dessert;
                return this;
            }

            public Builder toGo(boolean isToGo) {
                this.isToGo = isToGo;
                return this;
            }

            public Builder tableNumber(int tableNumber) {
                this.tableNumber = tableNumber;
                return this;
            }

            public TavernOrder build() {
                return new TavernOrder(this);
            }
        }
    }

    public void solution3_builder() {
        System.out.println("\n=== ROZWIĄZANIE 3: Builder ===\n");

        // Zamówienie minimalne (tylko wymagany drink)
        TavernOrder simpleOrder = TavernOrder.builder("Rum")
                .build();
        System.out.println("Zamówienie minimalne:");
        System.out.println(simpleOrder);

        // Zamówienie pełne
        TavernOrder fullOrder = TavernOrder.builder("Grog")
                .food("Fish and Chips")
                .dessert("Parrot Cake")
                .tableNumber(7)
                .build();
        System.out.println("Zamówienie pełne:");
        System.out.println(fullOrder);

        // Zamówienie na wynos
        TavernOrder toGoOrder = TavernOrder.builder("Whiskey")
                .food("Beef Jerky")
                .toGo(true)
                .build();
        System.out.println("Na wynos:");
        System.out.println(toGoOrder);
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * ROZWIĄZANIE 4: Strategy - Nawigacja statku
     * ════════════════════════════════════════════════════════════════
     */

    public interface NavigationStrategy {
        void navigate(String from, String to);
        String methodName();
        int speedRating(); // 1-10
    }

    public static class StarNavigation implements NavigationStrategy {
        @Override
        public void navigate(String from, String to) {
            System.out.println("   ⭐ Navigating from " + from + " to " + to + " using STARS");
            System.out.println("      Speed: SLOW | Time: NIGHT ONLY | Cost: FREE");
        }

        @Override public String methodName() { return "Star Navigation"; }
        @Override public int speedRating() { return 3; }
    }

    public static class CompassNavigation implements NavigationStrategy {
        @Override
        public void navigate(String from, String to) {
            System.out.println("   🧭 Navigating from " + from + " to " + to + " using COMPASS");
            System.out.println("      Speed: MEDIUM | Time: ANY | Cost: REQUIRES COMPASS");
        }

        @Override public String methodName() { return "Compass Navigation"; }
        @Override public int speedRating() { return 6; }
    }

    public static class MapNavigation implements NavigationStrategy {
        @Override
        public void navigate(String from, String to) {
            System.out.println("   🗺️ Navigating from " + from + " to " + to + " using MAP");
            System.out.println("      Speed: FAST | Time: ANY | Cost: REQUIRES MAP");
        }

        @Override public String methodName() { return "Map Navigation"; }
        @Override public int speedRating() { return 9; }
    }

    public static class NavigableShip {
        private final String name;
        private NavigationStrategy strategy;

        public NavigableShip(String name) {
            this.name = name;
            this.strategy = new CompassNavigation(); // domyślna
        }

        public void setNavigationStrategy(NavigationStrategy strategy) {
            System.out.println("\n🚢 " + name + " zmienia metodę na: " + strategy.methodName());
            this.strategy = strategy;
        }

        public void navigate(String from, String to) {
            strategy.navigate(from, to);
        }

        public String getName() { return name; }
    }

    public void solution4_strategy() {
        System.out.println("\n=== ROZWIĄZANIE 4: Strategy ===\n");

        NavigableShip blackPearl = new NavigableShip("Black Pearl");

        // Dzień - używamy kompasu
        System.out.println("🌞 DZIEŃ - używamy kompasu:");
        blackPearl.setNavigationStrategy(new CompassNavigation());
        blackPearl.navigate("Tortuga", "Nassau");

        // Noc - zmieniamy na gwiazdy
        System.out.println("\n🌙 NOC - zmieniamy na nawigację gwiazdową:");
        blackPearl.setNavigationStrategy(new StarNavigation());
        blackPearl.navigate("Nassau", "Havana");

        // Mamy mapę skarbów!
        System.out.println("\n🗺️ Znaleźliśmy mapę - najszybsza trasa:");
        blackPearl.setNavigationStrategy(new MapNavigation());
        blackPearl.navigate("Havana", "Wyspa Skarbów");
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * ROZWIĄZANIE 5: Quiz odpowiedzi
     * ════════════════════════════════════════════════════════════════
     */
    public void solution5_quizAnswers() {
        System.out.println("\n=== ROZWIĄZANIE 5: Quiz ===\n");

        System.out.println("A) Logger aplikacji - jedna instancja");
        System.out.println("   ✅ SINGLETON - jedna instancja loggera dla całej aplikacji\n");

        System.out.println("B) Tworzenie różnych formatów eksportu (PDF, CSV, Excel)");
        System.out.println("   ✅ FACTORY - tworzy odpowiedni eksporter na podstawie formatu\n");

        System.out.println("C) Konfiguracja połączenia HTTP z wieloma opcjami");
        System.out.println("   ✅ BUILDER - timeout, headers, retries... wiele opcjonalnych pól\n");

        System.out.println("D) Kompresja danych - różne algorytmy (ZIP, GZIP, LZ4)");
        System.out.println("   ✅ STRATEGY - zamienne algorytmy z tym samym interfejsem\n");

        System.out.println("E) Obiekt Request z headers, body, method, url, timeout...");
        System.out.println("   ✅ BUILDER - wiele opcjonalnych pól, fluent API\n");
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * BONUS: Mini-projekt (Zadanie domowe)
     * ════════════════════════════════════════════════════════════════
     */

    // CrewMember z Builderem
    public static class CrewMember {
        private final String name;
        private final String role;
        private final int experience;
        private final String weapon;
        private final List<String> skills;

        private CrewMember(Builder builder) {
            this.name = builder.name;
            this.role = builder.role;
            this.experience = builder.experience;
            this.weapon = builder.weapon;
            this.skills = new ArrayList<>(builder.skills);
        }

        public static Builder builder(String name) {
            return new Builder(name);
        }

        @Override
        public String toString() {
            return String.format("%s (%s) - Exp: %d, Weapon: %s, Skills: %s",
                    name, role, experience, weapon, skills);
        }

        public static class Builder {
            private final String name;
            private String role = "Sailor";
            private int experience = 0;
            private String weapon = null;
            private List<String> skills = new ArrayList<>();

            public Builder(String name) { this.name = name; }

            public Builder role(String role) { this.role = role; return this; }
            public Builder experience(int exp) { this.experience = exp; return this; }
            public Builder weapon(String weapon) { this.weapon = weapon; return this; }
            public Builder skill(String skill) { this.skills.add(skill); return this; }

            public CrewMember build() { return new CrewMember(this); }
        }
    }

    // CrewRegistry jako Singleton
    public enum CrewRegistry {
        INSTANCE;

        private final List<CrewMember> crew = new ArrayList<>();

        public void addMember(CrewMember member) { crew.add(member); }
        public List<CrewMember> getAllMembers() { return new ArrayList<>(crew); }
        public int size() { return crew.size(); }
    }

    // RoleFactory
    public static class RoleFactory {
        public static CrewMember createCaptain(String name) {
            return CrewMember.builder(name)
                    .role("Captain")
                    .experience(100)
                    .weapon("Cutlass")
                    .skill("Leadership")
                    .skill("Navigation")
                    .build();
        }

        public static CrewMember createCook(String name) {
            return CrewMember.builder(name)
                    .role("Cook")
                    .experience(50)
                    .weapon("Knife")
                    .skill("Cooking")
                    .skill("Fishing")
                    .build();
        }

        public static CrewMember createSailor(String name) {
            return CrewMember.builder(name)
                    .role("Sailor")
                    .experience(10)
                    .skill("Rope work")
                    .build();
        }
    }

    public void solutionBonus_miniProject() {
        System.out.println("\n=== BONUS: Mini-projekt ===\n");

        // Tworzenie załogi za pomocą Factory
        CrewMember captain = RoleFactory.createCaptain("Jack Sparrow");
        CrewMember cook = RoleFactory.createCook("Cookie");
        CrewMember sailor1 = RoleFactory.createSailor("Will Turner");
        CrewMember sailor2 = RoleFactory.createSailor("Ragetti");

        // Rejestracja w Singleton
        CrewRegistry.INSTANCE.addMember(captain);
        CrewRegistry.INSTANCE.addMember(cook);
        CrewRegistry.INSTANCE.addMember(sailor1);
        CrewRegistry.INSTANCE.addMember(sailor2);

        System.out.println("Załoga Black Pearl:");
        CrewRegistry.INSTANCE.getAllMembers().forEach(m -> System.out.println("  " + m));
        System.out.println("\nLiczba członków: " + CrewRegistry.INSTANCE.size());
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * ROZWIĄZANIE 6: CQRS - Command Query Responsibility Segregation
     * ════════════════════════════════════════════════════════════════
     */
    public void solution6_cqrs() {
        System.out.println("\n=== ROZWIĄZANIE 6: CQRS ===\n");

        // Import klas z pakietu cqrs
        pl.przemekzagorski.training.patterns.cqrs.PirateDatabase database =
                new pl.przemekzagorski.training.patterns.cqrs.PirateDatabase();
        pl.przemekzagorski.training.patterns.cqrs.CommandBus commandBus =
                new pl.przemekzagorski.training.patterns.cqrs.CommandBus();
        pl.przemekzagorski.training.patterns.cqrs.QueryBus queryBus =
                new pl.przemekzagorski.training.patterns.cqrs.QueryBus();

        // Rejestracja handlerów
        commandBus.registerHandler(
                pl.przemekzagorski.training.patterns.cqrs.CreatePirateCommand.class,
                new pl.przemekzagorski.training.patterns.cqrs.CreatePirateCommandHandler(database)
        );
        commandBus.registerHandler(
                pl.przemekzagorski.training.patterns.cqrs.UpdateBountyCommand.class,
                new pl.przemekzagorski.training.patterns.cqrs.UpdateBountyCommandHandler(database)
        );

        queryBus.registerHandler(
                pl.przemekzagorski.training.patterns.cqrs.GetPirateByIdQuery.class,
                new pl.przemekzagorski.training.patterns.cqrs.GetPirateByIdQueryHandler(database)
        );
        queryBus.registerHandler(
                pl.przemekzagorski.training.patterns.cqrs.FindPiratesByRankQuery.class,
                new pl.przemekzagorski.training.patterns.cqrs.FindPiratesByRankQueryHandler(database)
        );

        System.out.println("✅ CQRS System initialized!\n");

        // COMMANDS - zmiany stanu
        System.out.println("📝 Executing Commands (Write Operations):");
        commandBus.execute(new pl.przemekzagorski.training.patterns.cqrs.CreatePirateCommand(
                "Jack Sparrow", "Captain", 10000));
        commandBus.execute(new pl.przemekzagorski.training.patterns.cqrs.CreatePirateCommand(
                "Hector Barbossa", "Captain", 15000));
        commandBus.execute(new pl.przemekzagorski.training.patterns.cqrs.CreatePirateCommand(
                "Will Turner", "First Mate", 5000));

        // QUERIES - odczyt danych
        System.out.println("\n🔍 Executing Queries (Read Operations):");
        var pirate = queryBus.execute(
                new pl.przemekzagorski.training.patterns.cqrs.GetPirateByIdQuery(1L));
        pirate.ifPresent(p -> System.out.println("   Found: " + p));

        var captains = queryBus.execute(
                new pl.przemekzagorski.training.patterns.cqrs.FindPiratesByRankQuery("Captain"));
        System.out.println("   Captains: " + captains.size());
        captains.forEach(c -> System.out.println("   - " + c));

        // UPDATE Command
        System.out.println("\n📝 Updating bounty:");
        commandBus.execute(new pl.przemekzagorski.training.patterns.cqrs.UpdateBountyCommand(1L, 50000));

        // Verify
        System.out.println("\n🔍 Verifying update:");
        pirate = queryBus.execute(new pl.przemekzagorski.training.patterns.cqrs.GetPirateByIdQuery(1L));
        pirate.ifPresent(p -> System.out.println("   Updated: " + p));

        System.out.println("\n💡 KEY TAKEAWAYS:");
        System.out.println("   ✅ Commands CHANGE state (void)");
        System.out.println("   ✅ Queries READ data (return result)");
        System.out.println("   ✅ Clear separation of concerns!");
    }


    /**
     * Uruchom wszystkie rozwiązania.
     */
    public static void main(String[] args) {
        PatternExercisesSolutions solutions = new PatternExercisesSolutions();

        solutions.solution1_singleton();
        solutions.solution2_factory();
        solutions.solution3_builder();
        solutions.solution4_strategy();
        solutions.solution5_quizAnswers();
        solutions.solution6_cqrs();
        solutions.solutionBonus_miniProject();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ Wszystkie rozwiązania wzorców wykonane!");
        System.out.println("=".repeat(60));
    }
}
