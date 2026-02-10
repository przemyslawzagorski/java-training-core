package pl.przemekzagorski.training.refactoring;

import java.util.ArrayList;
import java.util.List;

/**
 * ╔═══════════════════════════════════════════════════════════════════╗
 * ║         ROZWIĄZANIA - REFACTORING & SOLID                        ║
 * ╠═══════════════════════════════════════════════════════════════════╣
 * ║  Porównaj swoje rozwiązania z tymi poniżej!                      ║
 * ╚═══════════════════════════════════════════════════════════════════╝
 */
public class RefactoringExercisesSolutions {

    /**
     * ════════════════════════════════════════════════════════════════
     * ROZWIĄZANIE 1: Extract Method
     * ════════════════════════════════════════════════════════════════
     *
     * PRZED: jedna metoda 50 linii
     * PO: 4 metody po 10-15 linii
     */

    // Stałe zamiast magic numbers (ćwiczenie 3)
    private static final int DAMAGE_PER_CANNON = 10;
    private static final int MEDIUM_CREW_THRESHOLD = 50;
    private static final int LARGE_CREW_THRESHOLD = 100;
    private static final int MEDIUM_CREW_BONUS = 20;
    private static final int LARGE_CREW_BONUS = 30;

    // WYODRĘBNIONA METODA 1: calculateDamage
    public int calculateDamage(int cannons, int crew) {
        int damage = cannons * DAMAGE_PER_CANNON;

        if (crew > LARGE_CREW_THRESHOLD) {
            damage += LARGE_CREW_BONUS;
        } else if (crew > MEDIUM_CREW_THRESHOLD) {
            damage += MEDIUM_CREW_BONUS;
        }

        return damage;
    }

    // WYODRĘBNIONA METODA 2: determineWinner
    public String determineWinner(String ship1Name, int health1After,
                                  String ship2Name, int health2After) {
        if (health1After <= 0 && health2After <= 0) {
            return "DRAW - both ships sunk!";
        } else if (health1After <= 0) {
            return ship2Name + " WINS!";
        } else if (health2After <= 0) {
            return ship1Name + " WINS!";
        } else {
            return health1After > health2After ? ship1Name : ship2Name;
        }
    }

    // WYODRĘBNIONA METODA 3: printReport
    public void printBattleReport(String ship1, int health1Before, int health1After,
                                  String ship2, int health2Before, int health2After,
                                  String winner) {
        System.out.println("═══════════════════════════════════════");
        System.out.println("           ⚔️ BATTLE REPORT ⚔️");
        System.out.println("═══════════════════════════════════════");
        System.out.printf("%-15s: %d → %d HP%n", ship1, health1Before, health1After);
        System.out.printf("%-15s: %d → %d HP%n", ship2, health2Before, health2After);
        System.out.println("───────────────────────────────────────");
        System.out.println("WINNER: " + winner);
        System.out.println("═══════════════════════════════════════");
    }

    // GŁÓWNA METODA - czysta i czytelna!
    public void processBattle(String ship1Name, int cannons1, int crew1, int health1,
                              String ship2Name, int cannons2, int crew2, int health2) {
        // 1. Oblicz obrażenia
        int damage1 = calculateDamage(cannons1, crew1);
        int damage2 = calculateDamage(cannons2, crew2);

        // 2. Zastosuj obrażenia
        int health1After = health1 - damage2;
        int health2After = health2 - damage1;

        // 3. Określ zwycięzcę
        String winner = determineWinner(ship1Name, health1After, ship2Name, health2After);

        // 4. Wyświetl raport
        printBattleReport(ship1Name, health1, health1After,
                ship2Name, health2, health2After, winner);
    }

