package pl.przemekzagorski.training.refactoring.smells.after;

/**
 * ✅ Prosta implementacja reportera - wypisuje na konsolę
 */
public class ConsoleBattleReporter implements BattleReporter {

    @Override
    public void report(BattleResult result) {
        System.out.println("\n=== RAPORT BITWY ===");

        if (result.isDraw()) {
            System.out.println("REMIS - Oba statki zatonęły!");
        } else {
            System.out.println("ZWYCIĘZCA: " + result.winner().getName());
            System.out.println("PRZEGRANY: " + result.loser().getName());
            System.out.println("ŁUPY: " + result.loot() + " złota");
        }
    }
}

