package pl.przemekzagorski.training.patterns.factory;

/**
 * Interfejs statku - wszystkie typy statków implementują ten interfejs.
 */
public interface Ship {

    String getName();
    void sail();
    void attack();
    int getCannons();
    int getCrewCapacity();
}