    public void solution1_extractMethod() {
        System.out.println("=== ROZWIĄZANIE 1: Extract Method ===\n");

        // Test wyodrębnionych metod
        int damage = calculateDamage(10, 120);
        System.out.println("calculateDamage(10 cannons, 120 crew) = " + damage);
        System.out.println("  → 10 * 10 + 30 (large crew bonus) = 130 ✅\n");

        String winner = determineWinner("Black Pearl", 50, "Flying Dutchman", 0);
        System.out.println("determineWinner(BP: 50 HP, FD: 0 HP) = " + winner + "\n");

        // Pełna bitwa
        processBattle("Black Pearl", 15, 120, 200,
                "Flying Dutchman", 20, 80, 180);
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * ROZWIĄZANIE 2: Extract Class (Single Responsibility)
     * ════════════════════════════════════════════════════════════════
     */

    // Encja statku (immutable record)
    public record Ship(String name, int cannons, int crew, int health) {
        public Ship withHealth(int newHealth) {
            return new Ship(name, cannons, crew, newHealth);
        }
    }

    // Wynik bitwy (value object)
    public record BattleResult(Ship ship1, Ship ship2, int damage1, int damage2, String winner) {}

    // Osobna klasa: obliczanie obrażeń
    public static class DamageCalculator {
        public int calculate(Ship ship) {
            int damage = ship.cannons() * DAMAGE_PER_CANNON;
            if (ship.crew() > LARGE_CREW_THRESHOLD) {
                damage += LARGE_CREW_BONUS;
            } else if (ship.crew() > MEDIUM_CREW_THRESHOLD) {
                damage += MEDIUM_CREW_BONUS;
            }
            return damage;
        }
    }

    // Osobna klasa: raportowanie
    public static class BattleReporter {
        public void printReport(BattleResult result) {
            System.out.println("\n📋 Battle Report:");
            System.out.println("  " + result.ship1().name() + " dealt " + result.damage1() + " damage");
            System.out.println("  " + result.ship2().name() + " dealt " + result.damage2() + " damage");
            System.out.println("  🏆 Winner: " + result.winner());
        }
    }

    // Interfejsy (ćwiczenie 4 - Dependency Inversion)
    public interface BattleRepository {
        void save(BattleResult result);
    }

    public interface NotificationService {
        void notifyWinner(String winner);
    }

    // Serwis z wstrzykiwanymi zależnościami
    public static class BattleService {
        private final DamageCalculator calculator;
        private final BattleReporter reporter;
        private final BattleRepository repository;
        private final NotificationService notifications;

        // Constructor Injection - łatwe testowanie!
        public BattleService(DamageCalculator calculator,
                             BattleReporter reporter,
                             BattleRepository repository,
                             NotificationService notifications) {
            this.calculator = calculator;
            this.reporter = reporter;
            this.repository = repository;
            this.notifications = notifications;
        }

        public BattleResult fight(Ship ship1, Ship ship2) {
            int damage1 = calculator.calculate(ship1);
            int damage2 = calculator.calculate(ship2);

            Ship ship1After = ship1.withHealth(ship1.health() - damage2);
            Ship ship2After = ship2.withHealth(ship2.health() - damage1);

            String winner = determineWinner(ship1After, ship2After);

            BattleResult result = new BattleResult(ship1After, ship2After, damage1, damage2, winner);

            reporter.printReport(result);
            repository.save(result);
            notifications.notifyWinner(winner);

            return result;
        }

        private String determineWinner(Ship ship1, Ship ship2) {
            if (ship1.health() <= 0 && ship2.health() <= 0) return "DRAW";
            if (ship1.health() <= 0) return ship2.name();
            if (ship2.health() <= 0) return ship1.name();
            return ship1.health() > ship2.health() ? ship1.name() : ship2.name();
        }
    }

    // Implementacje (można łatwo zamienić!)
    public static class InMemoryBattleRepository implements BattleRepository {
        private final List<BattleResult> battles = new ArrayList<>();

        @Override
        public void save(BattleResult result) {
            battles.add(result);
            System.out.println("  💾 Saved to in-memory repository");
        }

        public List<BattleResult> findAll() { return new ArrayList<>(battles); }
    }

    public static class ConsoleNotificationService implements NotificationService {
        @Override
        public void notifyWinner(String winner) {
            System.out.println("  📢 Notification: " + winner + " won the battle!");
        }
    }

    public void solution2_extractClass() {
        System.out.println("\n=== ROZWIĄZANIE 2: Extract Class ===\n");

        // Wstrzykiwane zależności
        DamageCalculator calculator = new DamageCalculator();
        BattleReporter reporter = new BattleReporter();
        InMemoryBattleRepository repository = new InMemoryBattleRepository();
        ConsoleNotificationService notifications = new ConsoleNotificationService();

        // Serwis z czystymi zależnościami
        BattleService service = new BattleService(calculator, reporter, repository, notifications);

        // Test
        Ship blackPearl = new Ship("Black Pearl", 15, 120, 200);
        Ship flyingDutchman = new Ship("Flying Dutchman", 20, 80, 180);

        service.fight(blackPearl, flyingDutchman);

        System.out.println("\n✅ Single Responsibility - każda klasa robi JEDNĄ rzecz!");
        System.out.println("✅ Dependency Inversion - łatwe mockowanie w testach!");
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * ROZWIĄZANIE 3: Replace Magic Numbers
     * ════════════════════════════════════════════════════════════════
     */

    // Wariant 1: Stałe w klasie (proste)
    public static class BattleConstants {
        public static final int DAMAGE_PER_CANNON = 10;
        public static final int MEDIUM_CREW_THRESHOLD = 50;
        public static final int LARGE_CREW_THRESHOLD = 100;
        public static final int MEDIUM_CREW_BONUS = 20;
        public static final int LARGE_CREW_BONUS = 30;
        public static final double CRITICAL_HIT_CHANCE = 0.1;
        public static final int MAX_HEALTH = 500;

        private BattleConstants() {} // utility class
    }

    // Wariant 2: Enum z wartościami (bardziej elastyczne)
    public enum CrewSize {
        SMALL(0, 0),
        MEDIUM(50, 20),
        LARGE(100, 30);

        private final int threshold;
        private final int bonus;

        CrewSize(int threshold, int bonus) {
            this.threshold = threshold;
            this.bonus = bonus;
        }

        public static int bonusForCrew(int crewCount) {
            if (crewCount > LARGE.threshold) return LARGE.bonus;
            if (crewCount > MEDIUM.threshold) return MEDIUM.bonus;
            return SMALL.bonus;
        }
    }

    public void solution3_magicNumbers() {
        System.out.println("\n=== ROZWIĄZANIE 3: Magic Numbers ===\n");

        System.out.println("PRZED (źle):");
        System.out.println("  int damage = cannons * 10;");
        System.out.println("  if (crew > 50) damage += 20;");
        System.out.println("  Problem: Co oznaczają 10, 50, 20? 🤔\n");

        System.out.println("PO (dobrze) - Wariant 1: Stałe");
        System.out.println("  int damage = cannons * DAMAGE_PER_CANNON;");
        System.out.println("  if (crew > MEDIUM_CREW_THRESHOLD) damage += MEDIUM_CREW_BONUS;");

        System.out.println("\nPO (lepiej) - Wariant 2: Enum");
        System.out.println("  damage += CrewSize.bonusForCrew(crew);");

        // Demo enum
        System.out.println("\nTest enum CrewSize:");
        System.out.println("  Crew 30: bonus = " + CrewSize.bonusForCrew(30));
        System.out.println("  Crew 75: bonus = " + CrewSize.bonusForCrew(75));
        System.out.println("  Crew 150: bonus = " + CrewSize.bonusForCrew(150));
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * ROZWIĄZANIE 4: Dependency Inversion
     * ════════════════════════════════════════════════════════════════
     */
    public void solution4_dependencyInversion() {
        System.out.println("\n=== ROZWIĄZANIE 4: Dependency Inversion ===\n");

        System.out.println("PRZED (tight coupling):");
        System.out.println("  class BattleService {");
        System.out.println("      void fight() {");
        System.out.println("          MySqlDatabase db = new MySqlDatabase();  // ❌ Konkretna klasa!");
        System.out.println("          db.save(result);");
        System.out.println("      }");
        System.out.println("  }");
        System.out.println("  Problem: Jak przetestować bez prawdziwej bazy? 🤔\n");

        System.out.println("PO (Dependency Inversion):");
        System.out.println("  interface BattleRepository {");
        System.out.println("      void save(BattleResult result);");
        System.out.println("  }");
        System.out.println("");
        System.out.println("  class BattleService {");
        System.out.println("      private final BattleRepository repo;  // ✅ Interfejs!");
        System.out.println("      ");
        System.out.println("      BattleService(BattleRepository repo) {  // Constructor Injection");
        System.out.println("          this.repo = repo;");
        System.out.println("      }");
        System.out.println("  }");

        System.out.println("\nKorzyści:");
        System.out.println("  ✅ Łatwe testowanie (Mock/Stub)");
        System.out.println("  ✅ Łatwa zmiana implementacji (SQL → Mongo)");
        System.out.println("  ✅ Loose coupling");
        System.out.println("  ✅ Zgodne z zasadą D z SOLID!");

        // Demo mockowania
        System.out.println("\nDemo - MockRepository w teście:");

        BattleRepository mockRepo = result -> System.out.println("    [MOCK] Would save: " + result.winner());
        NotificationService mockNotif = winner -> System.out.println("    [MOCK] Would notify: " + winner);

        BattleService testService = new BattleService(
                new DamageCalculator(),
                new BattleReporter(),
                mockRepo,     // mock!
                mockNotif     // mock!
        );

        Ship s1 = new Ship("Test Ship 1", 10, 50, 100);
        Ship s2 = new Ship("Test Ship 2", 8, 40, 80);

        testService.fight(s1, s2);
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * ROZWIĄZANIE 5: Code Review - odpowiedzi
     * ════════════════════════════════════════════════════════════════
     */
    public void solution5_codeReview() {
        System.out.println("\n=== ROZWIĄZANIE 5: Code Review ===\n");

        System.out.println("A) Code Smells w PirateManager:");
        System.out.println("   🔴 Long Method - hire() robi zbyt dużo");
        System.out.println("   🔴 God Class - jedna klasa obsługuje wszystko");
        System.out.println("   🔴 Duplicate Code - podobny kod dla każdej roli");
        System.out.println("   🔴 Feature Envy - metoda wie za dużo o innych obiektach");
        System.out.println("   🔴 Switch Statements - długi if/else");

        System.out.println("\nB) Naruszona zasada SOLID:");
        System.out.println("   ❌ Open/Closed Principle");
        System.out.println("   Dodanie nowej roli (np. 'surgeon') wymaga MODYFIKACJI");
        System.out.println("   metody hire() zamiast ROZSZERZENIA kodu.");

        System.out.println("\nC) Refactoring - Strategy Pattern:");
        System.out.println("");
        System.out.println("   interface HiringStrategy {");
        System.out.println("       void hire(String name, String ship);");
        System.out.println("   }");
        System.out.println("");
        System.out.println("   class CaptainHiring implements HiringStrategy { ... }");
        System.out.println("   class NavigatorHiring implements HiringStrategy { ... }");
        System.out.println("   class CookHiring implements HiringStrategy { ... }");
        System.out.println("");
        System.out.println("   class PirateManager {");
        System.out.println("       private Map<String, HiringStrategy> strategies;");
        System.out.println("");
        System.out.println("       void hire(String role, String name, String ship) {");
        System.out.println("           strategies.get(role).hire(name, ship);");
        System.out.println("       }");
        System.out.println("   }");
        System.out.println("");
        System.out.println("   // Dodanie nowej roli = nowa klasa, BEZ modyfikacji PirateManager!");
        System.out.println("   class SurgeonHiring implements HiringStrategy { ... }");
        System.out.println("   strategies.put(\"surgeon\", new SurgeonHiring());");
    }

    /**
     * Uruchom wszystkie rozwiązania.
     */
    public static void main(String[] args) {
        RefactoringExercisesSolutions solutions = new RefactoringExercisesSolutions();

        solutions.solution1_extractMethod();
        solutions.solution2_extractClass();
        solutions.solution3_magicNumbers();
        solutions.solution4_dependencyInversion();
        solutions.solution5_codeReview();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ Wszystkie rozwiązania refactoringu wykonane!");
        System.out.println("=".repeat(60));
    }
}
