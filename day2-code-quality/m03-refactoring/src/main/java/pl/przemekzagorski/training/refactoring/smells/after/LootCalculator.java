package pl.przemekzagorski.training.refactoring.smells.after;

import java.math.BigDecimal;

/**
 * ✅ Kalkulator łupów - jedna odpowiedzialność: obliczanie łupów
 */
public class LootCalculator {

    private static final BigDecimal LOOT_PER_CREW = new BigDecimal("10");  // 100 * 0.1
    private static final BigDecimal LARGE_SHIP_BONUS = new BigDecimal("500");
    private static final int LARGE_SHIP_CANNON_THRESHOLD = 20;

    public BigDecimal calculate(Ship winner, Ship loser) {
        BigDecimal baseLoot = LOOT_PER_CREW.multiply(BigDecimal.valueOf(loser.getCrew()));

        if (loser.getCannons() > LARGE_SHIP_CANNON_THRESHOLD) {
            baseLoot = baseLoot.add(LARGE_SHIP_BONUS);
        }

        return baseLoot;
    }
}

