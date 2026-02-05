package pl.przemekzagorski.training.refactoring.solid;

/**
 * Demonstracja zasad SOLID z pirackimi przykładami.
 */
public class SolidDemo {

    public static void main(String[] args) {
        System.out.println("🏴‍☠️ SOLID Principles Demo");
        System.out.println("===========================\n");

        demonstrateSRP();
        demonstrateOCP();
        demonstrateLSP();
        demonstrateISP();
        demonstrateDIP();
    }

    /**
     * S - Single Responsibility Principle
     * Klasa powinna mieć tylko jeden powód do zmiany.
     */
    private static void demonstrateSRP() {
        System.out.println("📌 S - Single Responsibility Principle\n");

        System.out.println("""
            ❌ ZŁE - klasa robi za dużo:
            
            class Ship {
                void sail() { }
                void attack() { }
                void saveToDatabase() { }      // Zapis do bazy?
                void printReport() { }          // Drukowanie?
                void sendNotification() { }     // Powiadomienia?
            }
            
            ✅ DOBRE - rozdzielone odpowiedzialności:
            
            class Ship {
                void sail() { }
                void attack() { }
            }
            
            class ShipRepository {
                void save(Ship ship) { }
            }
            
            class ShipReporter {
                void print(Ship ship) { }
            }
            
            class NotificationService {
                void notify(String message) { }
            }
            
            💡 Każda klasa ma JEDEN powód do zmiany!
            """);
    }

    /**
     * O - Open/Closed Principle
     * Otwarte na rozszerzenia, zamknięte na modyfikacje.
     */
    private static void demonstrateOCP() {
        System.out.println("📌 O - Open/Closed Principle\n");

        System.out.println("""
            ❌ ZŁE - musimy modyfikować klasę przy każdym nowym typie:
            
            class AttackCalculator {
                int calculate(String type) {
                    if (type.equals("cannon")) return 100;
                    if (type.equals("boarding")) return 80;
                    if (type.equals("ramming")) return 150;
                    // Nowy typ? Musimy zmienić tę klasę!
                    return 0;
                }
            }
            
            ✅ DOBRE - rozszerzamy przez nowe klasy:
            
            interface AttackStrategy {
                int calculateDamage();
            }
            
            class CannonAttack implements AttackStrategy {
                public int calculateDamage() { return 100; }
            }
            
            class BoardingAttack implements AttackStrategy {
                public int calculateDamage() { return 80; }
            }
            
            // Nowy typ? Nowa klasa - bez zmiany istniejącego kodu!
            class TorpedoAttack implements AttackStrategy {
                public int calculateDamage() { return 200; }
            }
            
            💡 Rozszerzamy funkcjonalność BEZ modyfikacji istniejącego kodu!
            """);
    }

    /**
     * L - Liskov Substitution Principle
     * Podklasa może zastąpić klasę bazową bez zmiany zachowania programu.
     */
    private static void demonstrateLSP() {
        System.out.println("📌 L - Liskov Substitution Principle\n");

        System.out.println("""
            ❌ ZŁE - podklasa zmienia oczekiwane zachowanie:
            
            class Ship {
                void sail() { System.out.println("Płynę!"); }
            }
            
            class SunkenShip extends Ship {
                void sail() { 
                    throw new RuntimeException("Nie mogę płynąć - zatonąłem!");
                }
            }
            
            // Kod który oczekuje Ship może się wysypać!
            void startJourney(Ship ship) {
                ship.sail();  // BOOM! dla SunkenShip
            }
            
            ✅ DOBRE - podklasy zachowują się zgodnie z kontraktem:
            
            interface Sailable {
                void sail();
            }
            
            class Ship implements Sailable {
                void sail() { System.out.println("Płynę!"); }
            }
            
            class SunkenShip {  // NIE implementuje Sailable!
                void salvage() { System.out.println("Wydobywam wrak"); }
            }
            
            💡 Jeśli coś nie może sail(), nie powinno dziedziczyć po Ship!
            """);
    }

    /**
     * I - Interface Segregation Principle
     * Lepiej wiele małych interfejsów niż jeden wielki.
     */
    private static void demonstrateISP() {
        System.out.println("📌 I - Interface Segregation Principle\n");

        System.out.println("""
            ❌ ZŁE - wielki interfejs wymusza niepotrzebne implementacje:
            
            interface Ship {
                void sail();
                void attack();
                void dive();      // Nie każdy statek nurkuje!
                void fly();       // Latający statek?!
            }
            
            class Galleon implements Ship {
                void sail() { ... }
                void attack() { ... }
                void dive() { throw new UnsupportedOperationException(); }
                void fly() { throw new UnsupportedOperationException(); }
            }
            
            ✅ DOBRE - małe, specjalizowane interfejsy:
            
            interface Sailable {
                void sail();
            }
            
            interface Armed {
                void attack();
            }
            
            interface Submersible {
                void dive();
            }
            
            class Galleon implements Sailable, Armed {
                void sail() { ... }
                void attack() { ... }
            }
            
            class Submarine implements Sailable, Armed, Submersible {
                void sail() { ... }
                void attack() { ... }
                void dive() { ... }
            }
            
            💡 Klasa implementuje tylko to, czego naprawdę potrzebuje!
            """);
    }

    /**
     * D - Dependency Inversion Principle
     * Zależności od abstrakcji, nie od konkretnych implementacji.
     */
    private static void demonstrateDIP() {
        System.out.println("📌 D - Dependency Inversion Principle\n");

        System.out.println("""
            ❌ ZŁE - zależność od konkretnej klasy:
            
            class BattleService {
                private MySqlDatabase database = new MySqlDatabase();
                private EmailNotifier notifier = new EmailNotifier();
                
                void processBattle() {
                    // ...
                    database.save(result);
                    notifier.send(message);
                }
            }
            
            // Jak przetestować? Musimy mieć MySQL i serwer email!
            
            ✅ DOBRE - zależność od abstrakcji (interfejsów):
            
            class BattleService {
                private final Database database;      // Interfejs!
                private final Notifier notifier;      // Interfejs!
                
                // Dependency Injection przez konstruktor
                BattleService(Database db, Notifier notifier) {
                    this.database = db;
                    this.notifier = notifier;
                }
            }
            
            // W produkcji:
            new BattleService(new MySqlDatabase(), new EmailNotifier());
            
            // W testach:
            new BattleService(new MockDatabase(), new MockNotifier());
            
            💡 Łatwe testowanie, łatwa zmiana implementacji!
            """);
    }
}

