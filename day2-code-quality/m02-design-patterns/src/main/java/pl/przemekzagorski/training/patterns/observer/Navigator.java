package pl.przemekzagorski.training.patterns.observer;

/**
 * Konkretny obserwator: Nawigator reaguje na wydarzenia.
 */
public class Navigator implements CrewObserver {

    private final String name;

    public Navigator(String name) {
        this.name = name;
    }

    @Override
    public void onEvent(String eventType, String message) {
        switch (eventType) {
            case "ENEMY_SPOTTED" -> System.out.println("   🗺️ Nawigator " + name + ": Szukam drogi ucieczki!");
            case "TREASURE_FOUND" -> System.out.println("   🗺️ Nawigator " + name + ": Zaznaczam na mapie!");
            case "RETREAT" -> System.out.println("   🗺️ Nawigator " + name + ": Kurs na najbliższy port!");
            default -> System.out.println("   🗺️ Nawigator " + name + ": Notuję w dzienniku.");
        }
    }
}
