package pl.przemekzagorski.training.patterns.observer;

/**
 * Demonstracja wzorca Observer.
 *
 * ╔═══════════════════════════════════════════════════════════════════╗
 * ║                      OBSERVER PATTERN                            ║
 * ╠═══════════════════════════════════════════════════════════════════╣
 * ║  PROBLEM:                                                        ║
 * ║    Jeden obiekt (Subject) musi powiadamiać wiele obiektów        ║
 * ║    (Observers) o zmianie stanu.                                  ║
 * ║                                                                  ║
 * ║  ROZWIĄZANIE:                                                    ║
 * ║    Subject trzyma listę Observers i wywołuje ich metodę          ║
 * ║    update() gdy coś się zmienia.                                 ║
 * ║                                                                  ║
 * ║  PRZYKŁADY W JAVIE/FRAMEWORKACH:                                 ║
 * ║    - java.util.Observer (deprecated, ale koncepcja żyje)         ║
 * ║    - Swing: ActionListener, MouseListener                        ║
 * ║    - Spring: ApplicationEventPublisher                           ║
 * ║    - JavaScript: addEventListener                                 ║
 * ╚═══════════════════════════════════════════════════════════════════╝
 */
public class ObserverDemo {

    public static void main(String[] args) {
        System.out.println("🏴‍☠️ OBSERVER PATTERN - Kapitan i załoga");
        System.out.println("=".repeat(55) + "\n");

        // 1️⃣ Tworzymy kapitana (Subject)
        Captain jackSparrow = new Captain("Jack Sparrow");

        // 2️⃣ Tworzymy członków załogi (Observers)
        Gunner gunner = new Gunner("Will Turner");
        Navigator navigator = new Navigator("Gibbs");
        Cook cook = new Cook("Pintel");

        // 3️⃣ Załoga rejestruje się u kapitana
        System.out.println("📋 Rekrutacja załogi:");
        jackSparrow.addObserver(gunner);
        jackSparrow.addObserver(navigator);
        jackSparrow.addObserver(cook);

        // 4️⃣ Kapitan ogłasza wydarzenia
        System.out.println("\n" + "=".repeat(55));
        System.out.println("SCENARIUSZ: Bitwa morska!");
        System.out.println("=".repeat(55));

        jackSparrow.spotEnemy("Okręt Royal Navy na prawej burcie!");

        System.out.println("\n" + "=".repeat(55));
        System.out.println("SCENARIUSZ: Znaleziono skarb!");
        System.out.println("=".repeat(55));

        jackSparrow.findTreasure("Skrzynia ze złotem na wyspie!");

        // 5️⃣ Kucharz opuszcza statek
        System.out.println("\n" + "=".repeat(55));
        System.out.println("Kucharz odchodzi ze statku...");
        jackSparrow.removeObserver(cook);

        System.out.println("\n" + "=".repeat(55));
        System.out.println("SCENARIUSZ: Odwrót!");
        System.out.println("=".repeat(55));

        jackSparrow.orderRetreat();

        System.out.println("\n" + "=".repeat(55));
        System.out.println("✅ KORZYŚCI OBSERVER:");
        System.out.println("   - Kapitan NIE zna szczegółów implementacji załogi");
        System.out.println("   - Możesz dodawać/usuwać obserwatorów dynamicznie");
        System.out.println("   - Łatwe testowanie (mock observers)");
        System.out.println("\n🎯 UŻYJ GDY:");
        System.out.println("   - Zmiana w jednym obiekcie wymaga akcji w innych");
        System.out.println("   - Nie wiesz z góry ILE obiektów będzie reagować");
        System.out.println("   - Chcesz luźne powiązanie (loose coupling)");
    }
}
