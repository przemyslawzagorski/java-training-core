package pl.przemekzagorski.training.patterns.decorator;

/**
 * Abstrakcyjny dekorator - bazowa klasa dla wszystkich ulepszeń.
 *
 * ╔═══════════════════════════════════════════════════════════════╗
 * ║  DECORATOR PATTERN - DLACZEGO TO WAŻNE?                       ║
 * ╠═══════════════════════════════════════════════════════════════╣
 * ║  Decorator pozwala DYNAMICZNIE dodawać funkcjonalność          ║
 * ║  bez zmiany oryginalnej klasy!                                 ║
 * ║                                                               ║
 * ║  Alternatywa (ZŁA): dziedziczenie                             ║
 * ║    - ArmoredShip extends Ship                                  ║
 * ║    - FastShip extends Ship                                     ║
 * ║    - ArmoredFastShip extends ??? ← problem!                   ║
 * ║                                                               ║
 * ║  Decorator (DOBRA):                                           ║
 * ║    - new ArmoredDecorator(new FastDecorator(basicShip))       ║
 * ║    - Kompozycja > Dziedziczenie!                              ║
 * ╚═══════════════════════════════════════════════════════════════╝
 */
public abstract class ShipDecorator implements Ship {

    protected final Ship decoratedShip;

    public ShipDecorator(Ship ship) {
        this.decoratedShip = ship;
    }

    @Override
    public String getName() {
        return decoratedShip.getName();
    }

    @Override
    public String getDescription() {
        return decoratedShip.getDescription();
    }

    @Override
    public int getCost() {
        return decoratedShip.getCost();
    }

    @Override
    public int getFirepower() {
        return decoratedShip.getFirepower();
    }
}
