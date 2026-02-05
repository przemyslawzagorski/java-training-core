package pl.przemekzagorski.training.patterns.factory;

/**
 * Demonstracja wzorca Factory.
 */
public class FactoryDemo {

    public static void main(String[] args) {
        System.out.println("🏴‍☠️ Factory Pattern Demo");
        System.out.println("=========================\n");

        demonstrateWithoutFactory();
        demonstrateWithFactory();
        demonstrateSemanticFactory();
    }

    /**
     * BEZ Factory - kod klienta musi znać konkretne klasy.
     */
    private static void demonstrateWithoutFactory() {
        System.out.println("1️⃣ BEZ Factory (zły kod)\n");

        // Kod jest związany z konkretnymi implementacjami
        Ship ship1 = new Galleon("Black Pearl");
        Ship ship2 = new Frigate("Queen Anne's Revenge");
        Ship ship3 = new Sloop("Interceptor");

        System.out.println("   Utworzono:");
        System.out.println("   • " + ship1);
        System.out.println("   • " + ship2);
        System.out.println("   • " + ship3);

        System.out.println("\n   ❌ Problem: Musimy znać wszystkie klasy!");
        System.out.println("   ❌ Trudno zmienić implementację!\n");
    }

    /**
     * Z Factory - kod klienta używa tylko interfejsu.
     */
    private static void demonstrateWithFactory() {
        System.out.println("2️⃣ Z Factory (dobry kod)\n");

        // Używamy factory - nie znamy konkretnych klas!
        Ship ship1 = ShipFactory.createShip(ShipFactory.ShipType.GALLEON, "Black Pearl");
        Ship ship2 = ShipFactory.createShip(ShipFactory.ShipType.FRIGATE, "Queen Anne's Revenge");
        Ship ship3 = ShipFactory.createShip("sloop", "Interceptor");

        System.out.println("   Utworzono:");
        System.out.println("   • " + ship1);
        System.out.println("   • " + ship2);
        System.out.println("   • " + ship3);

        // Możemy używać polimorficznie
        System.out.println("\n   Wszystkie statki płyną:");
        ship1.sail();
        ship2.sail();
        ship3.sail();

        System.out.println("\n   ✅ Kod klienta nie zna konkretnych klas!");
        System.out.println("   ✅ Łatwo dodać nowy typ statku!\n");
    }

    /**
     * Factory z semantycznymi metodami.
     */
    private static void demonstrateSemanticFactory() {
        System.out.println("3️⃣ Factory z semantycznymi metodami\n");

        Ship battleship = ShipFactory.createBattleship("HMS Victory");
        Ship scout = ShipFactory.createScoutShip("Swift");
        Ship trader = ShipFactory.createTradeShip("Merchant Queen");

        System.out.println("\n   Flota piracka gotowa:");
        battleship.attack();
        scout.sail();
        trader.sail();
    }
}

