package pl.przemekzagorski.training.patterns;

/**
 * ╔═══════════════════════════════════════════════════════════════════╗
 * ║              ĆWICZENIA - WZORCE PROJEKTOWE                       ║
 * ╠═══════════════════════════════════════════════════════════════════╣
 * ║  Czas: 25 minut                                                  ║
 * ║  Poziom: Średniozaawansowany                                     ║
 * ╚═══════════════════════════════════════════════════════════════════╝
 *
 * Zastosuj poznane wzorce do realnych problemów!
 */
public class PatternExercises {

    /**
     * ════════════════════════════════════════════════════════════════
     * ĆWICZENIE 1: Singleton - Konfiguracja aplikacji
     * ════════════════════════════════════════════════════════════════
     * 🎯 POZIOM: ŁATWY (gotowy kod - uruchom i obserwuj!)
     *
     * KONTEKST:
     * Potrzebujesz globalnej konfiguracji aplikacji, która jest ładowana
     * raz przy starcie i dostępna wszędzie. Singleton gwarantuje JEDNĄ instancję!
     *
     * ZADANIE:
     * 1. Uruchom metodę i OBSERWUJ jak działa Singleton
     * 2. Sprawdź czy config1 == config2 (ta sama instancja!)
     * 3. EKSPERYMENTUJ z ENUM vs Classic Singleton
     */
    public void exercise1_singleton_appConfig() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎯 ĆWICZENIE 1: Singleton - Obserwacja");
        System.out.println("=".repeat(60));

        // ════════════════════════════════════════════════════════════════
        // ENUM SINGLETON (ZALECANY!)
        // ════════════════════════════════════════════════════════════════

        System.out.println("\n1️⃣ ENUM SINGLETON:");
        System.out.println("   Najprostszy i najbezpieczniejszy sposób!");

        AppConfigEnum config1 = AppConfigEnum.INSTANCE;
        AppConfigEnum config2 = AppConfigEnum.INSTANCE;

        System.out.println("\n🔍 OBSERWUJ:");
        System.out.println("   config1 == config2: " + (config1 == config2));
        System.out.println("   config1.hashCode(): " + config1.hashCode());
        System.out.println("   config2.hashCode(): " + config2.hashCode());
        System.out.println("   ✅ Ta sama instancja!");

        System.out.println("\n📊 Konfiguracja:");
        System.out.println("   Database URL: " + config1.getDatabaseUrl());
        System.out.println("   Port: " + config1.getPort());
        System.out.println("   Max Connections: " + config1.getMaxConnections());

        // ════════════════════════════════════════════════════════════════
        // 💡 EKSPERYMENT 1: Zmiana konfiguracji
        // ════════════════════════════════════════════════════════════════
        // ODKOMENTUJ poniższe linie:
        //
        // System.out.println("\n🧪 EKSPERYMENT 1: Zmiana konfiguracji");
        // config1.configure("jdbc:postgresql://localhost/newdb", 9090, 50);
        // System.out.println("   config1.getPort(): " + config1.getPort());
        // System.out.println("   config2.getPort(): " + config2.getPort());
        //
        // ❓ PYTANIE: Czy config2 też ma nowy port?
        // 💡 ODPOWIEDŹ: TAK! To ta sama instancja, więc zmiana widoczna wszędzie!

