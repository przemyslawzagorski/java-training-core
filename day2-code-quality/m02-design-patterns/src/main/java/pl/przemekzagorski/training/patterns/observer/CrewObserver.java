package pl.przemekzagorski.training.patterns.observer;

/**
 * Observer - interfejs dla obserwatorów wydarzeń na morzu.
 */
public interface CrewObserver {

    /**
     * Wywoływane gdy kapitan ogłasza wydarzenie.
     * @param eventType typ wydarzenia (np. "ENEMY_SPOTTED", "TREASURE_FOUND")
     * @param message szczegóły wydarzenia
     */
    void onEvent(String eventType, String message);
}
