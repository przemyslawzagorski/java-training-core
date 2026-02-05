package pl.przemekzagorski.training.patterns.decorator;

/**
 * Podstawowy statek piracki - bez ulepszeń.
 */
public class BasicShip implements Ship {

    private final String name;

    public BasicShip(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return "Podstawowy statek piracki";
    }

    @Override
    public int getCost() {
        return 1000; // 1000 złotych dukatów
    }

    @Override
    public int getFirepower() {
        return 10; // podstawowa siła ognia
    }
}
