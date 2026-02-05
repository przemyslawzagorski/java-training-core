package pl.przemekzagorski.training.patterns.decorator;

/**
 * Dekorator: Szybsze żagle.
 * Statek jest szybszy (w opisie).
 */
public class FastSails extends ShipDecorator {

    public FastSails(Ship ship) {
        super(ship);
    }

    @Override
    public String getDescription() {
        return decoratedShip.getDescription() + " + Szybkie żagle";
    }

    @Override
    public int getCost() {
        return decoratedShip.getCost() + 300; // +300 dukatów
    }

    // Siła ognia bez zmian
}
