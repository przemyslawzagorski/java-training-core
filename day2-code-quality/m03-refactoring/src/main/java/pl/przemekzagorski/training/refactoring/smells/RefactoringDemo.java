package pl.przemekzagorski.training.refactoring.smells;

import pl.przemekzagorski.training.refactoring.smells.after.*;

/**
 * Demonstracja refaktoringu - porównanie "przed" i "po".
 */
public class RefactoringDemo {

    public static void main(String[] args) {
        System.out.println("🏴‍☠️ Refactoring Demo");
        System.out.println("======================\n");

        demonstrateBadCode();
        demonstrateGoodCode();
    }

    private static void demonstrateBadCode() {
        System.out.println("1️⃣ ZŁY KOD (BadPirateService)\n");

        System.out.println("""
            Problemy w BadPirateService:
            
            ❌ Long Method - processBattle() ma ~80 linii
            ❌ God Class - jedna klasa robi wszystko
            ❌ Magic Numbers - 0.1, 100, 500, 50, 20...
            ❌ Duplicate Code - obliczanie obrażeń skopiowane
            ❌ Feature Envy - logika obrażeń powinna być w Ship
            ❌ Primitive Obsession - parametry zamiast obiektów
            ❌ Brak testów - trudno przetestować
            
            Zobacz: BadPirateService.java
            """);
    }

    private static void demonstrateGoodCode() {
        System.out.println("2️⃣ DOBRY KOD (po refaktoringu)\n");

        // Tworzymy zależności
        LootCalculator lootCalculator = new LootCalculator();
        BattleReporter reporter = new ConsoleBattleReporter();
        BattleRepository repository = new InMemoryBattleRepository();

        // Wstrzykujemy przez konstruktor (Dependency Injection)
        GoodBattleService battleService = new GoodBattleService(
            lootCalculator, reporter, repository
        );

        // Tworzymy statki
        Ship blackPearl = new Ship("Black Pearl", 32, 100, 500);
        Ship interceptor = new Ship("Interceptor", 16, 40, 300);

        System.out.println("Bitwa: " + blackPearl.getName() + " vs " + interceptor.getName());

        // Walka!
        BattleResult result = battleService.processBattle(blackPearl, interceptor);

        System.out.println("""
            
            ✅ Refaktoring wykonany:
            
            • Ship - enkapsulacja danych i logiki obrażeń
            • LootCalculator - Single Responsibility
            • BattleResult - Value Object (record)
            • BattleReporter/Repository - interfejsy (Dependency Inversion)
            • GoodBattleService - krótkie metody, delegacja
            • Dependency Injection - łatwe testowanie
            • Stałe zamiast magic numbers
            • Brak duplikacji kodu
            """);
    }
}

