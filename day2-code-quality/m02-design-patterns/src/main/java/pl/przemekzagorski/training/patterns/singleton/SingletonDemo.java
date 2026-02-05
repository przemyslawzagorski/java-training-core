package pl.przemekzagorski.training.patterns.singleton;

/**
 * Demonstracja wzorca Singleton.
 */
public class SingletonDemo {

    public static void main(String[] args) {
        System.out.println("🏴‍☠️ Singleton Pattern Demo");
        System.out.println("===========================\n");

        demonstrateBasicSingleton();
        demonstrateEnumSingleton();
    }

    private static void demonstrateBasicSingleton() {
        System.out.println("1️⃣ Podstawowy Singleton\n");

        // Pierwsze pobranie - tworzy instancję
        Captain captain1 = Captain.getInstance();
        captain1.giveOrder("Podnieść kotwicę!");

        // Drugie pobranie - ta sama instancja
        Captain captain2 = Captain.getInstance();
        captain2.giveOrder("Ustawić żagle!");

        // Sprawdzenie czy to ta sama instancja
        System.out.println("\n>>> captain1 == captain2: " + (captain1 == captain2));
        System.out.println(">>> To TA SAMA instancja!\n");

        // Zmiana na jednej referencji wpływa na drugą
        captain1.setName("Hector Barbossa");
        System.out.println(">>> Zmieniono imię przez captain1");
        System.out.println(">>> captain2.getName(): " + captain2.getName());
        System.out.println(">>> Bo to TEN SAM obiekt!\n");
    }

    private static void demonstrateEnumSingleton() {
        System.out.println("2️⃣ Enum Singleton (zalecany!)\n");

        // Dostęp przez INSTANCE
        CaptainEnum captain = CaptainEnum.INSTANCE;
        captain.giveOrder("Kurs na Tortugę!");

        // Zawsze ta sama instancja
        CaptainEnum captain2 = CaptainEnum.INSTANCE;
        System.out.println("\n>>> captain == captain2: " + (captain == captain2));
        System.out.println(">>> Enum gwarantuje jedną instancję!\n");
    }
}

