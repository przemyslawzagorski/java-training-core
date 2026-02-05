package pl.przemekzagorski.training.patterns.factory;

/**
 * Galeon - duży, ciężki statek z wieloma armatami.
 * Wolny ale potężny w walce.
 */
public class Galleon implements Ship {

    private String name;

    public Galleon(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void sail() {
        System.out.println("⛵ Galeon " + name + " płynie powoli ale majestatycznie");
    }

    @Override
    public void attack() {
        System.out.println("💣 Galeon " + name + " odpala salwę z 32 armat!");
    }

    @Override
    public int getCannons() {
        return 32;
    }

    @Override
    public int getCrewCapacity() {
        return 100;
    }

    @Override
    public String toString() {
        return "Galleon{name='" + name + "', cannons=32, crew=100}";
    }
}

