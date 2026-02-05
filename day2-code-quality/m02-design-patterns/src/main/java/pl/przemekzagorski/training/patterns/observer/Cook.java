package pl.przemekzagorski.training.patterns.observer;

/**
 * Konkretny obserwator: Kucharz reaguje na wydarzenia.
 */
public class Cook implements CrewObserver {

    private final String name;

    public Cook(String name) {
        this.name = name;
    }

    @Override
    public void onEvent(String eventType, String message) {
        switch (eventType) {
            case "ENEMY_SPOTTED" -> System.out.println("   🍳 Kucharz " + name + ": Chowam garnki, chwytam tasak!");
            case "TREASURE_FOUND" -> System.out.println("   🍳 Kucharz " + name + ": Czas na ucztę!");
            case "RETREAT" -> System.out.println("   🍳 Kucharz " + name + ": Pakuję prowiant!");
            default -> System.out.println("   🍳 Kucharz " + name + ": A co na obiad?");
        }
    }
}
