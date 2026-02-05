package pl.przemekzagorski.training.patterns.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Subject - Kapitan ogłasza wydarzenia, załoga słucha.
 *
 * ╔═══════════════════════════════════════════════════════════════╗
 * ║  OBSERVER PATTERN - DLACZEGO TO WAŻNE?                        ║
 * ╠═══════════════════════════════════════════════════════════════╣
 * ║  Observer pozwala na LUŹNE POWIĄZANIE między obiektami.       ║
 * ║                                                               ║
 * ║  Kapitan NIE musi wiedzieć:                                   ║
 * ║    - ILU jest członków załogi                                 ║
 * ║    - JAK reagują na wydarzenia                                ║
 * ║    - KIEDY ktoś dołącza/odchodzi                              ║
 * ║                                                               ║
 * ║  Kapitan TYLKO:                                               ║
 * ║    - Ogłasza wydarzenie (notifyAll)                           ║
 * ║    - Pozwala się zarejestrować/wyrejestrować                  ║
 * ╚═══════════════════════════════════════════════════════════════╝
 */
public class Captain {

    private final String name;
    private final List<CrewObserver> observers = new ArrayList<>();

    public Captain(String name) {
        this.name = name;
    }

    /**
     * Dodaje obserwatora (członka załogi).
     */
    public void addObserver(CrewObserver observer) {
        observers.add(observer);
        System.out.println("   [+] Nowy członek załogi dołączył");
    }

    /**
     * Usuwa obserwatora.
     */
    public void removeObserver(CrewObserver observer) {
        observers.remove(observer);
        System.out.println("   [-] Członek załogi opuścił statek");
    }

    /**
     * Powiadamia WSZYSTKICH obserwatorów o wydarzeniu.
     */
    public void announce(String eventType, String message) {
        System.out.println("\n📢 Kapitan " + name + " ogłasza: " + message);
        System.out.println("   Powiadamiam " + observers.size() + " członków załogi...\n");

        for (CrewObserver observer : observers) {
            observer.onEvent(eventType, message);
        }
    }

    // Wygodne metody dla typowych wydarzeń

    public void spotEnemy(String enemyDescription) {
        announce("ENEMY_SPOTTED", "Wróg na horyzoncie! " + enemyDescription);
    }

    public void findTreasure(String treasureDescription) {
        announce("TREASURE_FOUND", "Skarb! " + treasureDescription);
    }

    public void orderRetreat() {
        announce("RETREAT", "Odwrót! Uciekamy!");
    }
}
