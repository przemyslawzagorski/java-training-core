package pl.przemekzagorski.training.patterns.strategy;

/**
 * Strategia ataku armatami - z daleka.
 */
public class CannonAttack implements AttackStrategy {

    @Override
    public void attack(String attackerName, String targetName) {
        System.out.println("💣 " + attackerName + " atakuje " + targetName + " salwą armatnią!");
        System.out.println("   BOOM! BOOM! BOOM!");
        System.out.println("   Dystans: daleki, Obrażenia: wysokie");
    }
}

