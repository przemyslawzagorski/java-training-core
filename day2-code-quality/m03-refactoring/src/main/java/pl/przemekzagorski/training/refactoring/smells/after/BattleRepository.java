package pl.przemekzagorski.training.refactoring.smells.after;

/**
 * ✅ Interfejs repozytorium - można podmienić implementację (np. na mock w testach)
 */
public interface BattleRepository {
    void save(BattleResult result);
}

