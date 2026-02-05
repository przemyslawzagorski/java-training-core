package pl.przemekzagorski.training.refactoring.smells.before;

import java.math.BigDecimal;
import java.util.List;

/**
 * ❌ ZŁY KOD - wiele code smells!
 *
 * Znajdź problemy:
 * 1. Long Method - metoda processBattle() robi ZA DUŻO
 * 2. God Class - klasa wie o wszystkim
 * 3. Magic Numbers - co oznacza 0.1, 0.15, 100?
 * 4. Duplicate Code - powtarzający się kod
 * 5. Feature Envy - metoda operuje głównie na danych Ship
 */
public class BadPirateService {

    // Wszystko w jednej klasie - ZŁE!

    public void processBattle(String ship1Name, int ship1Cannons, int ship1Crew, int ship1Health,
                              String ship2Name, int ship2Cannons, int ship2Crew, int ship2Health) {

        // Oblicz obrażenia statku 1
        int damage1 = ship1Cannons * 10;
        if (ship1Crew > 50) {
            damage1 = damage1 + 20;
        }
        if (ship1Crew > 100) {
            damage1 = damage1 + 30;
        }

        // Oblicz obrażenia statku 2
        int damage2 = ship2Cannons * 10;
        if (ship2Crew > 50) {
            damage2 = damage2 + 20;
        }
        if (ship2Crew > 100) {
            damage2 = damage2 + 30;
        }

        // Zastosuj obrażenia
        int newHealth1 = ship1Health - damage2;
        int newHealth2 = ship2Health - damage1;

        // Sprawdź zwycięzcę
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

        // Oblicz łupy (copy-paste!)
        BigDecimal loot = BigDecimal.ZERO;
        if (winner.equals(ship1Name)) {
            loot = new BigDecimal(ship2Crew * 100);
            loot = loot.multiply(new BigDecimal("0.1"));
            if (ship2Cannons > 20) {
                loot = loot.add(new BigDecimal("500"));
            }
        } else if (winner.equals(ship2Name)) {
            loot = new BigDecimal(ship1Crew * 100);
            loot = loot.multiply(new BigDecimal("0.1"));
            if (ship1Cannons > 20) {
                loot = loot.add(new BigDecimal("500"));
            }
        }

        // Wyświetl raport
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
        System.out.println("Łupy: " + loot);

        // Zapisz do bazy... (kolejna odpowiedzialność!)
        saveToDatabase(winner, loot);

        // Wyślij powiadomienie... (kolejna odpowiedzialność!)
        sendNotification(winner);
    }

    private void saveToDatabase(String winner, BigDecimal loot) {
        System.out.println("Zapisuję do bazy: " + winner + " zdobył " + loot);
    }

    private void sendNotification(String winner) {
        System.out.println("Wysyłam powiadomienie o zwycięstwie: " + winner);
    }
}

