package pl.przemekzagorski.training.refactoring;

/**
 * ╔═══════════════════════════════════════════════════════════════════╗
 * ║                ĆWICZENIA - REFACTORING & SOLID                   ║
 * ╠═══════════════════════════════════════════════════════════════════╣
 * ║  Czas: 30 minut                                                  ║
 * ║  Poziom: Średniozaawansowany                                     ║
 * ╚═══════════════════════════════════════════════════════════════════╝
 *
 * Praktyczne ćwiczenia z naprawiania złego kodu!
 */
public class RefactoringExercises {

    /**
     * ════════════════════════════════════════════════════════════════
     * ĆWICZENIE 1: Extract Method
     * ════════════════════════════════════════════════════════════════
     *
     * KONTEKST:
     * Poniższa metoda robi ZA DUŻO rzeczy. Podziel ją na mniejsze metody.
     *
     * ZADANIE:
     * 1. Wyodrębnij metodę calculateDamage(cannons, crew)
     * 2. Wyodrębnij metodę determineWinner(health1, health2)
     * 3. Wyodrębnij metodę printReport(...)
     *
     * PRZED:
     *   metoda 50 linii
     *
     * PO:
     *   4 metody po 10-15 linii każda
     *
     * TIP: W IDE użyj Refactor → Extract Method (Ctrl+Alt+M)
     */
    public void exercise1_extractMethod() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎯 ĆWICZENIE 1: Extract Method - Long Method");
        System.out.println("=".repeat(60));

        System.out.println("\n❌ ZŁY KOD - jedna długa metoda (50+ linii):\n");

        // Uruchom długą metodę - student WIDZI problem
        processBattle_LongMethod("Black Pearl", 15, 120, 200,
                "Flying Dutchman", 20, 80, 180);

