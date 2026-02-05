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
        // TODO: Przenieś ten kod do nowej klasy i zrefaktoruj
        //
        // ORYGINALNY KOD (smells/before/BadPirateService.java):
        //
        // public void processBattle(...) {
        //     // 1. Oblicz damage - WYODRĘBNIJ
        //     int damage1 = ship1Cannons * 10;
        //     if (ship1Crew > 50) damage1 += 20;
        //     if (ship1Crew > 100) damage1 += 30;
        //
        //     // 2. Określ winner - WYODRĘBNIJ
        //     String winner = ...
        //
        //     // 3. Wyświetl raport - WYODRĘBNIJ
        //     System.out.println(...)
        // }
        //
        // WYNIK:
        // private int calculateDamage(int cannons, int crew) {...}
        // private String determineWinner(int health1, int health2, ...) {...}
        // private void printReport(Ship s1, Ship s2, String winner) {...}
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
        // TODO: Stwórz nowe klasy i przenieś odpowiedzialności
        //
        // record Ship(String name, int cannons, int crew, int health) {}
        //
        // class DamageCalculator {
        //     int calculate(Ship ship) {
        //         int damage = ship.cannons() * 10;
        //         if (ship.crew() > 50) damage += 20;
        //         if (ship.crew() > 100) damage += 30;
        //         return damage;
        //     }
        // }
        //
        // class BattleService {
        //     private final DamageCalculator calculator;
        //     private final BattleReporter reporter;
        //     // Dependency Injection!
        // }
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
        // TODO: Zamień magiczne liczby na stałe
        //
        // PRZED:
        // int damage = cannons * 10;
        // if (crew > 50) damage += 20;
        //
        // PO:
        // int damage = cannons * DAMAGE_PER_CANNON;
        // if (crew > MEDIUM_CREW_THRESHOLD) damage += MEDIUM_CREW_BONUS;
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
        // TODO: Zaimplementuj Dependency Inversion
        //
        // interface BattleRepository {
        //     void save(BattleResult result);
        // }
        //
        // interface NotificationService {
        //     void notify(String winner);
        // }
        //
        // class BattleService {
        //     private final BattleRepository repository;
        //     private final NotificationService notifications;
        //
        //     // Constructor Injection
        //     public BattleService(BattleRepository repo, NotificationService notif) {
        //         this.repository = repo;
        //         this.notifications = notif;
        //     }
        // }
        //
        // // W testach możesz użyć mocków!
        // BattleService service = new BattleService(mockRepo, mockNotif);
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
        System.out.println("╠═══════════════════════════════════════════════════════════════════╣");
        System.out.println("║  1. Extract Method - rozbij długą metodę                         ║");
        System.out.println("║  2. Extract Class - usuń God Class                               ║");
        System.out.println("║  3. Magic Numbers - zamień na stałe                              ║");
        System.out.println("║  4. Dependency Inversion - interfejsy                            ║");
        System.out.println("║  5. Code Review - znajdź smells                                  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Otwórz smells/before/BadPirateService.java i refaktoruj!");
        System.out.println("Porównaj z smells/after/ gdy skończysz.");
    }
}
