package pl.przemekzagorski.training.patterns.observer;

/**
 * Konkretny obserwator: Kanonierzy reagują na wydarzenia.
 */
public class Gunner implements CrewObserver {

    private final String name;

    public Gunner(String name) {
        this.name = name;
    }

    @Override
    public void onEvent(String eventType, String message) {
        switch (eventType) {
            case "ENEMY_SPOTTED" -> System.out.println("   💣 Kanonier " + name + ": Ładuję armaty!");
            case "TREASURE_FOUND" -> System.out.println("   💣 Kanonier " + name + ": Super, ale ja pilnuję armat.");
            case "RETREAT" -> System.out.println("   💣 Kanonier " + name + ": Ogień osłonowy!");
            default -> System.out.println("   💣 Kanonier " + name + ": OK, zrozumiałem.");
        }
    }
}
