package pl.przemekzagorski.training.patterns.strategy;

/**
 * Interfejs strategii ataku.
 *
 * Wzorzec STRATEGY pozwala wymieniać algorytmy w runtime.
 */
@FunctionalInterface
public interface AttackStrategy {

    /**
     * Wykonuje atak na cel.
     *
     * @param attackerName nazwa atakującego
     * @param targetName nazwa celu
     */
    void attack(String attackerName, String targetName);
}

