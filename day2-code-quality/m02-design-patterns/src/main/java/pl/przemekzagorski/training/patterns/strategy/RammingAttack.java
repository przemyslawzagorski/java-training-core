package pl.przemekzagorski.training.patterns.strategy;

/**
 * Strategia taranowania - desperacki atak.
 */
public class RammingAttack implements AttackStrategy {

    @Override
    public void attack(String attackerName, String targetName) {
        System.out.println("🚢💥 " + attackerName + " taranuje " + targetName + "!");
        System.out.println("   TRZASK! Drewno pęka!");
        System.out.println("   Dystans: zero, Ryzyko: MAKSYMALNE, Desperacja: TAK!");
    }
}

