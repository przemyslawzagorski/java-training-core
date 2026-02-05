package pl.przemekzagorski.training.patterns.builder;

/**
 * Demonstracja wzorca Builder.
 */
public class BuilderDemo {

    public static void main(String[] args) {
        System.out.println("🏴‍☠️ Builder Pattern Demo");
        System.out.println("=========================\n");

        demonstrateWithoutBuilder();
        demonstrateWithBuilder();
    }

    /**
     * BEZ Builder - konstruktor z wieloma parametrami = koszmar!
     */
    private static void demonstrateWithoutBuilder() {
        System.out.println("1️⃣ BEZ Builder (antypattern: Telescoping Constructor)\n");

        System.out.println("""
            Wyobraź sobie konstruktor:
            
            new Ship(name, type, cannons, crewCapacity, hasSails, 
                     hasJollyRoger, captainName, cargoCapacity, homePort)
            
            ❌ Który parametr jest który?
            ❌ Co jeśli chcę tylko name i cannons?
            ❌ Muszę pamiętać kolejność!
            
            new Ship("Black Pearl", "Galleon", 32, 100, true, true, 
                     "Jack Sparrow", 500, "Tortuga")
            
            """);
    }

    /**
     * Z Builder - czytelne, elastyczne, bezpieczne.
     */
    private static void demonstrateWithBuilder() {
        System.out.println("2️⃣ Z Builder (czytelne i elastyczne!)\n");

        // Pełna konfiguracja
        PirateShip blackPearl = new PirateShip.Builder("Black Pearl")
                .type("Galleon")
                .cannons(32)
                .crewCapacity(100)
                .captain("Jack Sparrow")
                .withJollyRoger()
                .homePort("Tortuga")
                .cargoCapacity(500)
                .build();

        System.out.println("Kompletny statek:");
        System.out.println(blackPearl);

        // Minimalna konfiguracja - tylko wymagane parametry
        PirateShip simpleShip = new PirateShip.Builder("Little Boat")
                .build();

        System.out.println("Prosty statek (tylko nazwa):");
        System.out.println(simpleShip);

        // Częściowa konfiguracja
        PirateShip scoutShip = new PirateShip.Builder("Swift Scout")
                .type("Sloop")
                .cannons(8)
                .crewCapacity(25)
                .build();

        System.out.println("Statek zwiadowczy (niektóre parametry):");
        System.out.println(scoutShip);

        System.out.println("""
            ✅ Każdy parametr jest nazwany - wiadomo co ustawiamy
            ✅ Można pominąć opcjonalne parametry
            ✅ Kolejność nie ma znaczenia
            ✅ Obiekt jest immutable (niezmienny)
            ✅ Można dodać walidację w build()
            """);
    }
}

