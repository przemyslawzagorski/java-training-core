package pl.przemekzagorski.training.patterns.factory;

/**
 * Wzorzec FACTORY METHOD - Stocznia Piracka 🏭
 *
 * Problem: Chcemy tworzyć różne typy obiektów bez określania konkretnej klasy.
 *          Kod klienta nie powinien znać szczegółów implementacji.
 *
 * Przykład piracki: Stocznia produkuje różne typy statków.
 *                   Klient mówi tylko "chcę galeon" - stocznia wie jak go zbudować.
 *
 * Kiedy używać:
 * - Gdy masz rodzinę powiązanych klas
 * - Gdy chcesz ukryć logikę tworzenia
 * - Gdy wybór klasy zależy od parametrów
 */
public class ShipFactory {

    /**
     * Enum typów statków - bezpieczniejszy niż String.
     */
    public enum ShipType {
        GALLEON, FRIGATE, SLOOP
    }

    /**
     * Metoda fabrykująca - tworzy statek odpowiedniego typu.
     *
     * @param type typ statku
     * @param name nazwa statku
     * @return nowy statek
     */
    public static Ship createShip(ShipType type, String name) {
        return switch (type) {
            case GALLEON -> new Galleon(name);
            case FRIGATE -> new Frigate(name);
            case SLOOP -> new Sloop(name);
        };
    }

    /**
     * Alternatywna wersja z String (mniej bezpieczna, ale częstsza w praktyce).
     */
    public static Ship createShip(String type, String name) {
        return switch (type.toLowerCase()) {
            case "galleon" -> new Galleon(name);
            case "frigate" -> new Frigate(name);
            case "sloop" -> new Sloop(name);
            default -> throw new IllegalArgumentException("Nieznany typ statku: " + type);
        };
    }

    /**
     * Factory może też mieć bardziej semantyczne metody.
     */
    public static Ship createBattleship(String name) {
        System.out.println("🏭 Stocznia buduje bojowy galeon...");
        return new Galleon(name);
    }

    public static Ship createScoutShip(String name) {
        System.out.println("🏭 Stocznia buduje szybki slup zwiadowczy...");
        return new Sloop(name);
    }

    public static Ship createTradeShip(String name) {
        System.out.println("🏭 Stocznia buduje uniwersalną fregatę...");
        return new Frigate(name);
    }
}

