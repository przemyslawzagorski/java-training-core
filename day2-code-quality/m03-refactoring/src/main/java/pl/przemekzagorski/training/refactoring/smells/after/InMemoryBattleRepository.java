package pl.przemekzagorski.training.refactoring.smells.after;

/**
 * ✅ Prosta implementacja repozytorium - symuluje zapis
 */
public class InMemoryBattleRepository implements BattleRepository {

    @Override
    public void save(BattleResult result) {
        if (result.isDraw()) {
            System.out.println("💾 Zapisano remis do bazy");
        } else {
            System.out.println("💾 Zapisano zwycięstwo " + result.winner().getName() + " do bazy");
        }
    }
}

