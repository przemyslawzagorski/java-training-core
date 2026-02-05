package pl.przemekzagorski.training.refactoring.smells.after;

import java.math.BigDecimal;

/**
 * ✅ DOBRY KOD - po refaktoringu
 *
 * Zmiany:
 * 1. Wydzielona klasa Ship (enkapsulacja danych i logiki obrażeń)
 * 2. Wydzielona klasa LootCalculator (Single Responsibility)
 * 3. Wydzielony BattleResult (Value Object)
 * 4. Usunięte magic numbers (stałe z nazwami)
 * 5. Usunięty duplicate code (Ship.calculateDamage())
 * 6. Krótkie, czytelne metody
 * 7. Dependency Injection (BattleReporter, BattleRepository)
 */
public class GoodBattleService {

    private final LootCalculator lootCalculator;
    private final BattleReporter reporter;
    private final BattleRepository repository;

    // Dependency Injection - zależności przez konstruktor
    public GoodBattleService(LootCalculator lootCalculator,
                             BattleReporter reporter,
                             BattleRepository repository) {
        this.lootCalculator = lootCalculator;
        this.reporter = reporter;
        this.repository = repository;
    }

    /**
     * Główna metoda - czytelna, krótka, deleguje do wyspecjalizowanych klas.
     */
    public BattleResult processBattle(Ship ship1, Ship ship2) {
        executeCombat(ship1, ship2);
        BattleResult result = determineWinner(ship1, ship2);

        reporter.report(result);
        repository.save(result);

        return result;
    }

    private void executeCombat(Ship ship1, Ship ship2) {
        int damage1 = ship1.calculateDamage();
        int damage2 = ship2.calculateDamage();

        ship1.takeDamage(damage2);
        ship2.takeDamage(damage1);
    }

    private BattleResult determineWinner(Ship ship1, Ship ship2) {
        if (ship1.isDestroyed() && ship2.isDestroyed()) {
            return BattleResult.draw();
        }

        Ship winner = determineStronger(ship1, ship2);
        Ship loser = (winner == ship1) ? ship2 : ship1;
        BigDecimal loot = lootCalculator.calculate(winner, loser);

        return BattleResult.victory(winner, loser, loot);
    }

    private Ship determineStronger(Ship ship1, Ship ship2) {
        if (ship1.isDestroyed()) return ship2;
        if (ship2.isDestroyed()) return ship1;
        return ship1.isStrongerThan(ship2) ? ship1 : ship2;
    }
}

