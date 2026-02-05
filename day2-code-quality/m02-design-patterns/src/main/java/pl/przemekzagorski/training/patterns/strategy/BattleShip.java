package pl.przemekzagorski.training.patterns.strategy;

/**
 * Statek piracki używający wzorca Strategy.
 *
 * Strategia ataku może być zmieniana w runtime!
 */
public class BattleShip {

    private String name;
    private AttackStrategy attackStrategy;

    public BattleShip(String name) {
        this.name = name;
        this.attackStrategy = new CannonAttack();  // Domyślna strategia
    }

    /**
     * Zmiana strategii w runtime - serce wzorca Strategy!
     */
    public void setAttackStrategy(AttackStrategy strategy) {
        this.attackStrategy = strategy;
        System.out.println("⚙️ " + name + " zmienia taktykę ataku!");
    }

    /**
     * Wykonuje atak używając aktualnej strategii.
     */
    public void attack(String target) {
        if (attackStrategy == null) {
            System.out.println(name + " nie ma ustawionej strategii ataku!");
            return;
        }
        attackStrategy.attack(name, target);
    }

    public String getName() {
        return name;
    }
}

