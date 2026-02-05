package pl.przemekzagorski.training.patterns.decorator;

/**
 * Demonstracja wzorca Decorator.
 *
 * ╔═══════════════════════════════════════════════════════════════════╗
 * ║                      DECORATOR PATTERN                           ║
 * ╠═══════════════════════════════════════════════════════════════════╣
 * ║  PROBLEM:                                                        ║
 * ║    Chcesz dodawać funkcje do obiektu dynamicznie,                ║
 * ║    bez tworzenia miliona podklas.                                ║
 * ║                                                                  ║
 * ║  ROZWIĄZANIE:                                                    ║
 * ║    Decorator "owija" obiekt i dodaje/modyfikuje zachowanie.      ║
 * ║                                                                  ║
 * ║  PRZYKŁAD Z JAVY:                                                ║
 * ║    BufferedReader → InputStreamReader → FileInputStream          ║
 * ║    (każdy "dekoruje" poprzedni!)                                 ║
 * ╚═══════════════════════════════════════════════════════════════════╝
 */
public class DecoratorDemo {

    public static void main(String[] args) {
        System.out.println("🏴‍☠️ DECORATOR PATTERN - Budowa statku pirackiego");
        System.out.println("=".repeat(55) + "\n");

        // 1️⃣ Podstawowy statek
        Ship basicShip = new BasicShip("Black Pearl");
        printShipInfo("1️⃣ Podstawowy statek:", basicShip);

        // 2️⃣ Dodajemy armaty
        Ship armedShip = new CannonUpgrade(basicShip);
        printShipInfo("2️⃣ Z armatami:", armedShip);

        // 3️⃣ Dodajemy pancerz
        Ship armoredShip = new ArmorPlating(armedShip);
        printShipInfo("3️⃣ Z armatami i pancerzem:", armoredShip);

        // 4️⃣ Dodajemy szybkie żagle
        Ship fullyUpgraded = new FastSails(armoredShip);
        printShipInfo("4️⃣ Pełne ulepszenia:", fullyUpgraded);

        System.out.println("=".repeat(55));
        System.out.println("✅ KORZYŚCI DECORATOR:");
        System.out.println("   - Możesz kombinować ulepszenia DOWOLNIE");
        System.out.println("   - Nie musisz tworzyć klasy dla każdej kombinacji");
        System.out.println("   - Możesz dodać nowe ulepszenie bez zmiany istniejących\n");

        System.out.println("🎯 GDZIE ZNAJDZIESZ DECORATOR W JAVIE:");
        System.out.println("   - java.io: BufferedReader, InputStreamReader...");
        System.out.println("   - Collections.unmodifiableList()");
        System.out.println("   - Collections.synchronizedList()");
    }

    private static void printShipInfo(String title, Ship ship) {
        System.out.println(title);
        System.out.println("   Nazwa: " + ship.getName());
        System.out.println("   Opis:  " + ship.getDescription());
        System.out.println("   Koszt: " + ship.getCost() + " dukatów");
        System.out.println("   Siła ognia: " + ship.getFirepower());
        System.out.println();
    }
}
