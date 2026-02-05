package pl.przemekzagorski.training.patterns.decorator;

/**
 * Dekorator: Wzmocniony kadłub.
 * Nie zmienia siły ognia, ale zwiększa przetrwalność (w opisie).
 */
public class ArmorPlating extends ShipDecorator {

    public ArmorPlating(Ship ship) {
        super(ship);
    }

    @Override
    public String getDescription() {
        return decoratedShip.getDescription() + " + Wzmocniony kadłub";
    }

    @Override
    public int getCost() {
        return decoratedShip.getCost() + 800; // +800 dukatów
    }

    // Siła ognia bez zmian - pancerz nie strzela!
}
