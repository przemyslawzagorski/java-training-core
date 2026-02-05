package pl.przemekzagorski.training.patterns.strategy;

/**
 * Strategia abordażu - wejście na pokład wroga.
 */
public class BoardingAttack implements AttackStrategy {

    @Override
    public void attack(String attackerName, String targetName) {
        System.out.println("⚔️ " + attackerName + " idzie na abordaż " + targetName + "!");
        System.out.println("   Piraci przeskakują na wrogie pokład!");
        System.out.println("   Dystans: bliski, Ryzyko: wysokie, Łupy: maksymalne!");
    }
}