        System.out.println("\n" + "─".repeat(60));
        System.out.println("💡 ZADANIE:");
        System.out.println("Zrefaktoruj metodę processBattle_LongMethod():");
        System.out.println("  1. Wyodrębnij calculateDamage(cannons, crew)");
        System.out.println("  2. Wyodrębnij determineWinner(health1, health2, ...)");
        System.out.println("  3. Wyodrębnij printReport(...)");
        System.out.println("\n🎯 CEL: 4 metody po 10-15 linii zamiast 1 metody 50 linii");
        System.out.println("🆘 Rozwiązanie: RefactoringExercisesSolutions.solution1_extractMethod()");
        System.out.println("=".repeat(60));
    }

    /**
     * ❌ ZŁY KOD - LONG METHOD (50+ linii)
     * Problem: Robi ZA DUŻO rzeczy w jednej metodzie!
     */
    private void processBattle_LongMethod(String ship1Name, int ship1Cannons, int ship1Crew, int ship1Health,
                                          String ship2Name, int ship2Cannons, int ship2Crew, int ship2Health) {

        // 1. Oblicz obrażenia statku 1 (DUPLICATE CODE!)
        int damage1 = ship1Cannons * 10;  // MAGIC NUMBER!
        if (ship1Crew > 50) {
            damage1 = damage1 + 20;
        }
        if (ship1Crew > 100) {
            damage1 = damage1 + 30;
        }

        // 2. Oblicz obrażenia statku 2 (DUPLICATE CODE!)
        int damage2 = ship2Cannons * 10;  // MAGIC NUMBER!
        if (ship2Crew > 50) {
            damage2 = damage2 + 20;
        }
        if (ship2Crew > 100) {
            damage2 = damage2 + 30;
        }

        // 3. Zastosuj obrażenia
        int newHealth1 = ship1Health - damage2;
        int newHealth2 = ship2Health - damage1;

        // 4. Sprawdź zwycięzcę (LONG IF/ELSE!)
        String winner;
        if (newHealth1 <= 0 && newHealth2 <= 0) {
            winner = "Remis - oba statki zatonęły!";
        } else if (newHealth1 <= 0) {
            winner = ship2Name;
        } else if (newHealth2 <= 0) {
            winner = ship1Name;
        } else {
            if (newHealth1 > newHealth2) {
                winner = ship1Name;
            } else {
                winner = ship2Name;
            }
        }

        // 5. Wyświetl raport (KOLEJNA ODPOWIEDZIALNOŚĆ!)
        System.out.println("=== RAPORT BITWY ===");
        System.out.println("Statek 1: " + ship1Name);
        System.out.println("  Armaty: " + ship1Cannons);
        System.out.println("  Załoga: " + ship1Crew);
        System.out.println("  Zdrowie przed: " + ship1Health);
        System.out.println("  Zdrowie po: " + newHealth1);
        System.out.println("Statek 2: " + ship2Name);
        System.out.println("  Armaty: " + ship2Cannons);
        System.out.println("  Załoga: " + ship2Crew);
        System.out.println("  Zdrowie przed: " + ship2Health);
        System.out.println("  Zdrowie po: " + newHealth2);
        System.out.println("ZWYCIĘZCA: " + winner);

        System.out.println("\n🔴 Problemy:");
        System.out.println("  • Metoda ma 50+ linii (powinna < 20)");
        System.out.println("  • Robi 5 rzeczy naraz (oblicza, wyświetla, logika)");
        System.out.println("  • Duplicate code (obliczanie obrażeń 2x)");
        System.out.println("  • Magic numbers (10, 50, 100, 20, 30)");
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * ĆWICZENIE 2: Extract Class (usuń God Class)
     * ════════════════════════════════════════════════════════════════
     *
     * KONTEKST:
     * BadPirateService robi ZA DUŻO:
     * - Oblicza obrażenia
     * - Wyświetla raport
     * - Zapisuje do bazy
     * - Wysyła powiadomienia
     *
     * ZADANIE:
     * Rozbij na osobne klasy (Single Responsibility!):
     *
     * 1. Ship - encja statku (name, cannons, crew, health)
     * 2. DamageCalculator - obliczanie obrażeń
     * 3. BattleService - logika bitwy
     * 4. BattleReporter - wyświetlanie raportu
     * 5. BattleRepository - zapis do bazy (interfejs!)
     * 6. NotificationService - powiadomienia (interfejs!)
     *
     * DIAGRAM:
     *
     *   BattleService
     *        │
     *   ┌────┴─────┬──────────┬─────────────┐
     *   │          │          │             │
     *   v          v          v             v
     * Calculator  Reporter  Repository  Notification
     */
    public void exercise2_extractClass() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎯 ĆWICZENIE 2: Extract Class - God Class");
        System.out.println("=".repeat(60));

        System.out.println("\n❌ ZŁY KOD - jedna klasa robi WSZYSTKO:\n");

        // Uruchom God Class - student WIDZI że robi za dużo
        GodClassBattleService godClass = new GodClassBattleService();
        godClass.processBattle("Black Pearl", 15, 120, 200,
                "Flying Dutchman", 20, 80, 180);

        System.out.println("\n" + "─".repeat(60));
        System.out.println("💡 ZADANIE:");
        System.out.println("Rozbij GodClassBattleService na osobne klasy:");
        System.out.println("  1. Ship - encja statku (name, cannons, crew, health)");
        System.out.println("  2. DamageCalculator - obliczanie obrażeń");
        System.out.println("  3. BattleService - logika bitwy");
        System.out.println("  4. BattleReporter - wyświetlanie raportu");
        System.out.println("  5. BattleRepository - zapis do bazy (interfejs!)");
        System.out.println("\n🎯 CEL: Każda klasa = JEDNA odpowiedzialność (Single Responsibility)");
        System.out.println("🆘 Rozwiązanie: RefactoringExercisesSolutions.solution2_extractClass()");
        System.out.println("=".repeat(60));
    }

    /**
     * ❌ ZŁY KOD - GOD CLASS
     * Problem: Jedna klasa robi WSZYSTKO (naruszenie Single Responsibility)
     */
    private static class GodClassBattleService {

        public void processBattle(String ship1Name, int ship1Cannons, int ship1Crew, int ship1Health,
                                  String ship2Name, int ship2Cannons, int ship2Crew, int ship2Health) {

            // ODPOWIEDZIALNOŚĆ 1: Obliczanie obrażeń
            int damage1 = ship1Cannons * 10;
            if (ship1Crew > 50) damage1 += 20;
            if (ship1Crew > 100) damage1 += 30;

            int damage2 = ship2Cannons * 10;
            if (ship2Crew > 50) damage2 += 20;
            if (ship2Crew > 100) damage2 += 30;

            int newHealth1 = ship1Health - damage2;
            int newHealth2 = ship2Health - damage1;

            // ODPOWIEDZIALNOŚĆ 2: Logika biznesowa (określanie zwycięzcy)
            String winner;
            if (newHealth1 <= 0 && newHealth2 <= 0) {
                winner = "Remis";
            } else if (newHealth1 <= 0) {
                winner = ship2Name;
            } else if (newHealth2 <= 0) {
                winner = ship1Name;
            } else {
                winner = newHealth1 > newHealth2 ? ship1Name : ship2Name;
            }

            // ODPOWIEDZIALNOŚĆ 3: Wyświetlanie (prezentacja)
            System.out.println("=== RAPORT ===");
            System.out.println(ship1Name + ": " + ship1Health + " → " + newHealth1);
            System.out.println(ship2Name + ": " + ship2Health + " → " + newHealth2);
            System.out.println("Zwycięzca: " + winner);

            // ODPOWIEDZIALNOŚĆ 4: Zapis do bazy (persistence)
            saveToDatabase(winner);

            // ODPOWIEDZIALNOŚĆ 5: Powiadomienia (notification)
            sendNotification(winner);

            System.out.println("\n🔴 Problemy:");
            System.out.println("  • Klasa ma 5 odpowiedzialności (powinna mieć 1!)");
            System.out.println("  • Trudno testować (wszystko w jednym miejscu)");
            System.out.println("  • Trudno zmienić (np. zmiana sposobu zapisu do bazy)");
            System.out.println("  • Naruszenie Single Responsibility Principle");
        }

        private void saveToDatabase(String winner) {
            System.out.println("  [DB] Zapisuję: " + winner);
        }

        private void sendNotification(String winner) {
            System.out.println("  [EMAIL] Wysyłam powiadomienie: " + winner);
        }
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * ĆWICZENIE 3: Replace Magic Numbers
     * ════════════════════════════════════════════════════════════════
     *
     * KONTEKST:
     * W kodzie są "magiczne liczby": 10, 50, 100, 20, 30, 0.1, 500...
     * Co one oznaczają? Nikt nie wie bez komentarza!
     *
     * ZADANIE:
     * Zamień na stałe z opisowymi nazwami:
     *
     *   private static final int DAMAGE_PER_CANNON = 10;
     *   private static final int MEDIUM_CREW_THRESHOLD = 50;
     *   private static final int LARGE_CREW_THRESHOLD = 100;
     *   private static final int MEDIUM_CREW_BONUS = 20;
     *   private static final int LARGE_CREW_BONUS = 30;
     *
     * BONUS: Wynieś do klasy BattleConfig lub enum
     */
    public void exercise3_removeMagicNumbers() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎯 ĆWICZENIE 3: Replace Magic Numbers");
        System.out.println("=".repeat(60));

        System.out.println("\n❌ ZŁY KOD - magiczne liczby:\n");

        // Uruchom kod z magic numbers
        int damage = calculateDamage_WithMagicNumbers(10, 120);
        System.out.println("Obrażenia dla 10 armat i 120 załogi: " + damage);

        System.out.println("\n🤔 Co oznaczają te liczby?");
        System.out.println("  • 10 - ???");
        System.out.println("  • 50 - ???");
        System.out.println("  • 100 - ???");
        System.out.println("  • 20 - ???");
        System.out.println("  • 30 - ???");

        System.out.println("\n" + "─".repeat(60));
        System.out.println("💡 ZADANIE:");
        System.out.println("Zamień magiczne liczby na stałe z opisowymi nazwami:");
        System.out.println("  private static final int DAMAGE_PER_CANNON = 10;");
        System.out.println("  private static final int MEDIUM_CREW_THRESHOLD = 50;");
        System.out.println("  private static final int LARGE_CREW_THRESHOLD = 100;");
        System.out.println("  private static final int MEDIUM_CREW_BONUS = 20;");
        System.out.println("  private static final int LARGE_CREW_BONUS = 30;");
        System.out.println("\n🎯 CEL: Kod samodokumentujący się (bez komentarzy!)");
        System.out.println("🆘 Rozwiązanie: RefactoringExercisesSolutions.solution3_magicNumbers()");
        System.out.println("=".repeat(60));
    }

    /**
     * ❌ ZŁY KOD - MAGIC NUMBERS
     * Problem: Co oznaczają liczby 10, 50, 100, 20, 30? 🤔
     */
    private int calculateDamage_WithMagicNumbers(int cannons, int crew) {
        int damage = cannons * 10;  // Co oznacza 10? 🤔

        if (crew > 50) {  // Co oznacza 50? 🤔
            damage += 20;  // Co oznacza 20? 🤔
        }

        if (crew > 100) {  // Co oznacza 100? 🤔
            damage += 30;  // Co oznacza 30? 🤔
        }

        System.out.println("  Kod: damage = cannons * 10");
        System.out.println("       if (crew > 50) damage += 20");
        System.out.println("       if (crew > 100) damage += 30");

        System.out.println("\n🔴 Problemy:");
        System.out.println("  • Nikt nie wie co oznaczają liczby bez komentarza");
        System.out.println("  • Trudno zmienić (trzeba szukać we wszystkich miejscach)");
        System.out.println("  • Łatwo pomylić (czy 50 to próg czy bonus?)");

        return damage;
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * ĆWICZENIE 4: SOLID - Dependency Inversion
     * ════════════════════════════════════════════════════════════════
     *
     * KONTEKST:
     * BattleService bezpośrednio wywołuje:
     *   - saveToDatabase() - co jeśli chcemy MongoDB zamiast SQL?
     *   - sendNotification() - co jeśli email zamiast push?
     *
     * PROBLEM:
     * Klasa jest ZALEŻNA od konkretnych implementacji (tight coupling).
     *
     * ROZWIĄZANIE (Dependency Inversion):
     * 1. Stwórz interfejsy: BattleRepository, NotificationService
     * 2. BattleService przyjmuje interfejsy w konstruktorze
     * 3. Konkretne implementacje wstrzykiwane z zewnątrz
     *
     * DIAGRAM:
     *
     *   BattleService ──────► BattleRepository (interfejs)
     *                                 △
     *                         ┌───────┴───────┐
     *                         │               │
     *                    SqlRepository    MongoRepository
     */
    public void exercise4_dependencyInversion() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎯 ĆWICZENIE 4: Dependency Inversion (SOLID)");
        System.out.println("=".repeat(60));

        System.out.println("\n❌ ZŁY KOD - tight coupling (zależność od konkretnych klas):\n");

        // Uruchom kod z tight coupling
        TightlyCoupledBattleService tightService = new TightlyCoupledBattleService();
        tightService.processBattle("Black Pearl", "Flying Dutchman");

        System.out.println("\n" + "─".repeat(60));
        System.out.println("💡 ZADANIE:");
        System.out.println("Zastosuj Dependency Inversion Principle:");
        System.out.println("  1. Stwórz interfejs BattleRepository");
        System.out.println("  2. Stwórz interfejs NotificationService");
        System.out.println("  3. BattleService przyjmuje interfejsy w konstruktorze");
        System.out.println("  4. Wstrzykuj konkretne implementacje z zewnątrz");
        System.out.println("\n🎯 CEL: Loose coupling - łatwe testowanie i zmiana implementacji");
        System.out.println("🆘 Rozwiązanie: RefactoringExercisesSolutions.solution4_dependencyInversion()");
        System.out.println("=".repeat(60));
    }

    /**
     * ❌ ZŁY KOD - TIGHT COUPLING
     * Problem: Bezpośrednia zależność od konkretnych klas (MySqlRepository, EmailService)
     */
    private static class TightlyCoupledBattleService {

        // TIGHT COUPLING - bezpośrednie tworzenie obiektów!
        private final MySqlRepository repository = new MySqlRepository();
        private final EmailService emailService = new EmailService();

        public void processBattle(String ship1, String ship2) {
            String winner = ship1; // uproszczenie

            System.out.println("Bitwa: " + ship1 + " vs " + ship2);
            System.out.println("Zwycięzca: " + winner);

            // Bezpośrednie wywołanie konkretnych klas
            repository.saveToMySql(winner);
            emailService.sendEmail(winner);

            System.out.println("\n🔴 Problemy:");
            System.out.println("  • Nie można zmienić implementacji (np. MySQL → MongoDB)");
            System.out.println("  • Nie można przetestować (nie da się użyć mocków)");
            System.out.println("  • Tight coupling - klasa zna konkretne implementacje");
            System.out.println("  • Naruszenie Dependency Inversion Principle");
        }
    }

    // Konkretne klasy (tight coupling!)
    private static class MySqlRepository {
        public void saveToMySql(String winner) {
            System.out.println("  [MySQL] Zapisuję do bazy: " + winner);
        }
    }

    private static class EmailService {
        public void sendEmail(String winner) {
            System.out.println("  [Email] Wysyłam email: " + winner);
        }
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * ĆWICZENIE 5: Code Review - znajdź smells!
     * ════════════════════════════════════════════════════════════════
     *
     * Przeanalizuj poniższy kod i odpowiedz na pytania:
     *
     * ```java
     * public class PirateManager {
     *     public void hire(String name, String role, int exp, String ship) {
     *         if (role.equals("captain")) {
     *             System.out.println("Welcome Captain " + name);
     *             // sprawdź czy statek ma kapitana
     *             // ustaw nowego kapitana
     *             // zaktualizuj bazę
     *             // wyślij email do załogi
     *         } else if (role.equals("navigator")) {
     *             System.out.println("Welcome Navigator " + name);
     *             // sprawdź doświadczenie
     *             // przypisz do statku
     *             // zaktualizuj bazę
     *             // wyślij email
     *         } else if (role.equals("cook")) {
     *             // ...podobny kod...
     *         }
     *         // ...10 kolejnych ról...
     *     }
     * }
     * ```
     *
     * PYTANIA:
     * A) Jakie code smells widzisz? (wymień min. 3)
     * B) Którą zasadę SOLID narusza ten kod?
     * C) Jak zrefaktorowałbyś ten kod? (użyj wzorca!)
     *
     * ODPOWIEDZI:
     * A) Long Method, God Class, Duplicate Code, Feature Envy
     * B) Open/Closed (dodanie nowej roli = modyfikacja metody)
     * C) Strategy Pattern lub Factory + polimorfizm
     */
    public void exercise5_codeReview() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎯 ĆWICZENIE 5: Code Review - Znajdź Code Smells");
        System.out.println("=".repeat(60));

        System.out.println("\n📋 QUIZ: Przeanalizuj poniższy kod i odpowiedz na pytania:\n");

        System.out.println("❌ ZŁY KOD:");
        System.out.println("─".repeat(60));
        System.out.println("""
            public class PirateManager {
                public void hire(String name, String role, int exp, String ship) {
                    if (role.equals("captain")) {
                        System.out.println("Welcome Captain " + name);
                        // sprawdź czy statek ma kapitana
                        // ustaw nowego kapitana
                        // zaktualizuj bazę
                        // wyślij email do załogi
                    } else if (role.equals("navigator")) {
                        System.out.println("Welcome Navigator " + name);
                        // sprawdź doświadczenie
                        // przypisz do statku
                        // zaktualizuj bazę
                        // wyślij email
                    } else if (role.equals("cook")) {
                        // ...podobny kod...
                    }
                    // ...10 kolejnych ról...
                }
            }
            """);
        System.out.println("─".repeat(60));

        System.out.println("\n💡 PYTANIA:");
        System.out.println("  A) Jakie code smells widzisz? (wymień min. 3)");
        System.out.println("  B) Którą zasadę SOLID narusza ten kod?");
        System.out.println("  C) Jak zrefaktorowałbyś ten kod? (użyj wzorca!)");

        System.out.println("\n🔍 ODPOWIEDZI:");
        System.out.println("  A) Code smells:");
        System.out.println("     • Long Method (metoda robi za dużo)");
        System.out.println("     • God Class (jedna klasa zarządza wszystkim)");
        System.out.println("     • Duplicate Code (każda rola ma podobny kod)");
        System.out.println("     • Feature Envy (logika ról powinna być w klasach ról)");
        System.out.println("     • Primitive Obsession (String role zamiast obiektu Role)");
        System.out.println("  B) SOLID:");
        System.out.println("     • Open/Closed - dodanie nowej roli = modyfikacja metody");
        System.out.println("     • Single Responsibility - klasa robi za dużo");
        System.out.println("  C) Refaktoring:");
        System.out.println("     • Strategy Pattern (każda rola = osobna strategia)");
        System.out.println("     • Factory Pattern (tworzenie ról)");
        System.out.println("     • Polimorfizm (Role interface + Captain, Navigator, Cook)");

        System.out.println("\n📚 BONUS: Otwórz smells/before/BadPirateService.java");
        System.out.println("   i znajdź wszystkie te problemy w prawdziwym kodzie!");

        System.out.println("\n🎯 CEL: Rozpoznawanie code smells to pierwszy krok do refaktoringu!");
        System.out.println("🆘 Rozwiązanie: RefactoringExercisesSolutions.solution5_codeReview()");
        System.out.println("=".repeat(60));
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * ZADANIE DOMOWE: Refaktoruj BadPirateService
     * ════════════════════════════════════════════════════════════════
     *
     * Otwórz plik: smells/before/BadPirateService.java
     * Stwórz kopię w: smells/after/GoodBattleService.java
     *
     * Zastosuj WSZYSTKIE techniki:
     * 1. Extract Method
     * 2. Extract Class
     * 3. Remove Magic Numbers
     * 4. Dependency Inversion (interfejsy)
     * 5. Single Responsibility
     *
     * Finalny kod powinien mieć:
     * - Klasy < 100 linii
     * - Metody < 20 linii
     * - Brak magic numbers
     * - Interfejsy dla zależności zewnętrznych
     */

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════════╗");
        System.out.println("║                ĆWICZENIA - REFACTORING & SOLID                   ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════╝");

        RefactoringExercises exercises = new RefactoringExercises();

        // Uruchom wszystkie ćwiczenia
        exercises.exercise1_extractMethod();
        exercises.exercise2_extractClass();
        exercises.exercise3_removeMagicNumbers();
        exercises.exercise4_dependencyInversion();
        exercises.exercise5_codeReview();

        System.out.println("\n╔═══════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    PODSUMOWANIE                                   ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════╝");
        System.out.println("\n✅ Wszystkie ćwiczenia wykonane!");
        System.out.println("\n📚 NASTĘPNE KROKI:");
        System.out.println("  1. Zrefaktoruj kod w każdym ćwiczeniu");
        System.out.println("  2. Porównaj z RefactoringExercisesSolutions.java");
        System.out.println("  3. Uruchom testy: RefactoringTest.java");
        System.out.println("  4. BONUS: Zrefaktoruj smells/before/BadPirateService.java");
        System.out.println("\n🎯 Pamiętaj: Refaktoring = małe kroki + testy po każdej zmianie!");
    }
}
