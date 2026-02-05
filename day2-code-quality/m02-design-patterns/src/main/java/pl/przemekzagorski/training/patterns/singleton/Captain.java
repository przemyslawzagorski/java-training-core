package pl.przemekzagorski.training.patterns.singleton;

/**
 * Wzorzec SINGLETON - Jeden Kapitan na Statku 🏴‍☠️
 *
 * Problem: Potrzebujemy dokładnie JEDNEJ instancji klasy w całej aplikacji.
 *
 * Przykład piracki: Na każdym statku może być tylko JEDEN kapitan!
 * Nie chcemy przypadkowo stworzyć dwóch kapitanów.
 *
 * Kiedy używać:
 * - Konfiguracja aplikacji
 * - Logger
 * - Connection pool
 * - Cache
 */
public class Captain {

    // Jedyna instancja - przechowywana w zmiennej statycznej
    private static Captain instance;

    private String name;
    private String shipName;

    /**
     * PRYWATNY konstruktor - nikt z zewnątrz nie może wywołać new Captain()!
     */
    private Captain() {
        this.name = "Jack Sparrow";
        this.shipName = "Black Pearl";
        System.out.println("🏴‍☠️ Kapitan " + name + " obejmuje dowodzenie!");
    }

    /**
     * Publiczna metoda do pobierania jedynej instancji.
     *
     * UWAGA: Ta wersja NIE jest thread-safe!
     * W wielowątkowej aplikacji użyj synchronized lub enum.
     */
    public static Captain getInstance() {
        if (instance == null) {
            instance = new Captain();
        }
        return instance;
    }

    public void giveOrder(String order) {
        System.out.println("⚓ Kapitan " + name + " rozkazuje: " + order);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShipName() {
        return shipName;
    }
}

