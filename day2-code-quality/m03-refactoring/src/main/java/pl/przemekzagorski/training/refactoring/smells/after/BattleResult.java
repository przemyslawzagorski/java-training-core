package pl.przemekzagorski.training.refactoring.smells.after;

import java.math.BigDecimal;

/**
 * ✅ Wynik bitwy - Value Object
 */
public record BattleResult(
    Ship winner,
    Ship loser,
    BigDecimal loot,
    boolean isDraw
) {
    public static BattleResult draw() {
        return new BattleResult(null, null, BigDecimal.ZERO, true);
    }

    public static BattleResult victory(Ship winner, Ship loser, BigDecimal loot) {
        return new BattleResult(winner, loser, loot, false);
    }
}

