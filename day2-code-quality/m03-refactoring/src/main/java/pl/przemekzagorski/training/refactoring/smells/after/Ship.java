package pl.przemekzagorski.training.refactoring.smells.after;

import java.math.BigDecimal;

/**
 * ✅ Model statku - odpowiedzialność: przechowywanie danych statku
 */
public class Ship {

    private static final int LARGE_CREW_THRESHOLD = 50;
    private static final int HUGE_CREW_THRESHOLD = 100;
    private static final int DAMAGE_PER_CANNON = 10;
    private static final int LARGE_CREW_BONUS = 20;
    private static final int HUGE_CREW_BONUS = 30;

    private final String name;
    private final int cannons;
    private final int crew;
    private int health;

    public Ship(String name, int cannons, int crew, int health) {
        this.name = name;
        this.cannons = cannons;
        this.crew = crew;
        this.health = health;
    }

    /**
     * Oblicza obrażenia zadawane przez ten statek.
     * Logika w jednym miejscu - nie rozrzucona po kodzie!
     */
    public int calculateDamage() {
        int damage = cannons * DAMAGE_PER_CANNON;

        if (crew > HUGE_CREW_THRESHOLD) {
            damage += LARGE_CREW_BONUS + HUGE_CREW_BONUS;
        } else if (crew > LARGE_CREW_THRESHOLD) {
            damage += LARGE_CREW_BONUS;
        }

        return damage;
    }

    /**
     * Przyjmuje obrażenia.
     */
    public void takeDamage(int damage) {
        this.health -= damage;
    }

    public boolean isDestroyed() {
        return health <= 0;
    }

    public boolean isStrongerThan(Ship other) {
        return this.health > other.health;
    }

    // Gettery
    public String getName() { return name; }
    public int getCannons() { return cannons; }
    public int getCrew() { return crew; }
    public int getHealth() { return health; }
}

