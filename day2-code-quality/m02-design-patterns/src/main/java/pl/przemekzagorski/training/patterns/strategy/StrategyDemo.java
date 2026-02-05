package pl.przemekzagorski.training.patterns.strategy;

/**
 * Demonstracja wzorca Strategy.
 */
public class StrategyDemo {

    public static void main(String[] args) {
        System.out.println("🏴‍☠️ Strategy Pattern Demo");
        System.out.println("===========================\n");

        demonstrateWithoutStrategy();
        demonstrateWithStrategy();
        demonstrateWithLambda();
    }

    /**
     * BEZ Strategy - if/else koszmar.
     */
    private static void demonstrateWithoutStrategy() {
        System.out.println("1️⃣ BEZ Strategy (zły kod z if/else)\n");

        System.out.println("""
            void attack(String type, String target) {
                if (type.equals("cannon")) {
                    // 20 linii kodu ataku armatami
                } else if (type.equals("boarding")) {
                    // 20 linii kodu abordażu
                } else if (type.equals("ramming")) {
                    // 20 linii kodu taranowania
                } else if (type.equals("torpedo")) {  // Nowy typ!
                    // 20 linii kodu
                }
                // Metoda rośnie i rośnie...
            }
            
            ❌ Naruszenie Open/Closed Principle
            ❌ Trudne do testowania
            ❌ Jedna wielka metoda
            """);
    }

    /**
     * Z Strategy - elegancki polimorfizm.
     */
    private static void demonstrateWithStrategy() {
        System.out.println("2️⃣ Z Strategy (dobry kod)\n");

        BattleShip blackPearl = new BattleShip("Black Pearl");
        String enemy = "HMS Interceptor";

        // Domyślna strategia: armaty
        System.out.println(">>> Atak domyślną strategią (armaty):");
        blackPearl.attack(enemy);

        // Zmiana strategii na abordaż
        System.out.println("\n>>> Zmiana strategii na abordaż:");
        blackPearl.setAttackStrategy(new BoardingAttack());
        blackPearl.attack(enemy);

        // Desperacki manewr - taranowanie!
        System.out.println("\n>>> Sytuacja krytyczna - taranowanie!");
        blackPearl.setAttackStrategy(new RammingAttack());
        blackPearl.attack(enemy);

        System.out.println("""
            
            ✅ Każda strategia w osobnej klasie
            ✅ Łatwo dodać nową strategię (nowa klasa)
            ✅ Łatwo testować każdą strategię osobno
            ✅ Można zmieniać strategię w runtime
            """);
    }

    /**
     * Strategy z lambdami - dla prostych przypadków.
     */
    private static void demonstrateWithLambda() {
        System.out.println("3️⃣ Strategy z lambdami (Java 8+)\n");

        BattleShip flyingDutchman = new BattleShip("Flying Dutchman");

        // Strategy jako lambda!
        flyingDutchman.setAttackStrategy((attacker, target) -> {
            System.out.println("🦑 " + attacker + " uwalnia Krakena na " + target + "!");
            System.out.println("   Macki oplatają wrogi statek!");
        });

        flyingDutchman.attack("HMS Endeavour");

        System.out.println("""
            
            ✅ Dla prostych strategii - lambda wystarczy
            ✅ Nie trzeba tworzyć osobnej klasy
            ✅ @FunctionalInterface umożliwia lambdy
            """);
    }
}

