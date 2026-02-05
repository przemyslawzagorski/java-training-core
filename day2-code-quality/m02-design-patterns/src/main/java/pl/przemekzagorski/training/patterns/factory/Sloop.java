package pl.przemekzagorski.training.patterns.factory;

/**
 * Slup - mały, szybki statek. Idealny do zwiadów i ucieczek.
 */
public class Sloop implements Ship {

    private String name;

    public Sloop(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void sail() {
        System.out.println("⛵ Slup " + name + " śmiga po falach z dużą prędkością!");
    }

    @Override
    public void attack() {
        System.out.println("💣 Slup " + name + " strzela z 8 małych armat");
    }

    @Override
    public int getCannons() {
        return 8;
    }

    @Override
    public int getCrewCapacity() {
        return 25;
    }

    @Override
    public String toString() {
        return "Sloop{name='" + name + "', cannons=8, crew=25}";
    }
}

