package pl.przemekzagorski.training.patterns.factory;

/**
 * Fregata - średni statek, dobra równowaga między szybkością a siłą ognia.
 */
public class Frigate implements Ship {

    private String name;

    public Frigate(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void sail() {
        System.out.println("⛵ Fregata " + name + " płynie ze średnią prędkością");
    }

    @Override
    public void attack() {
        System.out.println("💣 Fregata " + name + " strzela z 20 armat!");
    }

    @Override
    public int getCannons() {
        return 20;
    }

    @Override
    public int getCrewCapacity() {
        return 60;
    }

    @Override
    public String toString() {
        return "Frigate{name='" + name + "', cannons=20, crew=60}";
    }
}