        // ════════════════════════════════════════════════════════════════
        // 💡 EKSPERYMENT 2: Classic Singleton (double-checked locking)
        // ════════════════════════════════════════════════════════════════
        // ODKOMENTUJ poniższe linie:
        //
        // System.out.println("\n🧪 EKSPERYMENT 2: Classic Singleton");
        // AppConfigClassic classic1 = AppConfigClassic.getInstance();
        // AppConfigClassic classic2 = AppConfigClassic.getInstance();
        // System.out.println("   classic1 == classic2: " + (classic1 == classic2));
        // System.out.println("   ✅ Też działa, ale ENUM jest prostszy!");
        //
        // ❓ PYTANIE: Dlaczego ENUM jest lepszy?
        // 💡 ODPOWIEDŹ:
        //    - Thread-safe bez synchronized
        //    - Serialization-safe (nie można stworzyć drugiej instancji)
        //    - Krótszy kod (1 linia vs 20 linii)

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ Ćwiczenie 1 zakończone!");
        System.out.println("💡 Teraz odkomentuj EKSPERYMENTY i pobaw się!");
        System.out.println("=".repeat(60));
    }

    // ════════════════════════════════════════════════════════════════
    // ENUM SINGLETON (dla Exercise 1)
    // ════════════════════════════════════════════════════════════════
    public enum AppConfigEnum {
        INSTANCE;

        private String databaseUrl = "jdbc:h2:mem:piratedb";
        private int port = 8080;
        private int maxConnections = 10;

        public String getDatabaseUrl() { return databaseUrl; }
        public int getPort() { return port; }
        public int getMaxConnections() { return maxConnections; }

        public void configure(String url, int port, int maxConn) {
            this.databaseUrl = url;
            this.port = port;
            this.maxConnections = maxConn;
        }
    }

    // ════════════════════════════════════════════════════════════════
    // CLASSIC SINGLETON (dla EKSPERYMENT 2)
    // ════════════════════════════════════════════════════════════════
    // ODKOMENTUJ jeśli chcesz zobaczyć tradycyjną implementację:
    //
    // public static class AppConfigClassic {
    //     private static volatile AppConfigClassic instance;
    //     private static final Object lock = new Object();
    //
    //     private String databaseUrl = "jdbc:h2:mem:piratedb";
    //     private int port = 8080;
    //
    //     private AppConfigClassic() {} // Prywatny konstruktor!
    //
    //     public static AppConfigClassic getInstance() {
    //         if (instance == null) {
    //             synchronized (lock) {
    //                 if (instance == null) {
    //                     instance = new AppConfigClassic();
    //                 }
    //             }
    //         }
    //         return instance;
    //     }
    //
    //     public String getDatabaseUrl() { return databaseUrl; }
    //     public int getPort() { return port; }
    // }

    /**
     * ════════════════════════════════════════════════════════════════
     * ĆWICZENIE 2: Factory - Bronie pirackie
     * ════════════════════════════════════════════════════════════════
     * 🎯 POZIOM: ŁATWY (gotowy kod - uruchom i obserwuj!)
     *
     * KONTEKST:
     * Piraci używają różnych broni: Cutlass (szabla), Pistol (pistolet), Cannon (działo).
     * Factory tworzy odpowiednią broń na podstawie typu (String).
     *
     * ZADANIE:
     * 1. Uruchom metodę i OBSERWUJ jak Factory tworzy różne bronie
     * 2. Sprawdź damage i range każdej broni
     * 3. EKSPERYMENTUJ z różnymi typami!
     */
    public void exercise2_factory_weapons() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎯 ĆWICZENIE 2: Factory - Obserwacja");
        System.out.println("=".repeat(60));

        // ════════════════════════════════════════════════════════════════
        // TWORZENIE BRONI ZA POMOCĄ FACTORY
        // ════════════════════════════════════════════════════════════════

        System.out.println("\n🔍 OBSERWUJ: Factory tworzy różne bronie");

        String[] weaponTypes = {"cutlass", "pistol", "cannon"};

        for (String type : weaponTypes) {
            Weapon weapon = WeaponFactory.create(type);
            System.out.printf("\n⚔️  %s:%n", type.toUpperCase());
            System.out.printf("   Nazwa: %s%n", weapon.name());
            System.out.printf("   Damage: %d%n", weapon.damage());
            System.out.printf("   Range: %d%n", weapon.range());
        }

        // ════════════════════════════════════════════════════════════════
        // PORÓWNANIE BRONI
        // ════════════════════════════════════════════════════════════════

        System.out.println("\n📊 PORÓWNANIE:");
        Weapon cutlass = WeaponFactory.create("cutlass");
        Weapon cannon = WeaponFactory.create("cannon");

        System.out.println("   Cutlass damage: " + cutlass.damage());
        System.out.println("   Cannon damage: " + cannon.damage());
        System.out.println("   Różnica: " + (cannon.damage() - cutlass.damage()) + " punktów!");

        // ════════════════════════════════════════════════════════════════
        // 💡 EKSPERYMENT 1: Aliasy (sword = cutlass)
        // ════════════════════════════════════════════════════════════════
        // ODKOMENTUJ poniższe linie:
        //
        // System.out.println("\n🧪 EKSPERYMENT 1: Aliasy");
        // Weapon sword = WeaponFactory.create("sword");
        // Weapon cutlass2 = WeaponFactory.create("cutlass");
        // System.out.println("   sword.name(): " + sword.name());
        // System.out.println("   cutlass.name(): " + cutlass2.name());
        // System.out.println("   ✅ 'sword' i 'cutlass' to ta sama broń!");
        //
        // ❓ PYTANIE: Jak Factory obsługuje aliasy?
        // 💡 ODPOWIEDŹ: W switch/case: case "cutlass", "sword" -> new Cutlass();

        // ════════════════════════════════════════════════════════════════
        // 💡 EKSPERYMENT 2: Nieznany typ (wyjątek)
        // ════════════════════════════════════════════════════════════════
        // ODKOMENTUJ poniższe linie:
        //
        // System.out.println("\n🧪 EKSPERYMENT 2: Nieznany typ");
        // try {
        //     Weapon musket = WeaponFactory.create("musket");
        // } catch (IllegalArgumentException e) {
        //     System.out.println("   ❌ Błąd: " + e.getMessage());
        //     System.out.println("   ✅ Factory poprawnie rzuca wyjątek!");
        // }
        //
        // ❓ PYTANIE: Dlaczego rzucamy wyjątek zamiast zwracać null?
        // 💡 ODPOWIEDŹ: Fail-fast! Lepiej od razu zobaczyć błąd niż NullPointerException później!

        // ════════════════════════════════════════════════════════════════
        // 💡 EKSPERYMENT 3: Case insensitive
        // ════════════════════════════════════════════════════════════════
        // ODKOMENTUJ poniższe linie:
        //
        // System.out.println("\n🧪 EKSPERYMENT 3: Case insensitive");
        // Weapon upper = WeaponFactory.create("CUTLASS");
        // Weapon lower = WeaponFactory.create("cutlass");
        // Weapon mixed = WeaponFactory.create("CuTlAsS");
        // System.out.println("   CUTLASS: " + upper.name());
        // System.out.println("   cutlass: " + lower.name());
        // System.out.println("   CuTlAsS: " + mixed.name());
        // System.out.println("   ✅ Wszystkie zwracają tę samą broń!");
        //
        // ❓ PYTANIE: Jak to działa?
        // 💡 ODPOWIEDŹ: type.toLowerCase() w Factory!

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ Ćwiczenie 2 zakończone!");
        System.out.println("💡 Teraz odkomentuj EKSPERYMENTY i pobaw się!");
        System.out.println("=".repeat(60));
    }

    // ════════════════════════════════════════════════════════════════
    // WEAPON INTERFACE + IMPLEMENTACJE (dla Exercise 2)
    // ════════════════════════════════════════════════════════════════

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

    // ════════════════════════════════════════════════════════════════
    // WEAPON FACTORY (dla Exercise 2)
    // ════════════════════════════════════════════════════════════════

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

    /**
     * ════════════════════════════════════════════════════════════════
     * ĆWICZENIE 3: Builder - Zamówienie w tawernie
     * ════════════════════════════════════════════════════════════════
     * 🎯 POZIOM: ŚREDNI (uzupełnij TODO)
     *
     * KONTEKST:
     * Pirat składa zamówienie w tawernie: napój główny + dodatki.
     * Zamówienie może mieć: drink (WYMAGANY), food, dessert, isToGo, tableNumber (OPCJONALNE).
     * Builder pozwala tworzyć obiekt z wieloma opcjonalnymi parametrami!
     *
     * ZADANIE:
     * 1. Uzupełnij brakujące metody w Builderze (food, dessert, toGo, tableNumber)
     * 2. Uzupełnij konstruktor TavernOrder (przypisz pola z buildera)
     * 3. Uruchom i sprawdź czy działa!
     */
    public void exercise3_builder_tavernOrder() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎯 ĆWICZENIE 3: Builder - Uzupełnij TODO");
        System.out.println("=".repeat(60));

        // ════════════════════════════════════════════════════════════════
        // PRZYKŁAD 1: Minimalne zamówienie (tylko drink)
        // ════════════════════════════════════════════════════════════════

        System.out.println("\n1️⃣ MINIMALNE ZAMÓWIENIE:");
        TavernOrder simpleOrder = TavernOrder.builder("Rum")
                .build();
        System.out.println(simpleOrder);

        // ════════════════════════════════════════════════════════════════
        // PRZYKŁAD 2: Pełne zamówienie
        // ════════════════════════════════════════════════════════════════

        System.out.println("\n2️⃣ PEŁNE ZAMÓWIENIE:");
        TavernOrder fullOrder = TavernOrder.builder("Grog")
                //.food("Fish and Chips")
                //.dessert("Parrot Cake")
                //.tableNumber(7)
                .build();
        System.out.println(fullOrder);

        // ════════════════════════════════════════════════════════════════
        // PRZYKŁAD 3: Zamówienie na wynos
        // ════════════════════════════════════════════════════════════════

        System.out.println("\n3️⃣ ZAMÓWIENIE NA WYNOS:");
        TavernOrder toGoOrder = TavernOrder.builder("Whiskey")
               // .food("Beef Jerky")
               // .toGo(true)
                .build();
        System.out.println(toGoOrder);

        // ════════════════════════════════════════════════════════════════
        // KORZYŚCI BUILDERA
        // ════════════════════════════════════════════════════════════════

        System.out.println("\n✅ KORZYŚCI BUILDERA:");
        System.out.println("   ✓ Fluent API (czytelne chainowanie)");
        System.out.println("   ✓ Opcjonalne parametry (nie trzeba 10 konstruktorów!)");
        System.out.println("   ✓ Immutable obiekt (bezpieczny w wielowątkowym środowisku)");
        System.out.println("   ✓ Walidacja w jednym miejscu (build())");

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ Ćwiczenie 3 zakończone!");
        System.out.println("💡 Sprawdź rozwiązanie w PatternExercisesSolutions.java");
        System.out.println("=".repeat(60));
    }

    // ════════════════════════════════════════════════════════════════
    // TAVERN ORDER + BUILDER (dla Exercise 3)
    // ════════════════════════════════════════════════════════════════

    public static class TavernOrder {
        private final String drink;      // WYMAGANE
        //private final String food;       // opcjonalne
        //private final String dessert;    // opcjonalne
        //private final boolean isToGo;    // opcjonalne
        //private final int tableNumber;   // opcjonalne

        // TODO: Uzupełnij konstruktor - przypisz pola z buildera
        private TavernOrder(Builder builder) {
            this.drink = builder.drink;
            // TODO: Przypisz pozostałe pola (food, dessert, isToGo, tableNumber)
            // Hint: this.food = builder.food;
        }

        // Punkt wejścia - wymagany drink
        public static Builder builder(String drink) {
            return new Builder(drink);
        }

        // Gettery - klasa jest IMMUTABLE!
//        public String getDrink() { return drink; }
//        public String getFood() { return food; }
//        public String getDessert() { return dessert; }
//        public boolean isToGo() { return isToGo; }
//        public int getTableNumber() { return tableNumber; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("🍺 TavernOrder:\n");
//            sb.append("   Drink: ").append(drink).append("\n");
//            if (food != null) sb.append("   Food: ").append(food).append("\n");
//            if (dessert != null) sb.append("   Dessert: ").append(dessert).append("\n");
//            sb.append("   To-go: ").append(isToGo ? "Yes" : "No").append("\n");
//            if (tableNumber > 0) sb.append("   Table: #").append(tableNumber).append("\n");
            return sb.toString();
        }

        // ════════════════════════════════════════════════════════════════
        // BUILDER (inner class)
        // ════════════════════════════════════════════════════════════════

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

            // TODO: Uzupełnij metody buildera (zwracają 'this' dla chainowania!)
            // Hint: public Builder food(String food) { this.food = food; return this; }

            // TODO: Metoda food(String food)

            // TODO: Metoda dessert(String dessert)

            // TODO: Metoda toGo(boolean isToGo)

            // TODO: Metoda tableNumber(int tableNumber)

            public TavernOrder build() {
                return new TavernOrder(this);
            }
        }
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * ĆWICZENIE 4: Strategy - Nawigacja statku
     * ════════════════════════════════════════════════════════════════
     * 🎯 POZIOM: TRUDNY (napisz od zera z pomocą wskazówek)
     *
     * KONTEKST:
     * Statek może nawigować różnymi metodami:
     * - StarNavigation (po gwiazdach - wolna, działa nocą, za darmo)
     * - CompassNavigation (średnia, działa zawsze, wymaga kompasu)
     * - MapNavigation (szybka, działa zawsze, wymaga mapy)
     *
     * Strategy pozwala ZMIENIAĆ algorytm w runtime!
     *
     * ZADANIE:
     * 1. Utwórz interfejs NavigationStrategy z metodami: navigate(from, to), methodName(), speedRating()
     * 2. Zaimplementuj 3 strategie (StarNavigation, CompassNavigation, MapNavigation)
     * 3. Utwórz klasę NavigableShip która używa NavigationStrategy
     * 4. Pokaż zmianę strategii w runtime (dzień → noc → mapa skarbów)
     *
     * STRUKTURA:
     * - NavigationStrategy (interface)
     *   - navigate(String from, String to) - wykonuje nawigację
     *   - methodName() - zwraca nazwę metody
     *   - speedRating() - zwraca ocenę szybkości (1-10)
     * - StarNavigation, CompassNavigation, MapNavigation (implementacje)
     * - NavigableShip (klasa)
     *   - setNavigationStrategy(NavigationStrategy) - zmienia strategię
     *   - navigate(String from, String to) - deleguje do strategii
     *
     * 💡 WSKAZÓWKI:
     * - interface NavigationStrategy { void navigate(String from, String to); ... }
     * - class StarNavigation implements NavigationStrategy { ... }
     * - class NavigableShip { private NavigationStrategy strategy; ... }
     * - ship.setNavigationStrategy(new CompassNavigation());
     * - ship.navigate("Tortuga", "Nassau"); // używa kompasu
     *
     * SCENARIUSZ TESTOWY:
     *   NavigableShip ship = new NavigableShip("Black Pearl");
     *   ship.setNavigationStrategy(new CompassNavigation());
     *   ship.navigate("Tortuga", "Nassau"); // dzień - kompas
     *
     *   ship.setNavigationStrategy(new StarNavigation());
     *   ship.navigate("Nassau", "Havana");  // noc - gwiazdy
     *
     *   ship.setNavigationStrategy(new MapNavigation());
     *   ship.navigate("Havana", "Treasure Island"); // mapa skarbów!
     *
     * 🆘 Jeśli utkniesz, sprawdź PatternExercisesSolutions.solution4_strategy()
     */
    public void exercise4_strategy_navigation() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎯 ĆWICZENIE 4: Strategy - Napisz od zera");
        System.out.println("=".repeat(60));

        // TODO: Utwórz interfejs NavigationStrategy
        // interface NavigationStrategy {
        //     void navigate(String from, String to);
        //     String methodName();
        //     int speedRating();
        // }

        // TODO: Zaimplementuj 3 strategie
        // class StarNavigation implements NavigationStrategy { ... }
        // class CompassNavigation implements NavigationStrategy { ... }
        // class MapNavigation implements NavigationStrategy { ... }

        // TODO: Utwórz klasę NavigableShip
        // class NavigableShip {
        //     private NavigationStrategy strategy;
        //     public void setNavigationStrategy(NavigationStrategy s) { ... }
        //     public void navigate(String from, String to) { ... }
        // }

        // TODO: Przetestuj zmianę strategii w runtime
        // NavigableShip ship = new NavigableShip("Black Pearl");
        //
        // System.out.println("\n🌞 DZIEŃ - używamy kompasu:");
        // ship.setNavigationStrategy(new CompassNavigation());
        // ship.navigate("Tortuga", "Nassau");
        //
        // System.out.println("\n🌙 NOC - zmieniamy na gwiazdy:");
        // ship.setNavigationStrategy(new StarNavigation());
        // ship.navigate("Nassau", "Havana");
        //
        // System.out.println("\n🗺️ Znaleźliśmy mapę - najszybsza trasa:");
        // ship.setNavigationStrategy(new MapNavigation());
        // ship.navigate("Havana", "Treasure Island");

        System.out.println("\n💡 WSKAZÓWKA: Sprawdź strukturę w komentarzach powyżej!");
        System.out.println("🆘 Jeśli utkniesz, zobacz PatternExercisesSolutions.java");
        System.out.println("=".repeat(60));
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * ĆWICZENIE 5: Rozpoznaj wzorzec! (QUIZ)
     * ════════════════════════════════════════════════════════════════
     *
     * Przeczytaj poniższe scenariusze i określ KTÓRY wzorzec zastosować:
     *
     * A) Logger aplikacji - powinna być tylko jedna instancja
     *    Odpowiedź: _______________
     *
     * B) Tworzenie różnych formatów eksportu (PDF, CSV, Excel)
     *    Odpowiedź: _______________
     *
     * C) Konfiguracja połączenia HTTP z wieloma opcjami
     *    Odpowiedź: _______________
     *
     * D) Kompresja danych - różne algorytmy (ZIP, GZIP, LZ4)
     *    Odpowiedź: _______________
     *
     * E) Obiekt Request z headers, body, method, url, timeout...
     *    Odpowiedź: _______________
     *
     * ODPOWIEDZI (odwróć kartkę 😉):
     * A) Singleton, B) Factory, C) Builder, D) Strategy, E) Builder
     */

    /**
     * ════════════════════════════════════════════════════════════════
     * 💡 ZADANIE DOMOWE: Mini-projekt z 3 wzorcami
     * ════════════════════════════════════════════════════════════════
     *
     * SCENARIUSZ:
     * Stwórz system zarządzania załogą piracką:
     *
     * 1. CrewMember - Builder (name, role, experience, weapon, skills[])
     *    - Wymagane: name
     *    - Opcjonalne: role, experience, weapon, skills
     *
     * 2. CrewRegistry - Singleton (lista wszystkich członków załogi)
     *    - addMember(CrewMember)
     *    - getAllMembers()
     *    - size()
     *
     * 3. RoleFactory - tworzy CrewMember z predefiniowaną konfiguracją
     *    - createCaptain(name) → CrewMember z weapon="Cutlass", exp=100, skills=["Leadership", "Navigation"]
     *    - createCook(name) → CrewMember z weapon="Knife", exp=50, skills=["Cooking", "Fishing"]
     *    - createSailor(name) → CrewMember z weapon=null, exp=10, skills=["Rope work"]
     *
     * BONUS: Dodaj Strategy dla obliczania wynagrodzenia:
     *    - DailyWageStrategy - stała płaca za dzień (np. 10 gold/day)
     *    - LootShareStrategy - procent z łupu (np. 5% dla marynarza, 20% dla kapitana)
     *
     * PRZYKŁAD UŻYCIA:
     *   CrewMember captain = RoleFactory.createCaptain("Jack Sparrow");
     *   CrewRegistry.INSTANCE.addMember(captain);
     *
     *   CrewMember cook = RoleFactory.createCook("Cookie");
     *   CrewRegistry.INSTANCE.addMember(cook);
     *
     *   System.out.println("Załoga: " + CrewRegistry.INSTANCE.size());
     *
     * 🆘 Rozwiązanie w PatternExercisesSolutions.solutionBonus_miniProject()
     */

    // ═══════════════════════════════════════════════════════════════════════════════════════
    // EXERCISE 6: CQRS - Command Query Responsibility Segregation ⭐⭐
    // ═══════════════════════════════════════════════════════════════════════════════════════

    /**
     * 🎯 CEL: Zrozumieć separację Commands (zmiany) od Queries (odczyt)
     *
     * 📚 TEORIA:
     * - Command = ZMIENIA stan (void) - CREATE, UPDATE, DELETE
     * - Query = ODCZYTUJE dane (zwraca wynik) - READ
     * - CommandBus i QueryBus rozdzielają odpowiedzialności
     *
     * 🏴‍☠️ SCENARIUSZ: System zarządzania piratami
     *
     * ✅ ZADANIE:
     * 1. Uruchom kod i obserwuj separację Commands/Queries
     * 2. Dodaj nową komendę: DeletePirateCommand
     * 3. Dodaj nowe zapytanie: CountPiratesQuery
     */
    public void exercise6_cqrs_pirateManagement() {
        System.out.println("═".repeat(70));
        System.out.println("EXERCISE 6: CQRS - Command Query Responsibility Segregation ⭐⭐");
        System.out.println("═".repeat(70));
        System.out.println();

        // TODO: Zaimplementuj CQRS pattern
        // 1. Stwórz Commands (CreatePirate, UpdateBounty)
        // 2. Stwórz Queries (GetPirateById, FindByRank)
        // 3. Stwórz CommandBus i QueryBus
        // 4. Zarejestruj handlery
        // 5. Wykonaj operacje

        System.out.println("💡 HINT: Sprawdź CQRSDemo.java dla pełnego przykładu!");
        System.out.println("💡 HINT: Commands zwracają void, Queries zwracają wynik!");
        System.out.println();
    }


    /**
     * Uruchom wszystkie ćwiczenia.
     */
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════════╗");
        System.out.println("║              ĆWICZENIA - WZORCE PROJEKTOWE                       ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════════╣");
        System.out.println("║  PROGRESJA TRUDNOŚCI:                                            ║");
        System.out.println("║  1. Singleton (ŁATWY) - uruchom i obserwuj                       ║");
        System.out.println("║  2. Factory (ŁATWY) - uruchom i eksperymentuj                    ║");
        System.out.println("║  3. Builder (ŚREDNI) - uzupełnij TODO                            ║");
        System.out.println("║  4. Strategy (TRUDNY) - napisz od zera                           ║");
        System.out.println("║  5. Quiz (QUIZ) - rozpoznaj wzorzec na papierze                  ║");
        System.out.println("║  6. CQRS (ŚREDNI) - separacja Commands/Queries                   ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("🚀 Uruchamiam ćwiczenia...\n");

        PatternExercises exercises = new PatternExercises();

        // Exercise 1-2: ŁATWE - gotowy kod, uruchom i obserwuj!
        exercises.exercise1_singleton_appConfig();
        exercises.exercise2_factory_weapons();

        // Exercise 3: ŚREDNI - uzupełnij TODO
        exercises.exercise3_builder_tavernOrder();

        // Exercise 4: TRUDNY - napisz od zera
        exercises.exercise4_strategy_navigation();

        // Exercise 6: ŚREDNI - CQRS pattern
        exercises.exercise6_cqrs_pirateManagement();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ Wszystkie ćwiczenia wykonane!");
        System.out.println("💡 Sprawdź rozwiązania w PatternExercisesSolutions.java");
        System.out.println("📝 Ćwiczenie 5 (Quiz) - rozwiąż na papierze!");
        System.out.println("=".repeat(60));
    }
}
