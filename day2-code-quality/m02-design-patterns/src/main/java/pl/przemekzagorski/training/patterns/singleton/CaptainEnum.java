package pl.przemekzagorski.training.patterns.singleton;

/**
 * Singleton - wersja THREAD-SAFE przy użyciu enum.
 *
 * Jest to ZALECANA implementacja Singletona w Javie!
 * - Thread-safe "za darmo"
 * - Odporna na serializację
 * - Odporna na refleksję
 */
public enum CaptainEnum {

    INSTANCE;  // Jedyna instancja

    private String name = "Jack Sparrow";
    private String shipName = "Black Pearl";

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

