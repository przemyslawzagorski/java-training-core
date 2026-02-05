package pl.przemekzagorski.training.patterns.decorator;

/**
 * Dekorator: Dodatkowe armaty.
 * Zwiększa siłę ognia za dodatkową cenę.
 */
public class CannonUpgrade extends ShipDecorator {

    public CannonUpgrade(Ship ship) {
        super(ship);
    }

    @Override
    public String getDescription() {
        return decoratedShip.getDescription() + " + Dodatkowe armaty";
    }

    @Override
    public int getCost() {
        return decoratedShip.getCost() + 500; // +500 dukatów
    }

    @Override
    public int getFirepower() {
        return decoratedShip.getFirepower() + 20; // +20 do siły ognia
    }
}
