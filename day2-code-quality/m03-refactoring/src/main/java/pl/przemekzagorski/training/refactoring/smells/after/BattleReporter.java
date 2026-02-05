package pl.przemekzagorski.training.refactoring.smells.after;

/**
 * ✅ Interfejs reportera - można podmienić implementację
 */
public interface BattleReporter {
    void report(BattleResult result);
}

